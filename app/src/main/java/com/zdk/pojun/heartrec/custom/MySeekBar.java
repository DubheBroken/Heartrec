package com.zdk.pojun.heartrec.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Developer on 2017/7/7.
 */

public class MySeekBar extends android.support.v7.widget.AppCompatSeekBar {

    public MySeekBar(Context context) {
        super(context);
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * onTouchEvent 是在 SeekBar 继承的抽象类 AbsSeekBar 里
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //原来是要将TouchEvent传递下去的,我们不让它传递下去就行了
        //return super.onTouchEvent(event);

        return false ;
    }





}
