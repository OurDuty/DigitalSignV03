package com.example.ourduty.digitalsignv03;

import android.app.Activity;
import android.os.Bundle;

/*********************************************
                UNUSED ACTIVITY
 **********************************************/

public class SupportFiles extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_files);

        /*                     PREFERENCE EXAMPLE
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mark = prefs.getBoolean("animationPref", true);
        TextView tmp = (TextView)findViewById(R.id.textView);
        tmp.setText("animationPref = " + mark);
        */
    }
}