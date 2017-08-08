package com.zdk.pojun.heartrec.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zdk.pojun.heartrec.AppData;
import com.zdk.pojun.heartrec.R;
import com.zdk.pojun.heartrec.adapter.Paint_Adapter;
import com.zdk.pojun.heartrec.adapter.Record_Adapter;
import com.zdk.pojun.heartrec.custom.CustomPopwindow;
import com.zdk.pojun.heartrec.entity.Paint_Entity;
import com.zdk.pojun.heartrec.entity.Record_Entity;
import com.zdk.pojun.heartrec.utils.Constant;
import com.zdk.pojun.heartrec.utils.DbManager;
import com.zdk.pojun.heartrec.adapter.Text_Adapter;
import com.zdk.pojun.heartrec.entity.Text_Entity;
import com.zdk.pojun.heartrec.utils.SqliteHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import floatingactionbutton.FloatingActionsMenu;

/**
 * Created by Zero on 2017/2/15.
 */

public class Main_activity extends FragmentActivity implements OnClickListener {

    private List<Text_Entity> listText;
    private List<Paint_Entity> listPaint;
    private List<Record_Entity> listRecord;
    private List<Record_Adapter.ViewHolder> viewHolderList;
    private MediaPlayer player;
    public boolean isPlaying = false;
    int playingItem = -1;//正在播放的项目，-1表示无
    private boolean runed = false;//是否允许过动画线程

    private Runnable switchRunnable, loopRunnable, toNormalRunnable, recoverRunnable;
    private Thread loopThread;
    private ExecutorService cacheThreadPool;
    private boolean running = true;

    private ListView listView;
    private Text_Adapter textAdapter;
    private Paint_Adapter paintAdapter;
    private Record_Adapter recordAdapter;
    private SqliteHelper sqliteHelper;

