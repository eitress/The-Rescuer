package com.example.android.primusgradus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

/**
 * Created by Isabel on 18/03/2018.
 */
public class Settings extends AppCompatActivity {

    SharedPreferences prefs = null;
    boolean ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        prefs = getSharedPreferences("PrimusGradus", MODE_PRIVATE);
        handleClicking();
    }

    public void handleClicking(){
        final NumberPicker nps = (NumberPicker) findViewById(R.id.nps);
        final NumberPicker npm = (NumberPicker) findViewById(R.id.npm);
        final NumberPicker nph = (NumberPicker) findViewById(R.id.nph);
        Button save = (Button) findViewById(R.id.save);
        RadioGroup rgroup = (RadioGroup) findViewById(R.id.radiogroup);

        ringtone = prefs.getBoolean("ring",true);

        if(ringtone){
            rgroup.check(R.id.ringtone);
        }else{
            rgroup.check(R.id.notification);
        }

        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.ringtone){
                    ringtone = true;
                }else{
                    ringtone = false;
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

                Log.w("GAMMA", "Time set to "+seconds+" seconds");

                if(ringtone){
                    prefs.edit().putBoolean("ring",true).commit();
                }else{
                    prefs.edit().putBoolean("ring",false).commit();
                }

                finish();
            }
        });
    }

}
