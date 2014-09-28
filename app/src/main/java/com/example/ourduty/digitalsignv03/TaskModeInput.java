package com.example.ourduty.digitalsignv03;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;


public class TaskModeInput extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_mode_input);

        int theId = getIntent().getExtras().getInt("id");
        TextView txtInfo = (TextView)findViewById(R.id.textView);
        txtInfo.setText("Input id:" + theId);
    }
}