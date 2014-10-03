package com.example.ourduty.digitalsignv03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

public class TaskScreenActivity extends Activity {
    ListView lvMain;
    protected void onCreate(Bundle savedInstanceState) {
        String[] tasks = getResources().getStringArray(R.array.tasks);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);

        lvMain = (ListView) findViewById(R.id.lvMain);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tasks);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("myLog", "itemClick: position = " + position + ", id = "
                        + id);
                Intent intent = new Intent(TaskScreenActivity.this, InputActivity.class);
                intent.putExtra("id", String.valueOf(id));
                startActivity(intent);
            }
        });
    }
}