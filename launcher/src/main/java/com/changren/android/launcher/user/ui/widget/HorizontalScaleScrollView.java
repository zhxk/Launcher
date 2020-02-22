package com.changren.android.launcher.user.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.changren.android.launcher.util.LogUtils;

/**
 * 水平滚动刻度尺
 *
 * @author LichFaker on 16/3/4.
 * @Email lichfaker@gmail.com
 */
public class HorizontalScaleScrollView extends BaseScaleView {

    public HorizontalScaleScrollView(Context context) {
        super(context);
    }

    public HorizontalScaleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HorizontalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initVar() {
        mRectWidth = (mMax - mMin) * mScaleMargin;
        mRectHeight = mScaleHeight * 6;
        mScaleMaxHeight = mScaleHeight * 2;

        // 设置layoutParams
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(mRectWidth, mRectHeight);
        this.setLayoutParams(lp);
    }

    private int countScale = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = View.MeasureSpec.makeMeasureSpec(mRectHeight, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
        mScaleScrollViewRange = getMeasuredWidth();
        mTempScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
        mMidCountScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;

        //每一屏幕刻度的个数/2,中间刻度
        countScale = mScaleScrollViewRange / mScaleMargin / 2;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        paint.setColor(Color.GRAY);
        canvas.drawLine(0, 0, mRectWidth, 0, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        paint.setColor(Color.GRAY);
        paint.setTextSize(mRectHeight / 4);

        for (int i = 0, k = mMin; i <= (mMax - mMin); i++) {
            if (i % 5 == 0) { //整值
                canvas.drawLine(i * mScaleMargin, 0, i * mScaleMargin, mScaleMaxHeight, paint);
                if (i % 10 == 0) {
                    //整值文字
                    canvas.drawText(String.valueOf(k/10), i * mScaleMargin, mScaleMaxHeight + mScaleHeight * 2, paint);
                    k += 10;
                }
            } else {
                canvas.drawLine(i * mScaleMargin, 0, i * mScaleMargin, mScaleHeight, paint);
            }
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED);

        //每一屏幕刻度的个数/2
//        int countScale = mScaleScrollViewRange / mScaleMargin / 2;
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalX = mScroller.getFinalX();
        //滑动的刻度
        int tmpCountScale = (int) Math.rint((double) finalX / (double) mScaleMargin); //四舍五入取整

        //总刻度
        mCountScale = tmpCountScale + countScale + mMin;
        if (mScrollListener != null) { //回调方法
            mScrollListener.onScaleScroll(mCountScale);
        }
        canvas.drawLine(countScale * mScaleMargin + finalX, 0,
                countScale * mScaleMargin + finalX, mScaleMaxHeight + mScaleHeight, paint);
    }

    @Override
    public void scrollToScale(int val) {
        if (val < mMin || val > mMax) {
            return;
        }
        int dx = (val - mCountScale) * mScaleMargin;
        smoothScrollBy(dx, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataX = mScrollLastX - x;
                if (mCountScale - mTempScale < 0) { //向右边滑动
                    if (mCountScale <= mMin && dataX <= 0) //禁止继续向右滑动
                        return super.onTouchEvent(event);
                } else if (mCountScale - mTempScale > 0) { //向左边滑动
                    if (mCountScale >= mMax && dataX >= 0) //禁止继续向左滑动
                        return super.onTouchEvent(event);
                }
                smoothScrollBy(dataX, 0);
                mScrollLastX = x;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
                if (mCountScale < mMin) mCountScale = mMin;
                if (mCountScale > mMax) mCountScale = mMax;
                int finalX = (mCountScale - mMidCountScale) * mScaleMargin;
                mScroller.setFinalX(finalX); //纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

}
