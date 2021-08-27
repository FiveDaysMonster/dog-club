package com.example.lcd3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Button b_signIn;
    URL myUrl;
    String client_id = "160800954353-7034kreq5oaoe9il53abio1nectjfb8q.apps.googleusercontent.com";

    Integer RC_SIGN_IN = 1234, RC_SIGN_UP = 2345;

    String gender, identity;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    String TAG = "旗標";
    TextView t_alert_userName, t_alert_userEmail;

    TextView t_signUp, t_welcome;
    CircleImageView img_user_photo;

    //person
    String accountName = "", accountType = "", accountDisplayName = "", accountEmail = "", accountLevel = ""
            , accountFamilyName = "", accountGivenName = "", accountId = "", accountPhotoUrl = "",accountNickName = "";
    EditText e_userNickName;
    FirebaseDatabase firebaseDatabase;

    Boolean state_accountHasBeenCreated;
    Bitmap myBitmap;
    Integer hasGetImg = 987;
    URL url;
    Button b_logout;

    Handler handler = new Handler(){
          @Override
          public void handleMessage(Message msg) {
              super.handleMessage(msg);


              if(msg.what == hasGetImg){
                  img_user_photo.setImageBitmap(myBitmap);
                  img_user_photo.setVisibility(View.VISIBLE);
                  setNickName();

              }
              if(msg.what == 444){
                  img_user_photo.setVisibility(View.VISIBLE);
                  img_user_photo.setImageResource(R.drawable.google_plus);
                  setNickName();
              }
              if(msg.what == 333){
                  t_welcome.setText(accountNickName);
              }

              if(msg.what == 777){
                  setNickName();
                  Intent intent = new Intent(MainActivity.this,ui_level_3.class);
                  startActivity(intent);
                  /*if(accountLevel.equals("1")){
                      Intent intent = new Intent(MainActivity.this,ui_level_1.class);
                      startActivity(intent);
                  }
                  if(accountLevel.equals("2")){
                      Intent intent = new Intent(MainActivity.this,ui_level_2.class);
                      startActivity(intent);
                  }
                  if(accountLevel.equals("3")){
                      Intent intent = new Intent(MainActivity.this,ui_level_3.class);
                      startActivity(intent);
                  }

                   */
              }
          }
      };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        noStatusBar();
        init();
        bt_event();
    }



    private void init(){
        b_signIn = (Button) findViewById(R.id.b_signIn);
        t_signUp = (TextView) findViewById(R.id.t_signUp);
        firebaseDatabase = FirebaseDatabase.getInstance();
        img_user_photo = (CircleImageView) findViewById(R.id.img_userPhoto);
        t_welcome = (TextView) findViewById(R.id.t_welcomeUserName);
        b_logout = (Button) findViewById(R.id.b_logout);
    }

    private void bt_event(){
        b_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        t_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_UP);
                    }
                });

            }
        });

        b_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account == null){
            //t_welcome.setText("尚未登入");
            Toast.makeText(MainActivity.this,"尚未登入",Toast.LENGTH_SHORT).show();
        }
        else{
            updateUI(account);
            Toast.makeText(MainActivity.this,"登入成功",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(account.getPhotoUrl() != null){
                            URL myurl = new URL(account.getPhotoUrl().toString());
                            myBitmap = GetURLBitmap(myurl);
                            accountDisplayName = account.getDisplayName();
                            handler.sendEmptyMessage(hasGetImg);
                            setNickName();
                        }else{
                            accountDisplayName = account.getDisplayName();
                            handler.sendEmptyMessage(444);
                            setNickName();
                        }

                    }catch (Exception e){

                    }
                }
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task!=null){
                handleSignInResult(task);
            }
        }
        else if(requestCode == RC_SIGN_UP){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task!=null){
                handleSignUpResult(task);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            if(completedTask!=null){

                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                if(account !=null){

                    DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
                    databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Boolean state = false;
                            if(!task.isSuccessful()){

                            }else{
                                for(DataSnapshot ds : task.getResult().getChildren()){
                                    if(account.getId().equals(ds.getKey())){
                                        state = true;
                                    }
                                }
                                if(!state){
                                    Toast.makeText(MainActivity.this,"此帳號尚未註冊",Toast.LENGTH_SHORT).show();
                                    signOut();
                                }else{
                                    handler.sendEmptyMessage(777);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if(account.getPhotoUrl()!=null){
                                                    accountDisplayName = account.getDisplayName();
                                                    URL myurl = new URL(account.getPhotoUrl().toString());
                                                    System.out.println("..........................."+myurl);
                                                    myBitmap = GetURLBitmap(myurl);
                                                    handler.sendEmptyMessage(hasGetImg);
                                                }
                                                else{
                                                    accountDisplayName = account.getDisplayName();
                                                    handler.sendEmptyMessage(444);
                                                }

                                            }catch (Exception e){
                                                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+e.toString());
                                            }
                                        }
                                    }).start();
                                }
                            }
                        }
                    });

                }
            }


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


    private void updateUI(GoogleSignInAccount account){
        if(account!=null){
            System.out.println("=======================================");
            //most insure value not be null!!
            System.out.println("get Account name : " + account.getAccount().name);
            System.out.println("get Account type : " + account.getAccount().type);
            System.out.println("get Account toString : " + account.getAccount().toString());
            System.out.println("get Display name : " + account.getDisplayName());
            System.out.println("get Email : " + account.getEmail());

            System.out.println("get Family Name : " + account.getFamilyName());
            System.out.println("get Given Name : " + account.getGivenName());
            System.out.println("get ID : " + account.getId());
            System.out.println("get Id Token : " + account.getIdToken());
            System.out.println("get Type : " + account.getAccount().type);

            System.out.println("get Server Auth Code : " + account.getServerAuthCode());
            //System.out.println("get Photo Url : " + account.getPhotoUrl().toString());
            System.out.println("=======================================");

        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //img_user_photo.setImageResource(0);
                        //Toast.makeText(MainActivity.this,"sign Out",Toast.LENGTH_SHORT).show();
                    }
                });
        img_user_photo.setVisibility(View.INVISIBLE);
        t_welcome.setText("");
    }

    private void noStatusBar(){
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
    }



    private void handleSignUpResult(Task<GoogleSignInAccount> completedTask) {
        try {
            if(completedTask!=null){
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                if(account !=null){
                    //has account
                    //Toast.makeText(MainActivity.this,"此Email已被註冊過",Toast.LENGTH_SHORT).show();
                    //not create
                    //signUpUI(account);
                    DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
                    databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Boolean state = false;
                            if(!task.isSuccessful()){
                                hasCreated(account);
                            }else{
                                for(DataSnapshot ds : task.getResult().getChildren()){
                                    if(account.getId().equals(ds.getKey())){
                                        state = true;
                                    }
                                }
                                if(!state){
                                    signUpUI(account);
                                }else{
                                    Toast.makeText(MainActivity.this,"此Email已被註冊過",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            getSignUpData(null);
        }
    }

    private Boolean hasCreated(GoogleSignInAccount account){
        String myID = account.getId();
        DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    hasCreated(account);
                }else{
                    state_accountHasBeenCreated = false;
                    for(DataSnapshot ds : task.getResult().getChildren()){
                        if(myID.equals(ds.getKey())){
                            state_accountHasBeenCreated = true;
                        }
                    }
                }
            }
        });
        System.out.println("55555555555555555555"+state_accountHasBeenCreated);
        return state_accountHasBeenCreated;
    }

    private void getSignUpData(GoogleSignInAccount account){
        if(account!=null){
            if(account.getAccount().name != null){
                accountName = account.getAccount().name;
            }
            else{
                accountName = "";
            }

            if(account.getAccount().type != null){
                accountType = account.getAccount().type;
            }
            else{
                accountType = "";
            }

            if(account.getDisplayName() != null){
                accountDisplayName = account.getDisplayName();
            }
            else{
                accountDisplayName = "";
            }

            if(account.getEmail() != null){
                accountEmail = account.getEmail();
            }
            else{
                accountEmail = "";
            }

            if(account.getFamilyName() != null){
                accountFamilyName = account.getFamilyName();
            }
            else{
                accountFamilyName = "";
            }

            if(account.getGivenName() != null){
                accountGivenName = account.getGivenName();
            }
            else{
                accountGivenName = "";
            }

            if(account.getId() != null){
                accountId = account.getId();
            }
            else{
                accountId = "";
            }

            if(account.getPhotoUrl() == null){
                accountPhotoUrl = "";
            }
            else{
                accountPhotoUrl = account.getPhotoUrl().toString();
            }
        }
    }
    private void signUpUI(GoogleSignInAccount account){
        getSignUpData(account);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.alertdialog_sign_up,null);
        e_userNickName = (EditText) view.findViewById(R.id.e_userNickName);
        e_userNickName.setText(accountDisplayName);
        t_alert_userName = (TextView) view.findViewById(R.id.t_userName);
        t_alert_userName.setText(accountDisplayName);
        t_alert_userEmail = (TextView) view.findViewById(R.id.t_userEmail);
        t_alert_userEmail.setText(accountEmail);




        builder.setTitle("註冊帳號")
                .setView(view)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!e_userNickName.getText().toString().equals("")){
                            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_gender);
                            if(radioGroup.getCheckedRadioButtonId() == R.id.r_male)
                                gender = "1";
                            else if(radioGroup.getCheckedRadioButtonId() == R.id.r_female)
                                gender = "2";

                            RadioGroup radioGroup2 = (RadioGroup) view.findViewById(R.id.rg_identity);
                            if(radioGroup2.getCheckedRadioButtonId() == R.id.r_student)
                                identity = "1";
                            else if(radioGroup2.getCheckedRadioButtonId() == R.id.r_teacher)
                                identity = "2";
                            else
                                identity = "3";
                            DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
                            databaseReference.child(accountId).child("account_email").setValue(accountEmail);
                            databaseReference.child(accountId).child("account_display_name").setValue(accountDisplayName);
                            databaseReference.child(accountId).child("account_family_name").setValue(accountFamilyName);
                            databaseReference.child(accountId).child("account_given_name").setValue(accountGivenName);
                            databaseReference.child(accountId).child("account_photo_url").setValue(accountPhotoUrl);
                            databaseReference.child(accountId).child("account_level").setValue("3");
                            databaseReference.child(accountId).child("account_gender").setValue(gender);
                            databaseReference.child(accountId).child("account_identity").setValue(identity);
                            databaseReference.child(accountId).child("account_nickName").setValue(e_userNickName.getText().toString());
                            databaseReference.child(accountId).child("books").child("discover").setValue(0);
                            databaseReference.child(accountId).child("mails").child("num").setValue(0);
                            for(int ii=1;ii<=36;ii++){
                                databaseReference.child(accountId).child("books").child("b"+String.valueOf(ii)).setValue(0);
                            }
                            Toast.makeText(MainActivity.this,"註冊完成",Toast.LENGTH_SHORT).show();
                            signOut();
                        }
                        else{
                            Toast.makeText(MainActivity.this,"暱稱不可為空",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,"取消註冊",Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private static Bitmap GetURLBitmap(URL url)
    {
        try
        {
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        }
        catch (Exception e)
        {
            System.out.println("7777777777777777777777777777");
            System.out.println(e.toString());
            System.out.println("7777777777777777777777777777");
            return null;
        }
    }

    private void setNickName(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        DatabaseReference databaseReference = firebaseDatabase.getReference("accounts").child(account.getId());
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    setNickName();
                }else{
                    for(DataSnapshot ds : task.getResult().getChildren()){
                        if(ds.getKey().equals("account_nickName")){
                            accountNickName = ds.getValue().toString();
                        }
                        if(ds.getKey().equals("account_level")){
                            accountLevel = ds.getValue().toString();
                        }
                    }
                    handler.sendEmptyMessage(333);
                }
            }
        });
    }

}


