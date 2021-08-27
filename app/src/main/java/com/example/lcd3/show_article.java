package com.example.lcd3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class show_article extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase firebaseDatabase;

    String userID;
    String userNickName;
    Button b_leave_message;
    CircleImageView img_user_photo;

    Integer hasGetImg = 987, noImg = 444;
    Bitmap myBitmap, authorBitmap;

    TextView title,time,content,tag,name, t_leaveMessage;
    String timestamp ="";
    String year="",month="",day="",hour="",minute="",articleURL = "";

    String articleID;

    Integer setUserImage = 183,author_no_img = 888, author_has_img = 478
            ,setImage = 83838, leave_message;

    ArrayList<String> al_name, al_image, al_time, al_message;
    ArrayList<Bitmap> al_bitmap;

    //ListView listView;
    MyListView listView;
    Integer myPosition = 0;

    String userPhotoURL;
    EditText e;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == author_no_img){
                img_user_photo.setImageResource(R.drawable.google_plus);
            }
            if(msg.what == author_has_img){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL myurl = new URL(articleURL);
                            authorBitmap = GetURLBitmap(myurl);
                            handler.sendEmptyMessage(setImage);
                        }catch (Exception e){
                            handler.sendEmptyMessage(1188);
                        }
                    }
                }).start();
            }
            if(msg.what == setImage){
                img_user_photo.setImageBitmap(authorBitmap);
            }
            if(msg.what == 1188){
                img_user_photo.setImageResource(R.drawable.google_plus);
            }
            if(msg.what == 2255){
                listView.setAdapter(new myListAdapter());
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_article);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = getIntent();
        articleID = intent.getStringExtra("articleID");
        System.out.println("++++++++++++++++++++++++++++++++++"+articleID);
        noStatusBar();
        init();
        getArticle();
        bt_event();

    }
    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        userID = account.getId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(account.getPhotoUrl() != null){
                        userPhotoURL = account.getPhotoUrl().toString();
                        URL myurl = new URL(account.getPhotoUrl().toString());
                        myBitmap = GetURLBitmap(myurl);
                    }else{
                        userPhotoURL = "";
                    }

                }catch (Exception e){

                }
            }
        }).start();
        System.out.println("_____________________"+userID);
    }


    private void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        img_user_photo = (CircleImageView) findViewById(R.id.img_showArticle_photo);
        title = (TextView) findViewById(R.id.t_showArticle_title);
        content = (TextView) findViewById(R.id.t_showArticle_content);
        time = (TextView) findViewById(R.id.t_showArticle_time);
        name = (TextView) findViewById(R.id.t_showArticle_userName);
        tag = (TextView) findViewById(R.id.t_showArticle_tag);
        b_leave_message = (Button) findViewById(R.id.b_leave_message);
        t_leaveMessage = (TextView) findViewById(R.id.message_num);

        al_image = new ArrayList<String>();
        al_name = new ArrayList<String>();
        al_time = new ArrayList<String>();
        al_message = new ArrayList<String>();
        al_bitmap = new ArrayList<Bitmap>();

        listView = (MyListView) findViewById(R.id.list_showArticle);

    }

    private static Bitmap GetURLBitmap(URL url){
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

    private void noStatusBar(){
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
    }

    private void getArticle(){
        DatabaseReference databaseReference2 = firebaseDatabase.getReference("accounts");
        databaseReference2.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    getArticle();
                }else{
                    for(DataSnapshot ds : task.getResult().getChildren()){
                        if(ds.getKey().equals(userID)){
                            for(DataSnapshot ds2 : ds.getChildren()){
                                if(ds2.getKey().equals("account_nickName")){
                                    userNickName = ds2.getValue().toString();
                                }
                            }
                        }
                    }
                }
            }
        });
        DatabaseReference databaseReference = firebaseDatabase.getReference("articles");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    getArticle();
                }
                else{
                    try{
                        for(DataSnapshot ds : task.getResult().getChildren()){
                            if(ds.getKey().equals(articleID)){
                                for(DataSnapshot ds2 : ds.getChildren()){
                                    if(ds2.getKey().equals("title")){
                                        title.setText(ds2.getValue().toString());
                                    }
                                    else if(ds2.getKey().equals("content")){
                                        content.setText(ds2.getValue().toString());
                                    }
                                    else if(ds2.getKey().equals("year")){
                                        year = ds2.getValue().toString();
                                    }
                                    else if(ds2.getKey().equals("month")){
                                        month = ds2.getValue().toString();
                                    }
                                    else if(ds2.getKey().equals("day")){
                                        day = ds2.getValue().toString();
                                    }
                                    else if(ds2.getKey().equals("hour")){
                                        hour = ds2.getValue().toString();
                                    }
                                    else if(ds2.getKey().equals("minute")){
                                        minute = ds2.getValue().toString();
                                    }
                                    else if(ds2.getKey().equals("author_nickName")){
                                        name.setText(ds2.getValue().toString());
                                    }
                                    else if(ds2.getKey().equals("tags")){
                                        tag.setText(ds2.getValue().toString());
                                    }
                                    else if(ds2.getKey().equals("author_image")){
                                        articleURL = ds2.getValue().toString();
                                    }
                                    else if(ds2.getKey().equals("leave_message")){
                                        leave_message = Integer.parseInt(ds2.getValue().toString());
                                    }
                                }
                            }
                        }
                        time.setText(year+"-"+month+"-"+day+" "+hour+":"+minute);
                        if(articleURL.equals("")){
                            handler.sendEmptyMessage(author_no_img);
                        }
                        else{
                            handler.sendEmptyMessage(author_has_img);
                        }

                        if(leave_message == 0){
                            t_leaveMessage.setText("尚未有留言");
                        }
                        else{
                            t_leaveMessage.setText("留言 ("+leave_message+")");
                            al_time.clear();al_name.clear();al_image.clear();al_message.clear();
                            al_bitmap.clear();
                            for(int i=1;i<=leave_message;i++){
                                for(DataSnapshot ds : task.getResult().getChildren()){
                                    if(ds.getKey().equals(articleID)){
                                        for(DataSnapshot ds2 : ds.getChildren()){
                                            if(ds2.getKey().equals("messages")){
                                                for(DataSnapshot ds3 : ds2.getChildren()){
                                                    if(ds3.getKey().equals("m"+String.valueOf(i))){
                                                        al_message.add(ds3.getValue().toString());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                for(DataSnapshot ds : task.getResult().getChildren()){
                                    if(ds.getKey().equals(articleID)){
                                        for(DataSnapshot ds2 : ds.getChildren()){
                                            if(ds2.getKey().equals("leave_message_names")){
                                                for(DataSnapshot ds3 : ds2.getChildren()){
                                                    if(ds3.getKey().equals("n"+String.valueOf(i))){
                                                        al_name.add(ds3.getValue().toString());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                for(DataSnapshot ds : task.getResult().getChildren()){
                                    if(ds.getKey().equals(articleID)){
                                        for(DataSnapshot ds2 : ds.getChildren()){
                                            if(ds2.getKey().equals("leave_message_images")){
                                                for(DataSnapshot ds3 : ds2.getChildren()){
                                                    if(ds3.getKey().equals("i"+String.valueOf(i))){
                                                        al_image.add(ds3.getValue().toString());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                for(DataSnapshot ds : task.getResult().getChildren()){
                                    if(ds.getKey().equals(articleID)){
                                        for(DataSnapshot ds2 : ds.getChildren()){
                                            if(ds2.getKey().equals("leave_message_times")){
                                                for(DataSnapshot ds3 : ds2.getChildren()){
                                                    if(ds3.getKey().equals("t"+String.valueOf(i))){
                                                        al_time.add(ds3.getValue().toString());

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for(int j=0;j<al_image.size();j++){
                                            System.out.println("IIIIIIIIIIIIIII"+j+":::"+al_image);
                                            System.out.println("NNNNNNNNNNNNNNN"+j+":::"+al_bitmap);
                                            if(al_image.get(j).equals("")){
                                                System.out.println("OOOOOOOOOOOOOOOOOOOOOOOO");
                                                al_bitmap.add(null);
                                            }
                                            else{
                                                System.out.println("PPPPPPPPPPPPPPPPPPPPP");
                                                URL myurl = new URL(al_image.get(j));
                                                al_bitmap.add(GetURLBitmap(myurl));
                                            }
                                            System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQ");
                                        }
                                        handler.sendEmptyMessage(2255);
                                    }catch (Exception e){
                                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!"+e.toString());
                                        //al_bitmap.add(null);
                                    }
                                }
                            }).start();
                        }
                    }catch (Exception e){
                        System.out.println(e.toString());
                    }
                }
            }
        });

    }



    private void bt_event(){
        b_leave_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View myAlert = LayoutInflater.from(show_article.this).inflate(R.layout.alert_leave_message,null);
                e = (EditText) myAlert.findViewById(R.id.e_leave_message);
                AlertDialog.Builder builder = new AlertDialog.Builder(show_article.this);
                builder.setTitle("留言")
                        .setView(myAlert)
                        .setNegativeButton("取消",null)
                        .setPositiveButton("發佈", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!e.getText().toString().equals("")){
                                    DatabaseReference d = firebaseDatabase.getReference("articles");
                                    System.out.println("===========================");
                                    System.out.println(e.getText().toString());
                                    System.out.println("===========================");
                                    d.child(articleID).child("messages").child("m"+String.valueOf(leave_message+1)).setValue(e.getText().toString());
                                    d.child(articleID).child("leave_message").setValue(leave_message+1);
                                    d.child(articleID).child("leave_message_names").child("n" + String.valueOf(leave_message + 1)).setValue(userNickName);
                                    d.child(articleID).child("leave_message_images").child("i" + String.valueOf(leave_message+ 1) ).setValue(userPhotoURL);
                                    Calendar calendar = Calendar.getInstance();
                                    String myTime = "";
                                    myTime = String.valueOf(calendar.get(Calendar.YEAR))+"-"
                                            +String.valueOf(calendar.get(Calendar.MONTH)+1)+"-"
                                            +String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"  "
                                            +String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+":"
                                            +String.valueOf(calendar.get(Calendar.MINUTE));
                                    d.child(articleID).child("leave_message_times").child("t" + String.valueOf(leave_message + 1)).setValue(myTime);
                                    al_message.add(e.getText().toString());
                                    leave_message+=1;
                                    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+leave_message);
                                    al_name.add(userNickName);
                                    al_image.add(userPhotoURL);
                                    al_time.add(myTime);
                                    t_leaveMessage.setText("留言 ("+leave_message+")");
                                    Toast.makeText(show_article.this,"發佈成功",Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if(userPhotoURL.equals("")){
                                                    al_bitmap.add(null);
                                                }else{
                                                    URL myurl = new URL(userPhotoURL);
                                                    al_bitmap.add(GetURLBitmap(myurl));
                                                }
                                            }catch (Exception e){

                                            }
                                        }
                                    }).start();
                                    getArticle();
                                }else{
                                    Toast.makeText(show_article.this,"輸入不可為空",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            }
        });
    }

    private class myListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return al_message.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //handler.sendEmptyMessage(656463);
            View v = getLayoutInflater().inflate(R.layout.l_leave_message,null);
            CircleImageView circleImageView = v.findViewById(R.id.t_leave_message_image);
            TextView t_name = v.findViewById(R.id.t_leave_message_name);
            TextView t_time = v.findViewById(R.id.t_leave_message_time);
            TextView t_message = v.findViewById(R.id.t_leave_message_content);
            t_name.setText(al_name.get(position));
            t_time.setText(al_time.get(position));
            t_message.setText(al_message.get(position));
            myPosition = position;
            System.out.println("(((((((((((((((((("+al_bitmap);
            System.out.println("(((((((((((((((((("+al_bitmap.isEmpty());

            if(!al_bitmap.isEmpty()){
                if(al_bitmap.get(position) == null){
                    System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                    circleImageView.setImageResource(R.drawable.google_plus);
                }else{
                    System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
                    circleImageView.setImageBitmap(al_bitmap.get(position));
                }
            }
            //circleImageView.setImageBitmap(al_bitmap.get(position));
            return v;
        }
    }
}