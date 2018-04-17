package com.example.android.primusgradus;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import junit.framework.Test;

public class MainActivity extends AppCompatActivity {


    SharedPreferences prefs = null;
    boolean ringing;
    int seconds;
    boolean ring;
    int value;
    int type;
    boolean setToRing;
    Uri defaultRingtoneUriR;
    Ringtone defaultRingtoneR;
    Uri defaultRingtoneUriN;
    Ringtone defaultRingtoneN;
    boolean vibr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w("LOG W", "MA onCreate");

        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("PrimusGradus", MODE_PRIVATE);

        //prefs.edit().clear().commit();

        defaultRingtoneUriR = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        defaultRingtoneR = RingtoneManager.getRingtone(this, defaultRingtoneUriR);

        defaultRingtoneUriN = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
        defaultRingtoneN = RingtoneManager.getRingtone(this, defaultRingtoneUriN);


        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.screenBrightness =0.00001f;// I needed to dim the display
        this.getWindow().setAttributes(lp);

        LinearLayout lila = (LinearLayout) findViewById(R.id.screen);
        lila.setKeepScreenOn(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        hideSystemUI();

        setToRing = false;
        ringing = false;

        Log.w("LOG W", "MA onResume");
            permissions();
            seconds = prefs.getInt("time",0);
            ring = prefs.getBoolean("ring",true);
        vibr = prefs.getBoolean("vibr", true);


        if(ring){
            value = RingtoneManager.TYPE_RINGTONE;
            type = AudioManager.STREAM_RING;
        }else{
            value = RingtoneManager.TYPE_NOTIFICATION;
            type = AudioManager.STREAM_NOTIFICATION;
        }

        handleClicking();
    }


    @Override
    public void onPause(){
        super.onPause();
        Log.w("LOG W", "MA onPause");
    }

    @Override
    public void onStop(){
        super.onStop();

        Log.w("LOG W", "MA onStop");

        if(defaultRingtoneN.isPlaying()){
            defaultRingtoneN.stop();
            ringing = false;
            setToRing = false;
        }

        if(defaultRingtoneR.isPlaying()){
            defaultRingtoneR.stop();
            ringing = false;
            setToRing = false;
        }

        finish();

    }


    public void permissions(){

        Log.w("LOG W", "MA permissions");
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Log.w("LOG W", "MA perm: Not allowed yet");

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Please give us access to your Do Not Disturb settings")
                    .setPositiveButton("GO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.w("LOG W", "MA perm: Going to settings");
                            Intent intent = new Intent(
                                    android.provider.Settings
                                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                            startActivity(intent);
                            onResume();
                        }
                    })
                    .create()
                    .show();
        }
    }


    public void handleClicking(){

        Log.w("LOG W", "MA handleClicking");

        final AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        LinearLayout screen = (LinearLayout) findViewById(R.id.screen);

        screen.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    Log.w("LOG W", "MA onDoubleTap");

                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    am.setStreamVolume(type, 10, 0);
                    int sec = seconds*1000;

                    if(value == RingtoneManager.TYPE_RINGTONE) {

                        if (!setToRing) {

                            final Handler h2 = new Handler(){
                                @Override
                                public void handleMessage(Message msg){
                                    if(defaultRingtoneR.isPlaying()){
                                        defaultRingtoneR.stop();
                                        ringing = false;
                                        setToRing = false;
                                    }
                                }
                            };

                            setToRing = true;
                            Handler h = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    defaultRingtoneR.play();
                                    ringing = true;
                                    h2.sendEmptyMessageDelayed(0, 21000);
                                }
                            };

                            h.sendEmptyMessageDelayed(0, sec);
                            if(vibr){
                                v.vibrate(200);
                            }



                        } else if(ringing){
                            defaultRingtoneR.stop();
                            ringing = false;
                            setToRing = false;
                        }
                    }else{
                        if(!setToRing){
                            setToRing = true;
                            Handler h = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    defaultRingtoneN.play();
                                    Log.w("LOG W", "Inside Message Before While");
                                    while(defaultRingtoneN.isPlaying()){

                                    }
                                    Log.w("LOG W", "Inside Message After While");
                                    setToRing = false;

                                }
                            };

                            h.sendEmptyMessageDelayed(0, sec);
                            if(vibr){
                                v.vibrate(200);
                            }
                            Log.w("LOG W", "After Send Message");
                        }
                    }

                    return super.onDoubleTap(e);
                }
            });


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.w("LOG W", "MA Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
