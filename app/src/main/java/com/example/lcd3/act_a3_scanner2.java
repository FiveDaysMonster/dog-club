package com.example.lcd3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class act_a3_scanner2 extends AppCompatActivity  implements DecoratedBarcodeView.TorchListener {

    DecoratedBarcodeView decoratedBarcodeView;
    CaptureManager captureManager;
    Button button;
    Boolean isLight=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_a3_scanner2);

        noStatusBar();

        decoratedBarcodeView=(DecoratedBarcodeView) findViewById(R.id.barcodeview2);
        captureManager=new CaptureManager(act_a3_scanner2.this,decoratedBarcodeView);
        captureManager.initializeFromIntent(getIntent(),savedInstanceState);
        captureManager.decode();
        button=(Button) findViewById(R.id.lightButton2);
        if (!hasFlash()) {
            button.setVisibility(View.GONE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLight){
                    decoratedBarcodeView.setTorchOff();
                    isLight=false;
                }
                else{
                    decoratedBarcodeView.setTorchOn();
                    isLight=true;
                }
            }
        });
    }

    private void noStatusBar(){
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
    }



    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return decoratedBarcodeView.onKeyDown(keyCode,event)||super.onKeyDown(keyCode, event);
    }

    public boolean hasFlash(){
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public void onTorchOn() {
        isLight=true;
    }

    @Override
    public void onTorchOff() {
        isLight=false;
    }
}