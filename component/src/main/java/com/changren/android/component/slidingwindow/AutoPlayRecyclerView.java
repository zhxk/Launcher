package com.changren.android.component.slidingwindow;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.changren.android.component.R;
import com.changren.android.component.carousellayoutmanager.AutoPlayCenterSnapHelper;

/**
 * An implement of {@link RecyclerView} which support auto play.
 */

public class AutoPlayRecyclerView extends RecyclerView {

    private AutoPlaySnapHelper autoPlaySnapHelper;
//    private AutoPlayCenterSnapHelper autoPlayCenterSnapHelper;
    private boolean isLandscape = false;

    public AutoPlayRecyclerView(Context context) {
        this(context, null);
    }

    public AutoPlayRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoPlayRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoPlayRecyclerView);
        final int timeInterval = typedArray.getInt(R.styleable.AutoPlayRecyclerView_timeInterval, AutoPlaySnapHelper.TIME_INTERVAL);
        final int direction = typedArray.getInt(R.styleable.AutoPlayRecyclerView_direction, AutoPlaySnapHelper.RIGHT);
        typedArray.recycle();

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            //Todo 使用SnapHelper会导致滑动卡顿，所以暂时不使用
//            autoPlayCenterSnapHelper = new AutoPlayCenterSnapHelper(timeInterval, direction);
        } else {
            autoPlaySnapHelper = new AutoPlaySnapHelper(timeInterval, direction);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isLandscape) {
//                    if (autoPlayCenterSnapHelper != null) {
//                        autoPlayCenterSnapHelper.pause();
//                    }
                } else {
                    if (autoPlaySnapHelper != null) {
                        autoPlaySnapHelper.pause();
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isLandscape) {
//                    if (autoPlayCenterSnapHelper != null) {
//                        autoPlayCenterSnapHelper.start();
//                    }
                } else {
                    if (autoPlaySnapHelper != null) {
                        autoPlaySnapHelper.start();
                    }
                }
        }
        return result;
    }

    public void start() {
        autoPlaySnapHelper.start();
    }

    public void pause() {
        autoPlaySnapHelper.pause();
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (isLandscape) {
//            autoPlayCenterSnapHelper.attachToRecyclerView(this);
        } else {
            autoPlaySnapHelper.attachToRecyclerView(this);
        }

    }
}
