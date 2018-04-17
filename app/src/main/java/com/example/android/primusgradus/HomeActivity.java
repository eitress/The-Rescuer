package com.example.android.primusgradus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences prefs = null;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this, "ca-app-pub-6295581396939235~9666090624");

        mAdView = (AdView) findViewById(R.id.adMobBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.w("ADS Home","onAdLoaded()");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.w("ADS Home","onAdFailedToLoad()");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.w("ADS Home","onAdOpened()");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.w("ADS Home","onAdLeftApplication()");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.w("ADS Home","onAdClosed()");
            }
        });

        prefs = getSharedPreferences("PrimusGradus", MODE_PRIVATE);

        Log.w("LOG W", "HA onCreate");

        handleClicking();

        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).commit();
            prefs.edit().putInt("time",5).commit();
            prefs.edit().putBoolean("ring", true).commit();
            prefs.edit().putBoolean("vibr",true).commit();
        }
    }

    public void handleClicking(){
        Log.w("LOG W", "HA handleClicking");

        ImageView go = (ImageView) findViewById(R.id.go);
        ImageView settings = (ImageView) findViewById(R.id.settings);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.w("LOG W", "HA go clicked");

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.w("LOG W", "HA settings clicked");

                Intent intent = new Intent(HomeActivity.this, Settings.class);
                startActivity(intent);
            }
        });
    }

}
