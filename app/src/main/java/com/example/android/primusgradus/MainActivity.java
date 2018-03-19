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

    PowerManager pm;
    PowerManager.WakeLock wl;
    SharedPreferences prefs = null;
    boolean touch;
    int seconds;
    boolean ring;
    int value;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        touch = true;

        //prefs.edit().clear().commit();

        Log.w("BETA", "Permission bf.");
        permissions();
        Log.w("BETA", "Permission af");

        LinearLayout lila = (LinearLayout) findViewById(R.id.screen);
        lila.setKeepScreenOn(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            firstrun();
            prefs.edit().putBoolean("firstrun", false).commit();
            seconds = 5;
            prefs.edit().putInt("time",5).commit();
            ring = true;
            prefs.edit().putBoolean("ring", true).commit();
        }else{
            permissions();
            seconds = prefs.getInt("time",0);
            ring = prefs.getBoolean("ring",true);
        }

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
    public void onStop(){
        super.onStop();
    }


    public void permissions(){
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Log.w("PERM", "Not allowed yet");

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Please give us access to your Do Not Disturb settings")
                    .setPositiveButton("GO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.w("PERM", "Going to settings");
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

    public void firstrun(){

        permissions();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Welcome to Primus Gradus!\nLeave the app open and double-tap the screen to make your phone ring. The default is set to 5 seconds, but" +
                " you can change the timer in Settings")
                .setPositiveButton("GREAT!", null)
                .create()
                .show();


    }


    public void handleClicking(){

        Log.w("GAMMA", Integer.toString(value));

        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), value);
        final Ringtone defaultRingtone = RingtoneManager.getRingtone(this, defaultRingtoneUri);

        final AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        LinearLayout screen = (LinearLayout) findViewById(R.id.screen);

        screen.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    am.setStreamVolume(type, 10, 0);
                    int sec = seconds*1000;

                    if(touch){
                        touch = false;
                        Handler h = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                defaultRingtone.play();
                            }
                        };

                        h.sendEmptyMessageDelayed(0, sec);
                    }else{
                        touch = true;
                        defaultRingtone.stop();
                    }

                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.w("ALPHA", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);

                return true;
            }
        });

        Button settings = (Button) findViewById(R.id.settings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);

                startActivity(intent);
            }
        });

    }
}
