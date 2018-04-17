package com.example.android.primusgradus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class Settings extends AppCompatActivity {

    private AdView mAdView;
    SharedPreferences prefs = null;
    boolean ringtone;
    boolean vibr;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mAdView = (AdView) findViewById(R.id.adMobBannerSettings);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.w("ADS Settings","onAdLoaded()");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.w("ADS Settings","onAdFailedToLoad()");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.w("ADS Settings","onAdOpened()");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.w("ADS Settings","onAdLeftApplication()");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.w("ADS Settings","onAdClosed()");
            }
        });

        Log.w("LOG W", "SA onCreate");


        prefs = getSharedPreferences("PrimusGradus", MODE_PRIVATE);
        handleClicking();
    }

    public void handleClicking(){

        Log.w("LOG W", "SA handleClicking");

        final NumberPicker nps = (NumberPicker) findViewById(R.id.nps);
        final NumberPicker npm = (NumberPicker) findViewById(R.id.npm);
        final NumberPicker nph = (NumberPicker) findViewById(R.id.nph);
        Button save = (Button) findViewById(R.id.save);
        RadioGroup rgroup = (RadioGroup) findViewById(R.id.radiogroup);
        RadioGroup rgroupV = (RadioGroup) findViewById(R.id.radiogroupv);

        ringtone = prefs.getBoolean("ring",true);
        vibr = prefs.getBoolean("vibr",true);

        if(ringtone){
            rgroup.check(R.id.ringtone);
        }else{
            rgroup.check(R.id.notification);
        }

        if(vibr){
            rgroupV.check(R.id.vibrateYes);
        }else{
            rgroupV.check(R.id.vibrateNo);
        }

        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.ringtone){
                    Log.w("LOG W", "SA ringtone checked");
                    ringtone = true;
                }else{
                    Log.w("LOG W", "SA notifications checked");
                    ringtone = false;
                }
            }
        });

        rgroupV.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.vibrateYes){
                    vibr = true;
                }else{
                    vibr = false;
                }
            }
        });

        int time = prefs.getInt("time",0);

        nps.setMinValue(0);
        npm.setMinValue(0);
        nph.setMinValue(0);

        nps.setMaxValue(59);
        npm.setMaxValue(59);
        nph.setMaxValue(23);

        nps.setValue(time%60);
        npm.setValue((time/60)%60);
        nph.setValue((time/3600)%24);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seconds = nph.getValue()*3600+npm.getValue()*60+nps.getValue();

                prefs.edit().putInt("time",seconds).commit();

                Log.w("LOG W", "SA Time set to "+seconds+" seconds");
                Log.w("LOG W", "SA saved");

                prefs.edit().putBoolean("ring",ringtone).commit();
                prefs.edit().putBoolean("vibr",vibr).commit();

                finish();
            }
        });
    }

}