    //    注册控件
    private FloatingActionsMenu FAB_new_one;
    private FloatingActionButton fab_text,fab_paint,fab_record;
    private LinearLayout btnTextNavigation;
    private ImageView imageText;
    private TextView bottomBtnText;
    private LinearLayout btnPaintNavigation;
    private ImageView imagePaint;
    private TextView bottomBtnPaint;
    private LinearLayout btnRecordNavigation;
    private ImageView imageRecord;
    private TextView bottomBtnRecord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);

            // 状态栏背景色
            if (Build.VERSION.SDK_INT < 23) {
                tintManager.setStatusBarTintResource(R.color.colorAccent);
            } else {
                tintManager.setTintColor(getColor(R.color.colorAccent));
            }
        }


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
                            String sql = "delete from " + Constant.TABLE_NAME + " where " + Constant.ID + "=" + id + ";";
                            DbManager.execSQL(db, sql);
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
    public void deleteImage(final String filename) {
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

    //删除图片文件并刷新ListView
    public void deleteRecord(final String filename) {
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
                        initAdapter(3);
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

    //    右键菜单
    private void showDeleteRecordDia(final String filename) {
        AlertDialog.Builder multiDia = new AlertDialog.Builder(Main_activity.this);
        multiDia.setTitle("选择操作");
        multiDia.setPositiveButton("删除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                deleteRecord(filename);
            }
        });
        multiDia.create().show();
    }

    //      点击事件
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.fab_new_text:
                intent = new Intent(this, NewOne_activity.class);
                startActivity(intent);
                break;
            case R.id.fab_new_paint:
                intent = new Intent(this, Painter_activity.class);
                startActivity(intent);
                break;
            case R.id.fab_new_record:
                intent = new Intent(this, Record_activity.class);
                startActivity(intent);
                break;
            case R.id.btn_text_navigation:
                //切换到文字记事列表
                initAdapter(1);
                break;
            case R.id.btn_paint_navigation:
                //切换到图片记事列表
                initAdapter(2);
                break;
            case R.id.btn_record_navigation:
                //切换到录音记事列表
                initAdapter(3);
                break;
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
//        实例化控件
        listView = (ListView) findViewById(R.id.itemlist);
        FAB_new_one = (FloatingActionsMenu) findViewById(R.id.btn_newone);
        fab_text = (FloatingActionButton) findViewById(R.id.fab_new_text);
        fab_paint = (FloatingActionButton) findViewById(R.id.fab_new_paint);
        fab_record = (FloatingActionButton) findViewById(R.id.fab_new_record);
        btnTextNavigation = (LinearLayout) findViewById(R.id.btn_text_navigation);
        imageText = (ImageView) findViewById(R.id.image_text);
        bottomBtnText = (TextView) findViewById(R.id.bottom_btn_text);
        btnPaintNavigation = (LinearLayout) findViewById(R.id.btn_paint_navigation);
        imagePaint = (ImageView) findViewById(R.id.image_paint);
        bottomBtnPaint = (TextView) findViewById(R.id.bottom_btn_paint);
        btnRecordNavigation = (LinearLayout) findViewById(R.id.btn_record_navigation);
        imageRecord = (ImageView) findViewById(R.id.image_record);
        bottomBtnRecord = (TextView) findViewById(R.id.bottom_btn_record);


        cacheThreadPool = Executors.newCachedThreadPool();
        initAdapter(1);

        //        点击监听
        FAB_new_one.setOnClickListener(this);
        fab_text.setOnClickListener(this);
        fab_paint.setOnClickListener(this);
        fab_record.setOnClickListener(this);
        btnTextNavigation.setOnClickListener(this);
        btnPaintNavigation.setOnClickListener(this);
        btnRecordNavigation.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sqliteHelper = DbManager.getIntance(this);
        initView();
        initAdapter(AppData.getFinalPage());
    }

    /**
     * 初始化Adapter
     */
    private void initAdapter(int i) {
        initNavigation(i);
        switch (i) {
            case 1:
//                初始化文字适配器
                listText = new ArrayList<>();
                //查询数据
                SQLiteDatabase db = sqliteHelper.getWritableDatabase();
                Cursor cursor = null;
                String sql = "select * from " + Constant.TABLE_NAME + " order by " + Constant.TIME + " desc;";
                cursor = DbManager.selectDataBySql(db, sql, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        Text_Entity text_entity = new Text_Entity();
                        text_entity.setId(cursor.getString(cursor.getColumnIndex("id")));
                        text_entity.setTime(cursor.getString(cursor.getColumnIndex("time")));
                        text_entity.setSubstance(cursor.getString(cursor.getColumnIndex("substance")));
                        listText.add(text_entity);
                    }
                }
                db.close();//关闭数据库
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
                listView.setAdapter(textAdapter);
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
                listView.setAdapter(paintAdapter);
                break;
            case 3:
                //初始化录音适配器
                listRecord = new ArrayList<>();
                getRecord(listRecord);
                recordAdapter = new Record_Adapter(this, listRecord);

                viewHolderList = recordAdapter.getViewHolderList();

                //    item点击事件
                listView.setOnItemClickListener((new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                    long arg3) {
                                // TODO Auto-generated method stub);
                                final Record_Adapter.ViewHolder viewHolder = viewHolderList.get(arg2);
                                if (player != null && isPlaying && arg2 != playingItem) {
                                    stop();
                                    player = null;
                                    loopThread.interrupt();
                                    running = false;
                                }
                                for (int i = 0; i < viewHolderList.size(); i++) {
                                    TextView image = viewHolderList.get(i).image;
                                    TextView time = viewHolderList.get(i).time;
                                    image.setBackgroundResource(R.drawable.btn_play);
                                    time.setBackgroundResource(R.drawable.bg_player_normal);
                                }
                                if (player == null) {
                                    if (Build.VERSION.SDK_INT < 23) {
                                        player = MediaPlayer.create(Main_activity.this, Uri.parse("/mnt/" + listRecord.get(arg2).getFilename()));
                                    } else {
                                        player = new MediaPlayer();
                                        String uri = "/mnt/" + listRecord.get(arg2).getFilename();
                                        File file = new File(uri);
                                        try {
                                            FileInputStream fis = new FileInputStream(file);
                                            player.setDataSource(fis.getFD());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            isPlaying = false;
                                            player = null;
                                            loopThread.interrupt();
                                            running = false;
                                            viewHolder.image.setBackgroundResource(R.drawable.btn_play);
                                            viewHolder.time.setBackgroundResource(R.drawable.bg_player_normal);
                                            playingItem = -1;
                                        }
                                    });
                                }
                                if (isPlaying) {
                                    stop();
                                    player = null;
                                    loopThread.interrupt();
                                    running = false;
                                    playingItem = -1;
                                    viewHolder.image.setBackgroundResource(R.drawable.btn_play);
                                    viewHolder.time.setBackgroundResource(R.drawable.bg_player_normal);
                                } else {
                                    start();
                                    if (isPlaying) {
                                        viewHolder.image.setBackgroundResource(R.drawable.btn_pause);
                                        running = true;
                                        if (!runed || playingItem == -1) {
                                            updateSeekBar();
                                        }
                                        playingItem = arg2;
                                    } else {
                                        player = null;
                                    }
                                }

                            }
                        })


                );
