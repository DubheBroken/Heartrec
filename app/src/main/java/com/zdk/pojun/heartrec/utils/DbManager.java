package com.zdk.pojun.heartrec.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Zero on 2017/2/7.
 */

public class DbManager {
    private static SqliteHelper helper;
    public static SqliteHelper getIntance(Context context){
        if(helper==null){
            helper = new SqliteHelper(context);
        }
        return helper;
    }

    /**
     * 执行语句
     * @param db 数据库对象
     * @param sql sql语句
     * */
        public static void execSQL(SQLiteDatabase db, String sql){
            if(db!=null){
                if(sql!=null&&!"".equals(sql)){
                    db.execSQL(sql);
                }
            }
    }

    /**
     * 查询语句
     * @param sql 语句
     * @param db 数据库对象
     * @param selectionArgs 查询条件占位符
     * */
    public static Cursor selectDataBySql(SQLiteDatabase db, String sql, String[] selectionArgs){
        Cursor cursor=null;
        if(db!=null){
            cursor=db.rawQuery(sql, selectionArgs);
        }
        return cursor;
    }


}
