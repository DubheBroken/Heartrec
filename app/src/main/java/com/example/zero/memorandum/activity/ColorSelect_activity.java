package com.example.zero.memorandum.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.zero.memorandum.AppData;
import com.example.zero.memorandum.R;
import com.example.zero.memorandum.custom.ColorPickView;
import com.example.zero.memorandum.custom.ColorPickView.OnColorChangedListener;

/**
 * Created by Developer on 2017/7/4.
 */

public class ColorSelect_activity extends Activity implements OnCheckedChangeListener, OnClickListener {

    private int color;
    public static final int RESULT_CODE = 1;

    //    注册控件
    private ColorPickView colorPickerView;
    private RadioGroup radiogroupColorselect1;
    private RadioButton radioRed;
    private RadioButton radioOrange;
    private RadioButton radioYellow;
    private RadioButton radioGreen;
    private RadioButton radioBluegreen;
    private RadioGroup radiogroupColorselect2;
    private RadioButton radioBlue;
    private RadioButton radioPurple;
    private RadioButton radioPink;
    private RadioButton radioBlack;
    private RadioButton radioGray;
    private Button textColor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_picker_layout);
        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);

        initView();
    }

    private void initView() {
        colorPickerView = (ColorPickView) findViewById(R.id.color_picker_view);
        radiogroupColorselect1 = (RadioGroup) findViewById(R.id.radiogroup_colorselect1);
        radioRed = (RadioButton) findViewById(R.id.radio_red);
        radioOrange = (RadioButton) findViewById(R.id.radio_orange);
        radioYellow = (RadioButton) findViewById(R.id.radio_yellow);
        radioGreen = (RadioButton) findViewById(R.id.radio_green);
        radioBluegreen = (RadioButton) findViewById(R.id.radio_bluegreen);
        radiogroupColorselect2 = (RadioGroup) findViewById(R.id.radiogroup_colorselect2);
        radioBlue = (RadioButton) findViewById(R.id.radio_blue);
        radioPurple = (RadioButton) findViewById(R.id.radio_purple);
        radioPink = (RadioButton) findViewById(R.id.radio_pink);
        radioBlack = (RadioButton) findViewById(R.id.radio_black);
        radioGray = (RadioButton) findViewById(R.id.radio_gray);
        textColor = (Button) findViewById(R.id.text_color);

        radiogroupColorselect1.setOnCheckedChangeListener(this);
        radiogroupColorselect2.setOnCheckedChangeListener(this);

        textColor.setOnClickListener(this);

        colorPickerView.setOnColorChangedListener(new OnColorChangedListener() {

            @Override
            public void onColorChange(int color) {
                ColorSelect_activity.this.color = color;
                textColor.setTextColor(color);
            }

        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (group.getId()) {
            case R.id.radiogroup_colorselect1:
                radioRed.setChecked(false);
                radioOrange.setChecked(false);
                radioYellow.setChecked(false);
                radioGreen.setChecked(false);
                radioBluegreen.setChecked(false);
                switch (checkedId) {
                    case R.id.radio_red:
                        color = getColor(R.color.red);
                        break;
                    case R.id.radio_orange:
                        color = getColor(R.color.orange);
                        break;
                    case R.id.radio_yellow:
                        color = getColor(R.color.yellow);
                        break;
                    case R.id.radio_green:
                        color = getColor(R.color.green);
                        break;
                    case R.id.radio_bluegreen:
                        color = getColor(R.color.bluegreen);
                        break;
                }
                break;
            case R.id.radiogroup_colorselect2:
                radioBlue.setChecked(false);
                radioPurple.setChecked(false);
                radioPink.setChecked(false);
                radioBlack.setChecked(false);
                radioGray.setChecked(false);
                switch (checkedId) {
                    case R.id.radio_blue:
                        color = getColor(R.color.blue);
                        break;
                    case R.id.radio_purple:
                        color = getColor(R.color.purple);
                        break;
                    case R.id.radio_pink:
                        color = getColor(R.color.pink);
                        break;
                    case R.id.radio_black:
                        color = getColor(R.color.black);
                        break;
                    case R.id.radio_gray:
                        color = getColor(R.color.gray);
                        break;
                }
                break;
        }
        textColor.setTextColor(color);
    }

    private void result(){
        Log.i("---onDestroy---", "" + color);
        Intent intent = new Intent();
        intent.putExtra("color", color);
        setResult(RESULT_CODE, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_color:
                result();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        result();
    }
}
