package com.changren.android.launcher.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class DrawableTextView extends TextView {
    public DrawableTextView(Context context) {
        super(context);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        // 得到drawableLeft设置的drawable对象
        Drawable leftDrawable = drawables[0];
        if (leftDrawable != null) {
            // 得到leftDrawable的宽度
            int leftDrawableWidth = leftDrawable.getIntrinsicWidth();
            // 得到drawable与text之间的间距
            int drawablePadding = getCompoundDrawablePadding();
            // 得到文本的宽度
            int textWidth = (int) getPaint().measureText(getText().toString().trim());
            int bodyWidth = leftDrawableWidth + drawablePadding + textWidth;
            canvas.save();
            canvas.translate((getWidth() - bodyWidth) / 2, 0);
        }
        super.onDraw(canvas);
    }
}