//        item长按事件
                listView.setOnItemLongClickListener((new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                                   long arg3) {
                        showDeleteRecordDia(listRecord.get(arg2).getFilename());
                        return true;
                    }
                }));
                listView.setAdapter(recordAdapter);
                break;
        }


    }

    //同步seekbar与进度条时间
    private void updateSeekBar() {
        runed = true;
        loopRunnable = new Runnable() {
            @Override
            public void run() {
                if (running) {
                    try {
                        for (int i = 0; i < viewHolderList.size(); i++) {
                            if (i == playingItem) {
                                continue;
                            }
                            final TextView image = viewHolderList.get(i).image;
                            final TextView time = viewHolderList.get(i).time;
                            recoverRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (running) {
                                        image.setBackgroundResource(R.drawable.btn_play);
                                        time.setBackgroundResource(R.drawable.bg_player_normal);
                                    }
                                }
                            };
                            if (running) {
                                runOnUiThread(recoverRunnable);
                            }

                        }
                        if (playingItem >= 0)
                            displayAnim(viewHolderList.get(playingItem).time, true);
                        Thread.sleep(200);
                        if (playingItem >= 0)
                            displayAnim(viewHolderList.get(playingItem).time, false);
                        Thread.sleep(200);
                        updateSeekBar();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        loopThread = new Thread(loopRunnable);
        if (running) {
            cacheThreadPool.execute(loopThread);
        }
    }

    private void displayAnim(TextView textView, boolean bool) {
        if (player != null && !player.equals(null)) {
            if (bool) {
                for (int i = 1; i <= 5; i++) {
                    updateProgress(textView, i);
                }
            } else {
                for (int i = 5; i > 0; i--) {
                    updateProgress(textView, i);
                }
            }
        }
    }

    private void updateProgress(final TextView textView, final int type) {
        if (player != null && !player.equals(null) && isPlaying) {
            switchRunnable = new Runnable() {
                @Override
                public void run() {
                    if (running) {
                        switch (type) {
                            case 1:
                                textView.setBackgroundResource(R.drawable.bg_player_level1);
                                break;
                            case 2:
                                textView.setBackgroundResource(R.drawable.bg_player_level2);
                                break;
                            case 3:
                                textView.setBackgroundResource(R.drawable.bg_player_level3);
                                break;
                            case 4:
                                textView.setBackgroundResource(R.drawable.bg_player_level4);
                                break;
                            case 5:
                                textView.setBackgroundResource(R.drawable.bg_player_level5);
                                break;
                        }
                    }
                }
            };
            if (running) {
                runOnUiThread(switchRunnable);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            toNormalRunnable = new Runnable() {
                @Override
                public void run() {
                    if (running) {
                        textView.setBackgroundResource(R.drawable.bg_player_normal);
                    }
                }
            };
            if (running) {
                runOnUiThread(toNormalRunnable);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sqliteHelper = DbManager.getIntance(this);
        initView();
        initAdapter(AppData.getFinalPage());
    }

    private void getImage(List<Paint_Entity> list) {
        File file = new File(AppData.getImageFilePath());
        if (!file.exists()) {
            file.mkdirs();
        }
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

    private void getRecord(List<Record_Entity> list) {
        File file = new File(AppData.getRecordFilePath());
        if (!file.exists()) {
            file.mkdirs();
        }
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
                    if (suffix.toLowerCase().equals(".amr")) {
                        Record_Entity record_entity = new Record_Entity();
                        record_entity.setTime(fi.getName());
                        record_entity.setFilename(fi.getPath());
                        list.add(record_entity);
                    }
                }
            }
        }
    }

    private void initNavigation(int i) {
        bottomBtnPaint.setTextColor(getResources().getColor(R.color.text_black));
        bottomBtnText.setTextColor(getResources().getColor(R.color.text_black));
        bottomBtnRecord.setTextColor(getResources().getColor(R.color.text_black));
        imagePaint.setColorFilter(getResources().getColor(R.color.gray));
        imageText.setColorFilter(getResources().getColor(R.color.gray));
        imageRecord.setColorFilter(getResources().getColor(R.color.gray));
        switch (i) {
            case 1:
                bottomBtnText.setTextColor(getResources().getColor(R.color.colorAccent));
                imageText.setColorFilter(getResources().getColor(R.color.colorAccent));
                break;
            case 2:
                bottomBtnPaint.setTextColor(getResources().getColor(R.color.colorAccent));
                imagePaint.setColorFilter(getResources().getColor(R.color.colorAccent));
                break;
            case 3:
                bottomBtnRecord.setTextColor(getResources().getColor(R.color.colorAccent));
                imageRecord.setColorFilter(getResources().getColor(R.color.colorAccent));
                break;
        }
    }

    public void start() {
        if (!isPlaying) {
            try {
                if (Build.VERSION.SDK_INT > 23) {
                    player.prepare();
                }
                player.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "播放失败，文件损坏", Toast.LENGTH_LONG).show();
            } finally {
                isPlaying = true;
            }
        }
    }

    public void stop() {
        isPlaying = false;
        player.stop();
        player.release();
    }

}
