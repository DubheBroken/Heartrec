package com.example.zero.memorandum.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zero.memorandum.AppData;
import com.example.zero.memorandum.R;
import com.example.zero.memorandum.adapter.Paint_Adapter;
import com.example.zero.memorandum.custom.CustomPopwindow;
import com.example.zero.memorandum.entity.Paint_Entity;
import com.example.zero.memorandum.fragment.Bottom_Fragment;
import com.example.zero.memorandum.utils.Constant;
import com.example.zero.memorandum.utils.DbManager;
import com.example.zero.memorandum.adapter.Text_Adapter;
import com.example.zero.memorandum.entity.Text_Entity;
import com.example.zero.memorandum.utils.SqliteHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zero on 2017/2/15.
 */

public class Main_activity extends FragmentActivity implements OnClickListener {

    private List<Text_Entity> listText;
    private List<Paint_Entity> listPaint;

    private ListView listView;
    private Text_Adapter textAdapter;
    private Paint_Adapter paintAdapter;
    private SqliteHelper sqliteHelper;

    //    Fragment相关
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private Bottom_Fragment bottom_fragment = new Bottom_Fragment();

    //    注册控件
    private TextView bottomBtnText;
    private TextView bottomBtnPaint;
    private TextView bottomBtnRecord;
    private FloatingActionButton FAB_new_one;
    private CustomPopwindow customPopwindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);


//        初始化页面切换相关组件
        fragmentManager = getSupportFragmentManager();

//        请求权限
        ActivityCompat.requestPermissions(Main_activity.this, new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.SET_WALLPAPER,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);

        sqliteHelper = DbManager.getIntance(this);
        initView();


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

    //    删除记录并刷新ListView
    public void deleteText(final String id) {

        new AlertDialog.Builder(this).setTitle("确认要删除吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
//                            删除自动保存的数据
                        SQLiteDatabase db = sqliteHelper.getWritableDatabase();//打开数据库
                        try {
                            String sql = "deleteText from " + Constant.TABLE_NAME + " where " + Constant.ID + "=" + id + ";";
                            Log.i("strsql", sql);
                            DbManager.execSQL(db, sql);
                            Log.i("execSQL", "删除数据成功");
                        } catch (Exception e) {
                            Log.e("execSQL", "删除数据出错");
                        }
                        initView();
                        initAdapter(1);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();

    }

    //删除图片文件并刷新ListView
    public void deleteImage(final String filename){
        new AlertDialog.Builder(this).setTitle("确认要删除吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        File file = new File(filename);
                        try {
                            file.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        initView();
                        initAdapter(2);
                        listView.setAdapter(paintAdapter);
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
    private void showDeleteTextDia(final String id) {
        AlertDialog.Builder multiDia = new AlertDialog.Builder(Main_activity.this);
        multiDia.setTitle("选择操作");
        multiDia.setPositiveButton("删除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                deleteText(id);
            }
        });
        multiDia.create().show();
    }

    //    右键菜单
    private void showDeleteImageDia(final String filename) {
        AlertDialog.Builder multiDia = new AlertDialog.Builder(Main_activity.this);
        multiDia.setTitle("选择操作");
        multiDia.setPositiveButton("删除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                deleteImage(filename);
            }
        });
        multiDia.create().show();
    }

    //      点击事件
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_newone:
                customPopwindow = new CustomPopwindow(this, this);
                customPopwindow.setBackgroundDrawable(null);
                customPopwindow.showAsDropDown(v, -20, -620, Gravity.TOP);
                break;
            case R.id.btn_text:
                intent = new Intent(this, NewOne_activity.class);
                startActivity(intent);
                customPopwindow.dismiss();
                break;
            case R.id.btn_picture:
                intent = new Intent(this, Painter_activity.class);
                startActivity(intent);
                customPopwindow.dismiss();
                break;
            case R.id.btn_sound:
                Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
                customPopwindow.dismiss();
                break;
            case R.id.bottom_btn_text:
                //切换到文字记事列表
                initAdapter(1);
                listView.setAdapter(textAdapter);
                break;
            case R.id.bottom_btn_paint:
                //切换到图片记事列表
                initAdapter(2);
                listView.setAdapter(paintAdapter);
                break;
            case R.id.bottom_btn_record:
                //切换到录音记事列表
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
        bottomBtnText = (TextView) findViewById(R.id.bottom_btn_text);
        bottomBtnPaint = (TextView) findViewById(R.id.bottom_btn_paint);
        bottomBtnRecord = (TextView) findViewById(R.id.bottom_btn_record);

        initAdapter(1);

        //        点击监听
        FAB_new_one.setOnClickListener(this);
        bottomBtnText.setOnClickListener(this);
        bottomBtnPaint.setOnClickListener(this);
        bottomBtnRecord.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sqliteHelper = DbManager.getIntance(this);
        initView();
        initAdapter(1);
    }

    /**
     * 初始化Adapter
     */
    private void initAdapter(int i) {
        switch (i) {
            case 1:
//                初始化文字适配器
                    listText = new ArrayList<>();
                    //查询数据
                    SQLiteDatabase db = sqliteHelper.getWritableDatabase();
                    Log.i("execSQL", "db实例化完成");
                    Cursor cursor = null;
                    String sql = "select * from " + Constant.TABLE_NAME + " order by " + Constant.TIME + " desc;";
                    Log.i("strsql", sql);
                    cursor = DbManager.selectDataBySql(db, sql, null);
                    if (cursor != null) {
                        Log.i("cursor", "cursor不为空");
                        while (cursor.moveToNext()) {
                            Text_Entity text_entity = new Text_Entity();
                            text_entity.setId(cursor.getString(cursor.getColumnIndex("id")));
                            text_entity.setTime(cursor.getString(cursor.getColumnIndex("time")));
                            text_entity.setSubstance(cursor.getString(cursor.getColumnIndex("substance")));
                            listText.add(text_entity);
                        }
                    }
                    db.close();//关闭数据库
                    Log.i("execSQL", "数据库已关闭");
                    textAdapter = new Text_Adapter(this, listText);
                    listView.setAdapter(textAdapter);

                //    item点击事件
                listView.setOnItemClickListener((new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                    long arg3) {
                                // TODO Auto-generated method stub);
                                Intent intent = new Intent(Main_activity.this, NewOne_activity.class);
                                intent.putExtra("id", listText.get(arg2).getId());//将被点击的item id传递到新活动
                                Log.i("-------intent-------", "Extra=" + listText.get(arg2).getId());
                                startActivity(intent);
                            }
                        })
                );
