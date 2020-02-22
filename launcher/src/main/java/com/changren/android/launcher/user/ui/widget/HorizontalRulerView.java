package com.changren.android.launcher.user.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.changren.android.launcher.R;

/**
 * Author: wangsy
 * Create: 2018-12-01 16:37
 * Description: 横向刻度尺
 */
public class HorizontalRulerView extends View {

    private Paint mPaint;

    private int mWidth;
    private int mHeight;
    private int mStartWidth;
    private Rect bigBound;

    public HorizontalRulerView(Context context) {
        super(context);
    }

    public HorizontalRulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        // 获取自定义属性和默认值
//        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
//        // 刻度宽度
//        scaleWidth = mTypedArray.getDimensionPixelSize(R.styleable.RulerWheel_scaleWidth, DEFAULT_LINE_SIZE);
//        linePaint.setStrokeWidth(scaleWidth);
//        // 刻度颜色
//        mLineColorMax = mTypedArray.getColor(R.styleable.RulerWheel_lineColorMax, Color.BLACK);
//        mLineColorMid = mTypedArray.getColor(R.styleable.RulerWheel_lineColorMid, Color.BLACK);
//        mLineColorMin = mTypedArray.getColor(R.styleable.RulerWheel_lineColorMin, Color.BLACK);
//        mTypedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.textSizeSM));
        bigBound = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width;
        int height;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {//精准模式，match_parent或者固定dp值
            width = widthSize;
        } else {
            width = widthSize / 2;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = heightSize / 2;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = getWidth();
        mHeight = getHeight();
        mStartWidth = 0;
    }

    /**
     * 刻度：25~200kg
     * 刻度线宽度：2dp
     * 短刻度长度：15dp
     * 长刻度长度：30dp
     * 屏幕最多显示多少刻度：50，即刻度线间隔：16dp
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画背景
        canvas.drawLine(0, 0, mWidth, 0, mPaint);
        canvas.drawLine(0, mHeight, mWidth, mHeight, mPaint);

        //画数字
        for (int i = 0; i < 1000; i++) {
            if (i % 5 == 0) {
                canvas.drawLine(mStartWidth, 0, mStartWidth, getHeight() / 3, mPaint);
                mPaint.getTextBounds(String.valueOf(i), 0, String.valueOf(i).length(), bigBound);
                canvas.drawText(String.valueOf(i), mStartWidth - bigBound.width() / 2, getHeight() / 2 + bigBound.height() * 3 / 4, mPaint);
            } else {
                canvas.drawLine(mStartWidth, 0, mStartWidth, getHeight() / 5, mPaint);
//                mPaint.getTextBounds(String.valueOf(i), 0, String.valueOf(i).length(), smallBound);
//                canvas.drawText(String.valueOf(i), mStartWidth - smallBound.width() / 2, getHeight() / 2 + smallBound.height() * 3 / 4, mPaint);
            }
            mStartWidth += mWidth / 10;
        }
        //画中间刻度线
        mPaint.setColor(Color.BLUE);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, getHeight() / 3, mPaint);
    }
}
