package com.example.zero.memorandum.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.zero.memorandum.R;


/**
 * Created by Administrator on 2016/4/12.
 */
public class CustomPopwindow extends PopupWindow {
    private View mView;
    public CustomPopwindow(Activity context, View.OnClickListener itemsOnClick){
        super(context);
        initView(context,itemsOnClick);
    }

    private void initView(final Activity context,View.OnClickListener itemsOnClick) {
        // TODO Auto-generated method stub
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.popwindow_new_one,null);
        Button btn_text = (Button) mView.findViewById(R.id.btn_text);
        Button btn_picture = (Button) mView.findViewById(R.id.btn_picture);
        Button btn_sound = (Button) mView.findViewById(R.id.btn_sound);
        //设置按钮监听
        btn_text.setOnClickListener(itemsOnClick);
        btn_picture.setOnClickListener(itemsOnClick);
        btn_sound.setOnClickListener(itemsOnClick);
        //设置SelectPicPopupWindow的View
        this.setContentView(mView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置PopupWindow可触摸
        this.setTouchable(true);
        //设置非PopupWindow区域是否可触摸
//        this.setOutsideTouchable(false);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.select_anim);
        //实例化一个ColorDrawable颜色为半透明
        //设置SelectPicPopupWindow弹出窗体的背景
//        backgroundAlpha(context,0.5f);//0.0-1.0
//        this.setOnDismissListener(new OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                // TODO Auto-generated method stub
//                backgroundAlpha(context, 1f);
//            }
//        });
        this.setAnimationStyle(R.style.mypopwindow_anim_style);
//        设置进入退出动画
    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }



}