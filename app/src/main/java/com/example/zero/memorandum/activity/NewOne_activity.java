package com.example.zero.memorandum.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zero.memorandum.AppData;
import com.example.zero.memorandum.R;
import com.example.zero.memorandum.utils.Constant;
import com.example.zero.memorandum.utils.DbManager;
import com.example.zero.memorandum.entity.Text_Entity;
import com.example.zero.memorandum.utils.SqliteHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zero on 2017/2/16.
 */

public class NewOne_activity extends Activity implements OnClickListener {

    private Context context = this;

    private Intent intent;
    private String id = "";

    private SqliteHelper sqliteHelper;
    //    注册控件
    private EditText editText_substance;
    private TextView textView_time, btn_save, btn_cancel;
    private LinearLayout layout;

    //    数据变量
    private String nowtime, substance;

//    自动保存计时器
//    final Handler handler = new Handler();


    @Override
    public void onBackPressed() {
        cancel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newone_layout);

        //        获取时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date curtime = new Date(System.currentTimeMillis());//获取当前时间
        nowtime = formatter.format(curtime);

        initView();//初始化界面
        AppData.setFinalPage(1);

//        获取intent
        intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra("id")) {
//            获取intent中的值
                id = intent.getStringExtra("id");
//            为控件赋值
                initData(id);
            }
        }

        //        沉浸式状态栏
        // 4.4以上版本开启
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);


            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);

            // 状态栏背景色
            tintManager.setTintColor(getColor(R.color.colorAccent));
        }


//        自动保存

//        Runnable runnable = new Runnable(){
//            @Override
//            public void run() {
//            // TODO Auto-generated method stub
//            // 在此处添加自动执行的代码
////                刷新标题合和内容
//                if (id_value==-1) {
//                    save(-1);
//                    handler.postDelayed(this, interval);// 延迟间隔
//                    Log.i("delay", "延迟" + interval / 1000 + "秒");
//                }
//            }
//        };
//        handler.postDelayed(runnable, interval);// 打开定时器，执行操作
//        //handler.removeCallbacks(runnable);// 关闭定时器处理
//
//        内容改变监听
        //        沉浸式状态栏
        // 4.4及以上版本开启
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//        }
//
//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setNavigationBarTintEnabled(true);
//
//        // 状态栏背景色
//        tintManager.setTintColor(getColor(R.color.white));
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

    private void initView() {
        sqliteHelper = DbManager.getIntance(this);
//        option_helper = new OptionHelper(context);
        //        实例化控件
        editText_substance = (EditText) findViewById(R.id.edittext_substance);
        btn_save = (TextView) findViewById(R.id.btn_save);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        textView_time = (TextView) findViewById(R.id.text_time);
        layout = (LinearLayout) findViewById(R.id.layout_newone);

//        点击监听
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        editText_substance.addTextChangedListener(textWatcher);
        textView_time.setText(nowtime);
        //        改变背景图颜色
//        changeBg();

    }


//    点击事件


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_save:
                save();
                finish();
                break;
        }
    }

