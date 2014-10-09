package com.example.ourduty.digitalsignv03;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TaskScreenActivity extends Activity {
    ListView lvMain;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);
        updateList();
    }
    public void updateList(){
        lvMain = (ListView) findViewById(R.id.lvMain);
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("tasks", null, null, null, null, null, null);
        ArrayList<String> name_array = new ArrayList<String>();
        int nameColIndex = c.getColumnIndex("name");
        ArrayList<String> path_list = new ArrayList<String>();
        int pathColIndex = c.getColumnIndex("videoPath");
        if (c.moveToFirst())
            do {
                name_array.add(c.getString(nameColIndex));
                path_list.add(c.getString(pathColIndex));
            } while (c.moveToNext());
        //Array path_array = path_list.toArray();
        String[] path_array_tmp = new String[path_list.size()];
        path_array_tmp = path_list.toArray(path_array_tmp);
        final String[] path_array = path_array_tmp;


        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, name_array);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(TaskScreenActivity.this, InputActivity.class);
                intent.putExtra("id", String.valueOf(id+1));
                intent.putExtra("path", path_array[(int)id]);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        updateList();
    }
}