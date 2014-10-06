package com.example.ourduty.digitalsignv03;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.provider.Settings;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    SQLiteDatabase db;
    HttpURLConnection urlConnection;
    SharedPreferences prefs;
    SettingsActivity t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        t = this;
        super.onCreate(savedInstanceState);
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences,
                false);
        initSummary(getPreferenceScreen());
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        final AlertDialog alert = new AlertDialog.Builder(this).create();

        Preference sendStatsPrefButton = (Preference)findPreference("sendStatsPref");
        sendStatsPrefButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                alert.setButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){

                    }
                });
                alert.setTitle("Message");

                //Костыль для отправки, очень плохой, надо бы исправить потом
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                //Отправка на сервер
                prefs = PreferenceManager.getDefaultSharedPreferences(t);
                String editUploadLink = prefs.getString("editUploadLink", "http://oapi.ourduty.zz.mu/updateBD.php");
                Cursor c = db.query("statistics", null, null, null, null, null, null);
                if (c.moveToFirst()) {
                    // определяем номера столбцов по имени в выборке
                    int dateColIndex = c.getColumnIndex("date");
                    int typeColIndex = c.getColumnIndex("type");
                    int statsColIndex = c.getColumnIndex("stats");
                    do {
                        try {
                            // загрузка страницы
                            URL urlToRequest = new URL(editUploadLink);
                            HttpURLConnection urlConnection =
                                    (HttpURLConnection) urlToRequest.openConnection();
                            urlConnection.setDoOutput(true);
                            urlConnection.setDoInput(true);
                            urlConnection.setReadTimeout(10000);
                            urlConnection.setConnectTimeout(15000);
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setRequestProperty("Content-Type",
                                    "application/x-www-form-urlencoded");

                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("vers", "sig"));
                            params.add(new BasicNameValuePair("user", "and-" + Settings.Secure.getString(t.getContentResolver(),
                                    Settings.Secure.ANDROID_ID)));
                            params.add(new BasicNameValuePair("type", String.valueOf(c.getInt(typeColIndex))));
                            params.add(new BasicNameValuePair("stats", c.getString(statsColIndex)));
                            params.add(new BasicNameValuePair("date", c.getString(dateColIndex)));

                            OutputStream os = urlConnection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(getQuery(params));
                            writer.flush();
                            writer.close();
                            os.close();
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

                            if (allpage.toString().charAt(0) == '0') {
                                alert.setMessage("Stats sent!");
                                db.delete("statistics", "stats = '" + c.getString(statsColIndex) + "'", null);
                                alert.show();
                            } else {
                                alert.setMessage("Stats unsent! '" + allpage.toString() + "'");
                                alert.show();
                            }
                        } catch (Exception e) {
                            alert.setMessage("Exception:" + e);
                            alert.show();
                        }
                    } while (c.moveToNext());
                } else {
                    alert.setMessage("Nothing to send!");
                    alert.show();
                }
                return true;
            }
        });
        Preference checkTasksPrefButton = (Preference)findPreference("checkTasksPref");
        checkTasksPrefButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                alert.setButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){

                    }
                });
                alert.setTitle("Message");
                alert.setMessage("check stats");
                alert.show();
                return true;
            }
        });

        //Удалить БД
        Preference clearSamplesPrefButton = (Preference)findPreference("clearSamplesPref");
        clearSamplesPrefButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                dialog.setButton2("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        db.delete("statistics", null, null);
                    }
                });
                dialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                dialog.setMessage("Are you sure you want to delete DB?");
                dialog.show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        updatePrefSummary(findPreference(key));
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
        if (p instanceof MultiSelectListPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}