//    取消

    public void cancel() {
        if (!"".equals(id)) {//id不为空，界面从已存在的条目创建
            //查询是否更改过内容
            SQLiteDatabase db = sqliteHelper.getWritableDatabase();
            Log.i("execSQL", "db实例化完成");
            Cursor cursor = null;
            String sql = "select * from " + Constant.TABLE_NAME + " where " + Constant.ID + "=" + id + ";";
            Log.i("strsql", sql);
            cursor = DbManager.selectDataBySql(db, sql, null);
            if (cursor != null) {
                Log.i("cursor", "cursor不为空");
                cursor.moveToNext();
                Text_Entity text_entity = new Text_Entity();
                text_entity.setId(cursor.getString(cursor.getColumnIndex("id")));
                text_entity.setTime(cursor.getString(cursor.getColumnIndex("time")));
                text_entity.setSubstance(cursor.getString(cursor.getColumnIndex("substance")));

                db.close();//关闭数据库
                Log.i("execSQL", "数据库已关闭");
                if (text_entity.getSubstance().equals(substance)) {//没有改变内容
                    finish();//直接关闭
                } else {
                    showDialog();//询问是否保存
                }
            }
        } else {
            //界面为新建界面
            if (substance == null || "".equals(substance)) {//内容为空，用户没有输入
                finish();
            } else {
                showDialog();
            }
        }
    }

    public void showDialog() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("是否保存已经编辑的内容？")
                .setIcon(R.drawable.ic_dialog_info)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“保存”后的操作，保存数据
                        save();
                        finish();
                    }
                })
                .setNegativeButton("不保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“不保存”后的操作，关闭界面
                        finish();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“取消”后的操作，不做任何操作
                    }
                })
                .show();
        WindowManager.LayoutParams params =
                dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = 480;
        dialog.getWindow().setAttributes(params);
    }

    //    删除记录
    public void delete(int id) {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();//打开数据库
        try {
            String sql = "deleteText from " + Constant.TABLE_NAME + " where " + Constant.ID + "=\"" + id + "\";";
            Log.i("strsql", sql);
            DbManager.execSQL(db, sql);
            Log.i("execSQL", "删除数据成功");
        } catch (Exception e) {
            Log.e("execSQL", "删除数据出错");
        }
    }

    //    保存
    public void save() {
        Log.i("---click save---", "点击保存按钮");
        if (substance != null && !"".equals(substance)) {
            SQLiteDatabase db = sqliteHelper.getWritableDatabase();//打开数据库
            if (id != null && !id.equals("")) {
                try {
                    String sql = "update " + Constant.TABLE_NAME + " set " + "" + Constant.SUBSTANCE + "=\"" + substance + "\"," + Constant.TIME + "=\"" + nowtime + "\" where " + Constant.ID + "=" + id + ";";
                    Log.i("strsql", sql);
                    DbManager.execSQL(db, sql);
                    Log.i("---execSQL---", "更新数据成功");
                } catch (Exception e) {
                    Log.e("---execSQL---", "更新数据失败");
                }
            } else {
                try {
                    String sql = "insert into " + Constant.TABLE_NAME + " values(null,'" + substance + "','" + nowtime + "');";
                    Log.i("strsql", sql);
                    DbManager.execSQL(db, sql);
                    Log.i("---execSQL---", "插入数据成功");
                } catch (Exception e) {
                    Log.e("---execSQL---", "插入数据失败");
                }
            }
            db.close();//关闭数据库
            Log.i("---SQL---", "数据库已关闭");
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            //文本内容变化时

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            //文本内容变化前

        }

        @Override
        public void afterTextChanged(Editable s) {
            //文本内容变化后
            substance = editText_substance.getText().toString();
        }
    };

    /**
     * 查询并赋值控件
     */
    private void initData(String id) {
        //查询数据
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        String sql = "select * from " + Constant.TABLE_NAME + " where " + Constant.ID + "=" + id + ";";
        Cursor cursor = DbManager.selectDataBySql(db, sql, null);
        if (cursor != null) {
            Log.i("cursor", "cursor不为空");
//                为控件赋值
            if (cursor.moveToFirst()) {
                editText_substance.setText(cursor.getString(cursor.getColumnIndex("substance")));
            }
        }
        db.close();//关闭数据库
        Log.i("execSQL", "数据库已关闭");
    }

//    【已弃用】自动保存
//    private class ProvOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

//        @Override
//        public void onItemSelected(AdapterView<?> adapter, View view, int position, long id){
//            //获取选择的项的值
//            switch (view.getId()) {
//                case R.id.spinner_interval:
//                    switch (spinner_interval.getSelectedItem().toString()) {
//                        case "1秒":
//                            interval=1000;
//                            Log.i("interval","自动保存延迟变为1秒");
//                            break;
//                        case "3秒":
//                            interval=3000;
//                            Log.i("interval","自动保存延迟变为3秒");
//                            break;
//                        case "5秒":
//                            interval=5000;
//                            Log.i("interval","自动保存延迟变为5秒");
//                            break;
//                        case "10秒":
//                            interval=10000;
//                            Log.i("interval","自动保存延迟变为10秒");
//                    }
//                    break;
//
//            }
//        }

//        @Override
//        public void onNothingSelected(AdapterView<?> arg0) {
//            //未选中任何选项
//        }
//
//
//    }

//    private void changeBg(){
//        switch (option_helper.getSkin()) {
//            case "蓝色":
//                layout.setBackground(context.getDrawable(R.mipmap.blue));
//                break;
//            case "绿色":
//                layout.setBackground(context.getDrawable(R.mipmap.green));
//                break;
//            case "白色":
//                layout.setBackground(context.getDrawable(R.mipmap.white));
//                break;
//            case "红色":
//                layout.setBackground(context.getDrawable(R.mipmap.red));
//                break;
//            case "粉色":
//                layout.setBackground(context.getDrawable(R.mipmap.pink));
//                break;
//            case "黄色":
//                layout.setBackground(context.getDrawable(R.mipmap.yellow));
//                break;
//            case "青色":
//                layout.setBackground(context.getDrawable(R.mipmap.bluegreen));
//                break;
//            case "深蓝色":
//                layout.setBackground(context.getDrawable(R.mipmap.bluepurple));
//                break;
//            case "紫色":
//                layout.setBackground(context.getDrawable(R.mipmap.purple));
//                break;
//            default:
//                layout.setBackground(context.getDrawable(R.mipmap.yellow));
//                break;
//        }
//    }


}
