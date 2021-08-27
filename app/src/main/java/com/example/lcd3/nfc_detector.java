package com.example.lcd3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class nfc_detector extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    TextView textViewInfo;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_detector);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        textViewInfo = (TextView)findViewById(R.id.info);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this,
                    "NFC NOT supported on this devices!",
                    Toast.LENGTH_LONG).show();
            finish();
        }else if(!nfcAdapter.isEnabled()){
            Toast.makeText(this,
                    "NFC NOT Enabled!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }
    String userID;

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        userID = account.getId();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(tag == null){
                textViewInfo.setText("tag == null");
            }else{
                String myID = "";
                byte[] tagId = tag.getId();
                for(int i=0; i<tagId.length; i++){
                    myID += Integer.toHexString(tagId[i] & 0xFF);
                }
                textViewInfo.setText(myID);
                System.out.println(myID);
                if(myID.equals("43e383")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","43e383");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }else if(myID.equals("5ba383")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","5ba383");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }else if(myID.equals("34ab383")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","34ab383");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }else if(myID.equals("c26d363")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","c26d363");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }else if(myID.equals("fccc353")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","fccc353");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }else if(myID.equals("b4f383")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","b4f383");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }else if(myID.equals("c0d2353")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","c0d2353");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }else if(myID.equals("836a353")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","836a353");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }else if(myID.equals("2cf1383")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","2cf1383");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }else if(myID.equals("9843353")){
                    Intent intent11 = new Intent(nfc_detector.this,show_dog_file.class);
                    intent11.putExtra("tag_num","9843353");
                    intent11.putExtra("user_id",userID);
                    startActivity(intent11);
                    finish();
                }
                else{
                    Toast.makeText(nfc_detector.this,"無資料",Toast.LENGTH_SHORT).show();
                }

            }
        }else{
            Toast.makeText(this,
                    "onResume() : " + action,
                    Toast.LENGTH_SHORT).show();
        }
    }
}