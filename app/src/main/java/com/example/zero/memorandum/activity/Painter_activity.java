package com.example.zero.memorandum.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

    //    注册控件
    private TextView btnRevokePaint;
    private TextView btnRedoPaint;
    private TextView btnCleanPaint;
    private TextView btnPenStylePaint;
    private TextView btnPenColorPaint;
    private TextView btnBackPaint;
    private TextView btnSavePaint;
    private PaintView paintViewPad;
    private FrameLayout framelayoutPaint;
    private LinearLayout paint_linear;
    private SeekBar pen_size;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint_layout);

        initView();
        initData();
        setTranslucentStatus(true);
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
        framelayoutPaint = (FrameLayout) findViewById(R.id.framelayout_paint);
        pen_size = (SeekBar) findViewById(R.id.seekbar_pen_size);
        paint_linear = (LinearLayout) findViewById(R.id.paint_linear);

        btnSavePaint.setOnClickListener(this);
        btnRevokePaint.setOnClickListener(this);
        btnRedoPaint.setOnClickListener(this);
        btnCleanPaint.setOnClickListener(this);
        btnPenStylePaint.setOnClickListener(this);
        btnPenColorPaint.setOnClickListener(this);
        btnBackPaint.setOnClickListener(this);
        pen_size.setOnSeekBarChangeListener(new MySeekChangeListener());

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
                finish();
                break;
            case R.id.btn_save_paint:
                //保存
                paintViewPad.saveToSDCard();
                break;
        }
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
        paintViewPad.selectPaintSize(pen_size.getProgress());
    }

    /**
     * 画笔尺寸选择监听
     */
    private class MySeekChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            paintViewPad.selectPaintSize(seekBar.getProgress());
            //Toast.makeText(Painter_activity.this,"当前画笔尺寸为"+seekBar.getProgress(),Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            paintViewPad.selectPaintSize(seekBar.getProgress());
            //Toast.makeText(Painter_activity.this,"当前画笔尺寸为"+seekBar.getProgress(),Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
