package com.example.zero.memorandum.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.zero.memorandum.custom.PaintView;

import com.example.zero.memorandum.R;

/**
 * Created by Developer on 2017/6/27.
 */

public class Painter_activity extends Activity implements OnClickListener {

    private int penSize = 9;
    private Intent intent;
    private String fileName;

    //    注册控件
    private TextView btnRevokePaint;
    private TextView btnRedoPaint;
    private TextView btnCleanPaint;
    private TextView btnPenStylePaint;
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
        //        获取intent
        intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra("fileName")) {
//            获取intent中的值
                fileName = intent.getStringExtra("fileName");
//            为控件赋值
                initData(fileName);
            } else {
                initData();
            }
        }

        setTranslucentStatus(true);
    }

    private void initData(String fileName) {
        //获取的是屏幕宽高，通过控制freamlayout来控制涂鸦板大小
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight() - 110;
        paintViewPad = new PaintView(this, screenWidth, screenHeight);
        paint_linear.addView(paintViewPad);
        paintViewPad.requestFocus();
        paintViewPad.selectPaintSize(seekBar_pen_size.getProgress());
    }

    public void initView() {
//        实例化控件
        btnSavePaint = (TextView) findViewById(R.id.btn_save_paint);
        btnRevokePaint = (TextView) findViewById(R.id.btn_revoke_paint);
        btnRedoPaint = (TextView) findViewById(R.id.btn_redo_paint);
        btnCleanPaint = (TextView) findViewById(R.id.btn_clean_paint);
        btnPenStylePaint = (TextView) findViewById(R.id.btn_pen_style_paint);
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
                showMoreDialog(v);
                break;
            case R.id.btn_pen_color_paint:
                //选择画笔颜色
                showPaintColorDialog(v);
                break;
            case R.id.btn_back_paint:
                //返回
                showBackDialog();
                break;
            case R.id.btn_save_paint:
                //保存
                paintViewPad.saveToSDCard();
                finish();
                break;
        }
    }

    public void showBackDialog() {
        Dialog dialog = new android.app.AlertDialog.Builder(this).setTitle("是否保存已经编辑的内容？")
                .setIcon(R.drawable.ic_dialog_info)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“保存”后的操作，保存数据
                        paintViewPad.saveToSDCard();
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

    private int select_paint_color_index = 0;
    private int select_paint_style_index = 0;
    //private int select_paint_size_index = 0;

    /**
     * 显示画笔样式选项对话框
     */
    public void showMoreDialog(View parent) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("选择画笔或橡皮擦：");
        alertDialogBuilder.setSingleChoiceItems(R.array.paintstyle, select_paint_style_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_style_index = which;
                paintViewPad.selectPaintStyle(which);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    /**
     * 显示画笔颜色选择对话框
     */
    public void showPaintColorDialog(View parent) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("选择画笔颜色：");
        alertDialogBuilder.setSingleChoiceItems(R.array.paintcolor, select_paint_color_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_color_index = which;
                paintViewPad.selectPaintColor(which);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
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

    private void initData() {
        //获取的是屏幕宽高，通过控制freamlayout来控制涂鸦板大小
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight() - 102;
        paintViewPad = new PaintView(this, screenWidth, screenHeight);
        paint_linear.addView(paintViewPad);
        paintViewPad.requestFocus();
        paintViewPad.selectPaintSize(seekBar_pen_size.getProgress());
    }

    /**
     * 画笔尺寸选择监听
     */
    private class MySeekChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            penSize = seekBar.getProgress();
            paintViewPad.selectPaintSize(penSize);
            text_pen_size.setText("画笔尺寸：" + Integer.toString(penSize+1));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            penSize = seekBar.getProgress();
            paintViewPad.selectPaintSize(penSize);
            text_pen_size.setText("画笔尺寸：" + Integer.toString(penSize+1));
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
