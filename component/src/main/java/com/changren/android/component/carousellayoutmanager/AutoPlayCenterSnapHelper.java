package com.changren.android.component.carousellayoutmanager;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;

/**
 * Author: wangsy
 * Create: 2019-03-14 16:30
 * Description: RecyclerView自动滑动辅助类
 */
public class AutoPlayCenterSnapHelper extends PagerSnapHelper {

    final static int LEFT = 1;
    final static int RIGHT = 2;

    private Handler handler;
    private int timeInterval;
    private Runnable autoPlayRunnable;
    private boolean runnableAdded;
    private int direction;

    private RecyclerView mRecyclerView;

    public AutoPlayCenterSnapHelper(int timeInterval, int direction) {
        checkTimeInterval(timeInterval);
        checkDirection(direction);
        handler = new Handler(Looper.getMainLooper());
        this.timeInterval = timeInterval;
        this.direction = direction;
    }

    @Override
    public void attachToRecyclerView(@Nullable final RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
        if (mRecyclerView == null) {
            return;
        }

        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (! (layoutManager instanceof CarouselLayoutManager)) {
            return;
        }

        autoPlayRunnable = new Runnable() {
            @Override
            public void run() {
                final int currentPosition = ((CarouselLayoutManager) layoutManager).getCenterItemPosition();
                if (currentPosition == layoutManager.getItemCount() - 1) {
                    mRecyclerView.scrollToPosition(0);
//                    mRecyclerView.smoothScrollToPosition(0);
                } else {
                    mRecyclerView.scrollToPosition(currentPosition + 1);
                }
                handler.postDelayed(autoPlayRunnable, timeInterval);
            }
        };
        handler.postDelayed(autoPlayRunnable, timeInterval);
        runnableAdded = true;
    }

    public void pause() {
        if (runnableAdded) {
            handler.removeCallbacks(autoPlayRunnable);
            runnableAdded = false;
        }
    }

    public void start() {
        if (!runnableAdded) {
            handler.postDelayed(autoPlayRunnable, timeInterval);
            runnableAdded = true;
        }
    }

    public void setTimeInterval(int timeInterval) {
        checkTimeInterval(timeInterval);
        this.timeInterval = timeInterval;
    }

    public void setDirection(int direction) {
        checkDirection(direction);
        this.direction = direction;
    }

    private void checkDirection(int direction) {
        if (direction != LEFT && direction != RIGHT)
            throw new IllegalArgumentException("direction should be one of left or right");
    }

    private void checkTimeInterval(int timeInterval) {
        if (timeInterval <= 0)
            throw new IllegalArgumentException("time interval should greater than 0");
    }
}
