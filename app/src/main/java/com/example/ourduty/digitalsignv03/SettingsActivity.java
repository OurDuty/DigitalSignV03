package com.example.ourduty.digitalsignv03;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){

            }
        });
        dialog.setTitle("Message");

        Preference sendStatsPrefButton = (Preference)findPreference("sendStatsPref");
        sendStatsPrefButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                dialog.setMessage("send stats");
                dialog.show();
                return true;
            }
        });
        Preference checkTasksPrefButton = (Preference)findPreference("checkTasksPref");
        checkTasksPrefButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                dialog.setMessage("check stats");
                dialog.show();
                return true;
            }
        });
    }
}