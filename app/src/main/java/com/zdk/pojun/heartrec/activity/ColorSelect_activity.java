package com.zdk.pojun.heartrec.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zdk.pojun.heartrec.AppData;
import com.zdk.pojun.heartrec.R;
import com.zdk.pojun.heartrec.custom.ColorPickView;
import com.zdk.pojun.heartrec.custom.ColorPickView.OnColorChangedListener;

/**
 * Created by Developer on 2017/7/4.
 */

public class ColorSelect_activity extends Activity implements OnClickListener {

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
        colorPickerView = findViewById(R.id.color_picker_view);
        radioRed = findViewById(R.id.radio_red);
        radioOrange = findViewById(R.id.radio_orange);
        radioYellow = findViewById(R.id.radio_yellow);
        radioGreen = findViewById(R.id.radio_green);
        radioBluegreen = findViewById(R.id.radio_bluegreen);
        radioBlue = findViewById(R.id.radio_blue);
        radioPurple = findViewById(R.id.radio_purple);
        radioPink = findViewById(R.id.radio_pink);
        radioBlack = findViewById(R.id.radio_black);
        radioGray = findViewById(R.id.radio_gray);
        textColor = findViewById(R.id.text_color);

        textColor.setOnClickListener(this);
        radioRed.setOnClickListener(this);
        radioOrange.setOnClickListener(this);
        radioYellow.setOnClickListener(this);
        radioGreen.setOnClickListener(this);
        radioBluegreen.setOnClickListener(this);
        radioBlue.setOnClickListener(this);
        radioPurple.setOnClickListener(this);
        radioPink.setOnClickListener(this);
        radioBlack.setOnClickListener(this);
        radioGray.setOnClickListener(this);

        colorPickerView.setOnColorChangedListener(new OnColorChangedListener() {

            @Override
            public void onColorChange(int color) {
                ColorSelect_activity.this.color = color;
                textColor.setTextColor(color);
            }

        });
    }


    private void result() {
        Intent intent = new Intent();
        intent.putExtra("color", color);
        if (color == 0) {
            intent = new Intent();
            intent.putExtra("color", AppData.getPenColor());
            setResult(RESULT_CODE, intent);
        }
        setResult(RESULT_CODE, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_color:
                result();
                break;
            case R.id.radio_red:
                color = getResources().getColor(R.color.red);
                break;
            case R.id.radio_orange:
                color = getResources().getColor(R.color.orange);
                break;
            case R.id.radio_yellow:
                color = getResources().getColor(R.color.yellow);
                break;
            case R.id.radio_green:
                color = getResources().getColor(R.color.green);
                break;
            case R.id.radio_bluegreen:
                color = getResources().getColor(R.color.bluegreen);
                break;
            case R.id.radio_blue:
                color = getResources().getColor(R.color.blue);
                break;
            case R.id.radio_purple:
                color = getResources().getColor(R.color.purple);
                break;
            case R.id.radio_pink:
                color = getResources().getColor(R.color.pink);
                break;
            case R.id.radio_black:
                color = getResources().getColor(R.color.black);
                break;
            case R.id.radio_gray:
                color = getResources().getColor(R.color.gray);
                break;
        }
        if(v.getId()!=R.id.text_color){
            textColor.setTextColor(color);
        }
    }

    @Override
    public void onBackPressed() {
        result();
    }
}
