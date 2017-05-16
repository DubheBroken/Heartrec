package com.example.zero.memorandum.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Zero on 2017/2/7.
 */

public class SqliteHelper extends SQLiteOpenHelper {

    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SqliteHelper(Context context){
        super(context, Constant.DATABASE_NAME,null,Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + Constant.TABLE_NAME + "("
                + Constant.ID + " integer primary key AUTOINCREMENT,"
                + Constant.SUBSTANCE + " varchar(2000),"
                + Constant.TIME + " varchar(40));";
        Log.i("SQL",sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
