package com.example.lcd3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Calendar;

public class add_new_article_edit_ui extends AppCompatActivity {

    Button b_cha, b_send;
    EditText e_tags, e_title, e_content;
    FirebaseDatabase firebaseDatabase;

    String accountName = "", accountType = "", accountDisplayName = ""
            , accountEmail = "", accountLevel = ""
            , accountFamilyName = "", accountGivenName = "", accountId = ""
            , accountPhotoUrl = "",accountNickName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_article_edit_ui);
        noStatusBar();

        init();
        getNickName();
        b_event();
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

    private void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        b_cha = (Button) findViewById(R.id.b_cha_addNewArticle);
        b_send = (Button) findViewById(R.id.b_announce);
        e_tags = (EditText) findViewById(R.id.e_tags);
        e_title = (EditText) findViewById(R.id.e_titles);
        e_content = (EditText) findViewById(R.id.e_contents);
    }

    private void b_event(){
        b_cha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!e_title.getText().toString().equals("") && !e_content.getText().toString().equals("")){
                    getNickName();
                    sendArticleToFirebase();
                }else{
                    Toast.makeText(add_new_article_edit_ui.this
                            ,"標題與內容不可為空"
                    ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendArticleToFirebase(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("articles");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    sendArticleToFirebase();
                }else{
                    Calendar calendar = Calendar.getInstance();
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    databaseReference.child(timestamp).child("tags").setValue(e_tags.getText().toString());
                    databaseReference.child(timestamp).child("title").setValue(e_title.getText().toString());
                    databaseReference.child(timestamp).child("content").setValue(e_content.getText().toString());
                    databaseReference.child(timestamp).child("year").setValue(calendar.get(Calendar.YEAR));
                    databaseReference.child(timestamp).child("month").setValue(calendar.get(Calendar.MONTH)+1);
                    databaseReference.child(timestamp).child("day").setValue(calendar.get(Calendar.DAY_OF_MONTH));
                    databaseReference.child(timestamp).child("hour").setValue(calendar.get(Calendar.HOUR_OF_DAY));
                    databaseReference.child(timestamp).child("minute").setValue(calendar.get(Calendar.MINUTE));
                    databaseReference.child(timestamp).child("click_times").setValue(0);
                    databaseReference.child(timestamp).child("click_male").setValue(0);
                    databaseReference.child(timestamp).child("click_female").setValue(0);
                    databaseReference.child(timestamp).child("click_student").setValue(0);
                    databaseReference.child(timestamp).child("click_teacher").setValue(0);
                    databaseReference.child(timestamp).child("click_others").setValue(0);
                    databaseReference.child(timestamp).child("violation").setValue(0);
                    databaseReference.child(timestamp).child("author_name").setValue(accountDisplayName);
                    databaseReference.child(timestamp).child("author_nickName").setValue(accountNickName);
                    databaseReference.child(timestamp).child("author_id").setValue(accountId);
                    databaseReference.child(timestamp).child("author_email").setValue(accountEmail);
                    databaseReference.child(timestamp).child("author_image").setValue(accountPhotoUrl);
                    databaseReference.child(timestamp).child("leave_message").setValue(0);



                    Intent intent = new Intent(add_new_article_edit_ui.this,ui_level_3.class);
                    startActivity(intent);
                    Toast.makeText(add_new_article_edit_ui.this,"發佈成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void getNickName(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
        databaseReference.child(accountId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    getNickName();
                }else{
                    for(DataSnapshot ds : task.getResult().getChildren()){
                        if(ds.getKey().equals("account_nickName")){
                            accountNickName = ds.getValue().toString();
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!"+accountNickName);
                        }
                    }
                }
            }
        });
    }

    private void noStatusBar(){
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        getSignUpData(account);
    }
}