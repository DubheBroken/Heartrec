package com.example.zero.memorandum.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zero.memorandum.AppData;
import com.example.zero.memorandum.custom.PaintView;

import com.example.zero.memorandum.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Developer on 2017/6/27.
 */

public class Painter_activity extends Activity implements OnClickListener {

    private Intent intent;
    private String fileName;
    private SharedPreferences sp;
    private List<String> paintColorList;
    private final static int REQUEST_CODE = 1;
    private int select_paint_style_index = 1;//1笔，0橡皮

    //    注册控件
    private TextView btnRevokePaint;
    private TextView btnRedoPaint;
    private TextView btnCleanPaint;
    private ImageView btnPenStylePaint;
    private TextView btnPenColorPaint;
    private TextView btnBackPaint;
    private TextView btnSavePaint;
    private TextView text_pen_size;
    private PaintView paintViewPad;
    private FrameLayout framelayoutPaint;
    private LinearLayout paint_linear;
    private SeekBar seekBar_pen_size;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint_layout);

        initView();
        AppData.setFinalPage(2);

        //        获取intent
        intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra("fileName")) {
//            获取intent中的值
                fileName = intent.getStringExtra("fileName");
//            为控件赋值
                initData(fileName);
            } else {
                initData(null);
                //获得系统当前时间，并以该时间作为文件名
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                fileName = AppData.getImageFilePath() + "paint" + formatter.format(curDate) + ".png";
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
    }

    @Override
    public void onBackPressed() {
        showBackDialog();
    }

    private void initData(String fileName) {
        //获取的是屏幕宽高，通过控制freamlayout来控制涂鸦板大小
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight() - 110;
        paintViewPad = new PaintView(this, screenWidth, screenHeight, fileName);
        paint_linear.addView(paintViewPad);
        paintViewPad.requestFocus();
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        AppData.setPenColor(sp.getInt("pencolor", -65536));
        Log.i("---PenColor---",""+sp.getInt("pencolor", 0xff0000));
        AppData.setPenSize(sp.getInt("pensize", 9));
        paintColorList = Arrays.asList(getResources().getStringArray(R.array.paintcolor));
        paintViewPad.selectPaintColor(AppData.getPenColor());
        paintViewPad.selectPaintStyle(select_paint_style_index);
        seekBar_pen_size.setProgress(AppData.getPenSize());
        paintViewPad.selectPaintSize(seekBar_pen_size.getProgress());

    }

    public void initView() {
//        实例化控件
        btnSavePaint = (TextView) findViewById(R.id.btn_save_paint);
        btnRevokePaint = (TextView) findViewById(R.id.btn_revoke_paint);
        btnRedoPaint = (TextView) findViewById(R.id.btn_redo_paint);
        btnCleanPaint = (TextView) findViewById(R.id.btn_clean_paint);
        btnPenStylePaint = (ImageView) findViewById(R.id.btn_pen_style_paint);
        btnPenColorPaint = (TextView) findViewById(R.id.btn_pen_color_paint);
        btnBackPaint = (TextView) findViewById(R.id.btn_back_paint);
        text_pen_size = (TextView) findViewById(R.id.text_pen_size);
        framelayoutPaint = (FrameLayout) findViewById(R.id.framelayout_paint);
        seekBar_pen_size = (SeekBar) findViewById(R.id.seekbar_pen_size);
        paint_linear = (LinearLayout) findViewById(R.id.paint_linear);

        btnSavePaint.setOnClickListener(this);
        btnRevokePaint.setOnClickListener(this);
        btnRedoPaint.setOnClickListener(this);
        btnCleanPaint.setOnClickListener(this);
        btnPenStylePaint.setOnClickListener(this);
        btnPenColorPaint.setOnClickListener(this);
        btnBackPaint.setOnClickListener(this);
        seekBar_pen_size.setOnSeekBarChangeListener(new MySeekChangeListener());

        btnPenStylePaint.getBackground().setLevel(0);
//        btnRevokePaint.setEnabled(false);
//        btnRedoPaint.setEnabled(false);

//        paintViewPad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                btnRevokePaint.setEnabled(paintViewPad.haveSavePath());
//                btnRedoPaint.setEnabled(paintViewPad.haveDeletePath());
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_revoke_paint:
                //撤销
                paintViewPad.undo();
                break;
            case R.id.btn_redo_paint:
                //重做
                paintViewPad.recover();
                break;
            case R.id.btn_clean_paint:
                //清空
                paintViewPad.clean();
                break;
            case R.id.btn_pen_style_paint:
                //设置画笔样式
                changePenStyle();
                break;
            case R.id.btn_pen_color_paint:
                //选择画笔颜色
                intent = new Intent(this, ColorSelect_activity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_back_paint:
                //返回
                showBackDialog();
                break;
            case R.id.btn_save_paint:
                //保存
                paintViewPad.saveToSDCard(fileName);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AppData.setPenColor(data.getIntExtra("color", 0xff0000));
        paintViewPad.selectPaintColor(AppData.getPenColor());
        Log.i("---onActivityResult---", "" + AppData.getPenColor());
    }

    public void showBackDialog() {
        Dialog dialog = new android.app.AlertDialog.Builder(this).setTitle("是否保存已经编辑的内容？")
                .setIcon(R.drawable.ic_dialog_info)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“保存”后的操作，保存数据
                        paintViewPad.saveToSDCard(fileName);
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

    /**
     * 显示画笔样式选项对话框
     */
    public void changePenStyle() {
        btnPenStylePaint.getBackground().setLevel(select_paint_style_index);
        switch (select_paint_style_index) {
            case 0:
                select_paint_style_index = 1;
                Toast.makeText(this, "切换到画笔", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                select_paint_style_index = 0;
                Toast.makeText(this, "切换到橡皮", Toast.LENGTH_SHORT).show();
                break;
        }
        paintViewPad.selectPaintStyle(select_paint_style_index);
    }


    /**
     * 沉浸式状态栏
     */
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

    @Override
    protected void onDestroy() {
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("pencolor", AppData.getPenColor());
        editor.putInt("pensize", AppData.getPenSize());
        editor.apply();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        //避免style定义的转场退出时候出现2次
        this.overridePendingTransition(0, 0);
        super.onPause();
    }

    /**
     * 画笔尺寸选择监听
     */
    private class MySeekChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            AppData.setPenSize(seekBar.getProgress());
            paintViewPad.selectPaintSize(AppData.getPenSize());
            text_pen_size.setText("画笔尺寸：" + Integer.toString(AppData.getPenSize() + 1));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            AppData.setPenSize(seekBar.getProgress());
            paintViewPad.selectPaintSize(AppData.getPenSize());
            text_pen_size.setText("画笔尺寸：" + Integer.toString(AppData.getPenSize() + 1));
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
