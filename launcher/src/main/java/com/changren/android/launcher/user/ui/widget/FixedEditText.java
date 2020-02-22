package com.changren.android.launcher.user.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Author: wangsy
 * Create: 2018-12-05 14:21
 * Description: TODO(描述文件做什么)
 */

/**
 * 左边有固定文字EditText
 */
public class FixedEditText extends AppCompatEditText {
    private String fixedText;
    private int fixedTextColor;
    private OnClickListener mListener;
    private int leftPadding;
    private Paint paint;

    public FixedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setFakeBoldText(false);
    }

    public void setFixedText(String text, int color) {
        fixedText = text;
        fixedTextColor = color;
        leftPadding = getPaddingLeft();
        int left = (int) getPaint().measureText(fixedText) + getPaddingLeft();
        setPadding(left, getPaddingTop(), getPaddingBottom(), getPaddingRight());
        invalidate();
    }

    public void setDrawableClick(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(fixedText)) {
//            paint.setTextSize(30);
//            paint.setColor(fixedTextColor);
            canvas.drawText(fixedText, 0, getBaseline(), getPaint());
//            通过下面的代码，可以查看出文字的基线，以及view的中线
//            Paint p = new Paint();
//            p.setStrokeWidth(1);
//            p.setColor(Color.parseColor("#ff0000"));
//            canvas.drawLine(0, getBaseline(), getMeasuredWidth(), getBaseline(), p);
//            canvas.drawLine(0, getMeasuredHeight() / 2, getMeasuredWidth(), getMeasuredHeight() / 2, p);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mListener != null && getCompoundDrawables()[2] != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int i = getMeasuredWidth() - getCompoundDrawables()[2].getIntrinsicWidth();
                    if (event.getX() > i) {
                        mListener.onClick(this);
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }
}
