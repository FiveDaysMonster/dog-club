package com.example.lcd3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class show_dog_file extends AppCompatActivity {

    Button b_close;
    String tag_num;
    ImageView img;
    FirebaseDatabase firebaseDatabase;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    String userID;
    FrameLayout f;
    RelativeLayout r;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_dog_file);

        noStatusBar();
        init();
        Intent intent = getIntent();
        tag_num = intent.getStringExtra("tag_num");
        userID = intent.getStringExtra("user_id");
        img = (ImageView) findViewById(R.id.show_image_book);
        which_tag();
        b_close = (Button) findViewById(R.id.b_show_dog_image);
        b_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        f = (FrameLayout) findViewById(R.id.myFrame);
        r = (RelativeLayout) findViewById(R.id.show_file_relativelayout);
        b = (Button) findViewById(R.id.b_show_file);
    }
    private void noStatusBar(){
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
    }
    private void which_tag(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
        Integer tag = 0;

        if(userID.equals("116063306527075552878")){
            r.setVisibility(View.VISIBLE);
            if(tag_num.equals("43e383")){
                img.setImageResource(R.drawable.d3);
                databaseReference.child(userID).child("books").child("b1").setValue(1);
            }else if(tag_num.equals("5ba383")){
                img.setImageResource(R.drawable.d4);
                databaseReference.child(userID).child("books").child("b2").setValue(1);
            }else if(tag_num.equals("34ab383")){
                img.setImageResource(R.drawable.d5);
                databaseReference.child(userID).child("books").child("b3").setValue(1);
            }else if(tag_num.equals("c26d363")){
                img.setImageResource(R.drawable.d7);
                databaseReference.child(userID).child("books").child("b4").setValue(1);
            }else if(tag_num.equals("fccc353")){
                img.setImageResource(R.drawable.d8);
                databaseReference.child(userID).child("books").child("b5").setValue(1);
            }else if(tag_num.equals("b4f383")){
                img.setImageResource(R.drawable.d10);
                databaseReference.child(userID).child("books").child("b6").setValue(1);
            }else if(tag_num.equals("c0d2353")){
                img.setImageResource(R.drawable.d12);
                databaseReference.child(userID).child("books").child("b7").setValue(1);
            }else if(tag_num.equals("836a353")){
                img.setImageResource(R.drawable.d13);
                databaseReference.child(userID).child("books").child("b8").setValue(1);
            }else if(tag_num.equals("2cf1383")){
                img.setImageResource(R.drawable.d14);
                databaseReference.child(userID).child("books").child("b9").setValue(1);
            }else if(tag_num.equals("9843353")){
                img.setImageResource(R.drawable.d16);
                databaseReference.child(userID).child("books").child("b10").setValue(1);
            }
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(show_dog_file.this,"紀錄成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }else{
            r.setVisibility(View.INVISIBLE);
            if(tag_num.equals("43e383")){
                img.setImageResource(R.drawable.d3);
                databaseReference.child(userID).child("books").child("b1").setValue(1);
            }else if(tag_num.equals("5ba383")){
                img.setImageResource(R.drawable.d4);
                databaseReference.child(userID).child("books").child("b2").setValue(1);
            }else if(tag_num.equals("34ab383")){
                img.setImageResource(R.drawable.d5);
                databaseReference.child(userID).child("books").child("b3").setValue(1);
            }else if(tag_num.equals("c26d363")){
                img.setImageResource(R.drawable.d7);
                databaseReference.child(userID).child("books").child("b4").setValue(1);
            }else if(tag_num.equals("fccc353")){
                img.setImageResource(R.drawable.d8);
                databaseReference.child(userID).child("books").child("b5").setValue(1);
            }else if(tag_num.equals("b4f383")){
                img.setImageResource(R.drawable.d10);
                databaseReference.child(userID).child("books").child("b6").setValue(1);
            }else if(tag_num.equals("c0d2353")){
                img.setImageResource(R.drawable.d12);
                databaseReference.child(userID).child("books").child("b7").setValue(1);
            }else if(tag_num.equals("836a353")){
                img.setImageResource(R.drawable.d13);
                databaseReference.child(userID).child("books").child("b8").setValue(1);
            }else if(tag_num.equals("2cf1383")){
                img.setImageResource(R.drawable.d14);
                databaseReference.child(userID).child("books").child("b9").setValue(1);
            }else if(tag_num.equals("9843353")){
                img.setImageResource(R.drawable.d16);
                databaseReference.child(userID).child("books").child("b10").setValue(1);
            }
        }


    }
}