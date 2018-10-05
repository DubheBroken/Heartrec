package com.first.hdz.customview.swipelayout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;


public class SwipeLayout extends ViewGroup {
    private Scroller mScroller;

    private int mLastX;

    //用于决定是否将 SwipeLayout 退回的标志距离,即滑动第二个子空间的长度
    private int FlagWidthBack = 0;
    private int FlagWidthIntercept = 0;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            this.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            this.postInvalidate();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int realWidth = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i == 0) {
                FlagWidthBack = 0;
                FlagWidthIntercept = 0;
            }
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            if (i == 1) {
                FlagWidthBack = child.getMeasuredWidth();
            }
            if (i != 0) {
                FlagWidthIntercept += child.getMeasuredWidth();
            }
            realWidth += child.getMeasuredWidth();
        }
        setMeasuredDimension(realWidth, height);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int top = 0;
        int left = 0;
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        top += paddingTop;
        left += paddingLeft;
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            left += child.getMeasuredWidth();
        }
        scrollTo(0, 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 事件拦截
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            return false;
        } else {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mLastX = (int) ev.getRawX();
                    return false;
                case MotionEvent.ACTION_MOVE:
                    int offset = (int) ev.getRawX() - mLastX;
                    int scrollX = getScrollX();
                    if (offset < 0 && scrollX > FlagWidthIntercept) {

                        return true;
                    }
                    if (offset > 0 && scrollX <= 0) {
                        return true;
                    }
            }
            return false;
        }
    }

    /**
     * 事件消费
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_UP: {
                int scrollX = getScrollX();
                if (scrollX > FlagWidthIntercept) {
                    mScroller.startScroll(getScrollX(), getScrollY(), FlagWidthIntercept - getScrollX(), 0);
                } else if (scrollX <= FlagWidthIntercept && scrollX >= FlagWidthBack) {
                    mScroller.startScroll(getScrollX(), getScrollY(), FlagWidthIntercept - getScrollX(), 0);
                } else if (scrollX < FlagWidthBack && scrollX > 0) {
                    mScroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), 0);
                }
                invalidate();
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                int currentX = (int) event.getRawX();
                int scrollX = getScrollX();
                int offsetX = currentX - mLastX;
                if (scrollX == FlagWidthIntercept) {
                    // 左
                    if (offsetX <= 0) {
                        scrollTo(FlagWidthIntercept, 0);
                    } else {
                        scrollBy(-offsetX, 0);
                    }
                } else if (scrollX < FlagWidthIntercept) {
                    if (offsetX <= 0) {
                        if (scrollX + Math.abs(offsetX) <= FlagWidthIntercept) {
                            scrollBy(-offsetX, 0);
                        } else {
                            scrollTo(FlagWidthIntercept, 0);
                        }
                    } else {
                        if (scrollX - Math.abs(offsetX) <= 0) {
                            scrollTo(0, 0);
                        } else {
                            scrollBy(-offsetX, 0);
                        }
                    }
                }
                invalidate();
                mLastX = currentX;
            }
            break;
            case MotionEvent.ACTION_CANCEL:
                scrollTo(0, 0);
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}