package com.example.ourduty.digitalsignv03;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;


public class OneActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one);
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
// then you use
        boolean mark = prefs.getBoolean("animationPref", true);
        TextView tmp = (TextView)findViewById(R.id.textView);
        tmp.setText("animationPref = " + mark);
        Log.d("myLog", "animationPref = " + mark);
    }


}