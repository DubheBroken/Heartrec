package com.zdk.pojun.heartrec.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zero on 2017/2/7.
 */

public class SqliteHelper extends SQLiteOpenHelper {

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    private int operation=0;

    public void addOperation() {
        operation++;
    }

    public void removeOperation() {
        operation--;
    }



    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        operation++;
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        operation++;
        return super.getReadableDatabase();
    }

    SqliteHelper(Context context){
        super(context, Constant.DATABASE_NAME,null,Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + Constant.TABLE_NAME + "("
                + Constant.ID + " integer primary key AUTOINCREMENT,"
                + Constant.SUBSTANCE + " varchar(2000),"
                + Constant.TIME + " varchar(40));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