//        item长按事件
                listView.setOnItemLongClickListener((new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                                   long arg3) {
                        showDeleteTextDia(listText.get(arg2).getId());
                        return true;
                    }
                }));

                break;
            case 2:
                //初始化图片适配器
                    listPaint = new ArrayList<>();
                    getImage(listPaint);
                    paintAdapter = new Paint_Adapter(this, listPaint);

                //    item点击事件
                listView.setOnItemClickListener((new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                    long arg3) {
                                // TODO Auto-generated method stub);
                                Intent intent = new Intent(Main_activity.this, Painter_activity.class);
                                intent.putExtra("fileName", listPaint.get(arg2).getFilename());//将被点击的item文件名传递到新活动
                                startActivity(intent);
                            }
                        })
                );
//        item长按事件
                listView.setOnItemLongClickListener((new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                                   long arg3) {
                        showDeleteImageDia(listPaint.get(arg2).getFilename());
                        return true;
                    }
                }));
                break;
            case 3:
                //初始化录音适配器

                break;
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sqliteHelper = DbManager.getIntance(this);
        initView();
        initAdapter(1);
    }

    private void getImage(List<Paint_Entity> list) {
        File file = new File(AppData.getImageFilePath());
        File[] allfiles = file.listFiles();
        if (allfiles != null) {
            for (int i = 0; i < allfiles.length; i++) {
                File fi = allfiles[i];
                if (fi.isFile()) {
                    int idx = fi.getPath().lastIndexOf(".");
                    if (idx <= 0) {
                        continue;
                    }
                    String suffix = fi.getPath().substring(idx);
                    if (suffix.toLowerCase().equals(".png")) {
                        Paint_Entity paint_entity = new Paint_Entity();
                        paint_entity.setTime(fi.getName());
                        paint_entity.setFilename(fi.getPath());
                        list.add(paint_entity);
                    }
                }
            }
        }
    }

}
