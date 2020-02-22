package com.changren.android.component.ultraviewpager;

import android.os.Handler;
import android.os.Message;
import android.util.SparseIntArray;

/**
 * @author wangsy
 * @Package com.changren.android.component.ultraviewpager
 * @date 2018-09-14
 * @Description: ViewPager的定时执行控制类
 */
public class TimerHandler extends Handler {

    public interface TimerHandlerListener {
        int getNextItem();
        void callBack();
    }

    SparseIntArray specialInterval;
    long interval;
    boolean isStopped = true;
    TimerHandlerListener listener;

    static final int MSG_TIMER_ID = 87108;

    public TimerHandler(TimerHandlerListener listener, long interval) {
        this.listener = listener;
        this.interval = interval;
    }

    @Override
    public void handleMessage(Message msg) {
        if (MSG_TIMER_ID == msg.what) {
            if (listener != null) {
                int nextIndex = listener.getNextItem();
                listener.callBack();
                tick(nextIndex);
            }
        }
    }

    public void tick(int index) {
        sendEmptyMessageDelayed(TimerHandler.MSG_TIMER_ID, getNextInterval(index));
    }

    private long getNextInterval(int index) {
        long next = interval;
        if (specialInterval != null) {
            long has = specialInterval.get(index, -1);
            if (has > 0) {
                next = has;
            }
        }
        return next;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public void setListener(TimerHandlerListener listener) {
        this.listener = listener;
    }

    public void setSpecialInterval(SparseIntArray specialInterval) {
        this.specialInterval = specialInterval;
    }
}
