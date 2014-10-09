package com.example.ourduty.digitalsignv03;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by OurDuty on 06.10.14.
 */
class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("myLog", "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table statistics ("
                + "type integer,"
                + "stats text,"
                + "date date" + ");");
        db.execSQL("create table tasks ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "videoPath text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
