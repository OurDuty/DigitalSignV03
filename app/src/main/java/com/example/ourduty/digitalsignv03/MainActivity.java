package com.example.ourduty.digitalsignv03;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class MainActivity extends TabActivity{
    final String PATH = "/data/data/com.example.ourduty.digitalsignv03/";
    /** Called when the activity is first created. */
    static TabHost tabHost;

    public static TabHost getCurrentTabHost(){
        return tabHost;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        inflater.inflate(R.menu.update_tasks, menu);
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

    public void updateList(MenuItem item) {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        alert.setButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){

            }
        });

        //Костыль для отправки, очень плохой, надо бы исправить потом
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        alert.setTitle("Updating tasks");
        alert.setMessage("Downloadind tasks");
        alert.show();
        Cursor c = db.query("tasks", null, null, null, null, null, null);
        int id = 0;
        if (c.moveToFirst())
            do {
                id++;
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String editDownloadLink = prefs.getString("editDownloadLink", "http://oapi.ourduty.zz.mu/checkNewTasks.php");
        try {
            // загрузка страницы
            editDownloadLink += "?vers=sig&db_vers="+(id);
            URL urlToRequest = new URL(editDownloadLink);
            HttpURLConnection urlConnection =
                    (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);

            urlConnection.connect();

            InputStreamReader rd = new InputStreamReader(urlConnection.getInputStream());
            StringBuilder allpage = new StringBuilder();
            int n = 0;
            char[] buffer = new char[40000];
            while (n >= 0) {
                n = rd.read(buffer, 0, buffer.length);
                if (n > 0) {
                    allpage.append(buffer, 0, n);
                }
            }
            n = 0;
            String buf = "";
            while(n < allpage.toString().length()){
                char tmp = allpage.toString().charAt(n);
                if(tmp == ';'){
                    if(downloadFile(buf, PATH + (id+1) + ".mp4") != -1){
                        Date date = new Date();
                        ContentValues cv = new ContentValues();
                        cv.put("id", id+1);
                        cv.put("name", (id+1) + ". DOWNLOADED on " + date);
                        cv.put("videoPath", PATH + (id+1) + ".mp4");
                        db.insert("tasks", null, cv);
                        id++;
                    }

                    buf = "";
                } else buf += tmp;
                n++;
            }
            alert.setMessage("Download completed!");
        } catch (Exception e) {
            alert.setMessage("Exception:" + e);
        }
        TaskScreenActivity ts = (TaskScreenActivity) this.getLocalActivityManager().getActivity("tag2");
        if(ts != null && ts.lvMain != null)
            ts.updateList();
        alert.show();
    }

    public int downloadFile(String url, String dest_file_path) {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Error");
        try {
            File dest_file = new File(dest_file_path);
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();
            DataInputStream stream = new DataInputStream(u.openStream());
            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();
            DataOutputStream fos = new DataOutputStream(new FileOutputStream(dest_file));
            fos.write(buffer);
            fos.flush();
            fos.close();

        } catch(FileNotFoundException e) {
            alert.setMessage("Exception:" + e);
            alert.show();
            return -1;
        } catch (IOException e) {
            alert.setMessage("Exception:" + e);
            alert.show();
            return -1;
        }
        return 0;
    }
}