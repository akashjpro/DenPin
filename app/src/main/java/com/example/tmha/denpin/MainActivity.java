package com.example.tmha.denpin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private boolean isSupportFlash;
    private boolean isOn;

    private ImageButton mBtnSwitch;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private MediaPlayer mMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnSwitch = findViewById(R.id.btnSwitch);

        /**
         * First check if device is support flashlight or not
         */
        isSupportFlash = getApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isSupportFlash){
            // Device doesn't support flash
            // Show alert message and close the application
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("sorry, your device doesn't support flash light!");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();
            return;

        }

        getCamera();

        toggleButtonImage();

        mBtnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOn){
                    turnOffFlash();
                }else {
                    turnOnFlash();
                }
            }
        });

    }

    private void turnOnFlash() {
        if(mCamera == null || mParameters == null){
            return;
        }
        playSound();
        mParameters = mCamera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
        isOn = true;
        toggleButtonImage();
    }

    private void turnOffFlash() {
        if(mCamera == null || mParameters == null){
            return;
        }
        playSound();
        mParameters = mCamera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
        isOn = false;
        toggleButtonImage();
    }

    private void playSound() {
        if (isOn){
            mMediaPlayer = MediaPlayer.create(this, R.raw.light_switch_off);
        }else {
            mMediaPlayer = MediaPlayer.create(this, R.raw.light_switch_on);
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.release();
            }
        });
        mMediaPlayer.start();
    }

    private void getCamera() {
        if (mCamera == null && mParameters == null){
            try {
                mCamera = Camera.open();
                mParameters = mCamera.getParameters();
            }catch (RuntimeException e){
                Log.e("Camera error", e.getMessage());
            }

        }
    }

    private void toggleButtonImage() {
        if (isOn){
            mBtnSwitch.setImageResource(R.drawable.btn_switch_on);
        }else {
            mBtnSwitch.setImageResource(R.drawable.btn_switch_off);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
        if(isSupportFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


}
