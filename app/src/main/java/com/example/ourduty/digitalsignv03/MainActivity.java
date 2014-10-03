package com.example.ourduty.digitalsignv03;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class MainActivity extends TabActivity{
    /** Called when the activity is first created. */
    static TabHost tabHost;

    public static TabHost getCurrentTabHost(){
        return tabHost;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = getTabHost();
        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Free mode");
        tabSpec.setContent(new Intent(this, InputActivity.class));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Task mode");
        tabSpec.setContent(new Intent(this, TaskScreenActivity.class));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator("Settings");
        tabSpec.setContent(new Intent(this, SettingsActivity.class));
        tabHost.addTab(tabSpec);
    }

    public void openSettings(MenuItem item) {
        MainActivity.getCurrentTabHost().setCurrentTabByTag("tag3");
    }

    public void openFreeMode(MenuItem item) {
        MainActivity.getCurrentTabHost().setCurrentTabByTag("tag1");
    }

    public void openTasks(MenuItem item) {
        MainActivity.getCurrentTabHost().setCurrentTabByTag("tag2");
    }
}