package com.example.zero.memorandum.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zero.memorandum.R;
import com.example.zero.memorandum.custom.CustomPopwindow;
import com.example.zero.memorandum.utils.Constant;
import com.example.zero.memorandum.utils.DbManager;
import com.example.zero.memorandum.utils.MemorandumAdapter;
import com.example.zero.memorandum.utils.Memorandum_JavaBean;
import com.example.zero.memorandum.utils.SqliteHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zero on 2017/2/15.
 */

public class Main_activity extends Activity implements OnClickListener {

    private List<Memorandum_JavaBean> list;

    private ListView listView;
    private MemorandumAdapter adapter;
    private SqliteHelper sqliteHelper;
//    注册控件
    private FloatingActionButton FAB_new_one;
    private CustomPopwindow customPopwindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        sqliteHelper = DbManager.getIntance(this);
        initView();

        //    item点击事件
        listView.setOnItemClickListener((new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        // TODO Auto-generated method stub);
                        Intent intent = new Intent(Main_activity.this, NewOne_activity.class);
                        intent.putExtra("id", list.get(arg2).id);//将被点击的item id传递到新活动
                        Log.i("-------intent-------","Extra="+list.get(arg2).id);
                        startActivity(intent);
                    }
                })
        );
//        item长按事件
        listView.setOnItemLongClickListener((new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                           long arg3) {
                showMultiDia(list.get(arg2).id);
                return true;
            }
        }));

//        沉浸式状态栏
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);

        // 状态栏背景色
        tintManager.setTintColor(getColor(R.color.colorAccent));
    }

//    沉浸式状态栏
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

//    删除记录并刷新GridView
    public void delete(final String id){

        new AlertDialog.Builder(this).setTitle("确认要删除吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
//                            删除自动保存的数据
                        SQLiteDatabase db = sqliteHelper.getWritableDatabase();//打开数据库
                        try {
                            String sql = "delete from " + Constant.TABLE_NAME + " where " + Constant.ID + "=" + id + ";";
                            Log.i("strsql", sql);
                            DbManager.execSQL(db, sql);
                            Log.i("execSQL", "删除数据成功");
                        } catch (Exception e) {
                            Log.e("execSQL", "删除数据出错");
                        }
                        initView();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();

    }

//    右键菜单
    private void showMultiDia(final String id)
    {
        AlertDialog.Builder multiDia=new AlertDialog.Builder(Main_activity.this);
        multiDia.setTitle("选择操作");
        multiDia.setPositiveButton("删除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                delete(id);
            }
        });
        multiDia.create().show();
    }

    //      点击事件
    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.btn_newone:
                customPopwindow = new CustomPopwindow(this,this);
                customPopwindow.setBackgroundDrawable(null);
                customPopwindow.showAsDropDown(v,-20,-620,Gravity.TOP);
                break;
            case R.id.btn_text:
                intent = new Intent(this,NewOne_activity.class);
                startActivity(intent);
                customPopwindow.dismiss();
                break;
            case R.id.btn_picture:
                Toast.makeText(this,"敬请期待",Toast.LENGTH_SHORT).show();
                customPopwindow.dismiss();
                break;
            case R.id.btn_sound:
                Toast.makeText(this,"敬请期待",Toast.LENGTH_SHORT).show();
                customPopwindow.dismiss();
                break;
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
//        实例化控件
        listView = (ListView) findViewById(R.id.itemlist);
        FAB_new_one = (FloatingActionButton) findViewById(R.id.btn_newone);
        list = new ArrayList<Memorandum_JavaBean>();
        //查询数据
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        Log.i("execSQL","db实例化完成");
        Cursor cursor = null;
            String sql = "select * from "+ Constant.TABLE_NAME+" order by "+Constant.TIME+" desc;";
            Log.i("strsql",sql);
            cursor = DbManager.selectDataBySql(db,sql,null);
        if(cursor!=null) {
            Log.i("cursor","cursor不为空");
            while (cursor.moveToNext()) {
                Memorandum_JavaBean memorandum_javaBean = new Memorandum_JavaBean();
                memorandum_javaBean.id = cursor.getString(cursor.getColumnIndex("id"));
                memorandum_javaBean.time = cursor.getString(cursor.getColumnIndex("time"));
                memorandum_javaBean.substance = cursor.getString(cursor.getColumnIndex("substance"));
                list.add(memorandum_javaBean);
            }
        }
        db.close();//关闭数据库
        Log.i("execSQL","数据库已关闭");
        adapter = new MemorandumAdapter(this,list);
        listView.setAdapter(adapter);

        //        点击监听
        FAB_new_one.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sqliteHelper = DbManager.getIntance(this);
        initView();
    }



}
