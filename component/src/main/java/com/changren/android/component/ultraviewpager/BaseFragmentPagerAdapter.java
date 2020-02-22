package com.changren.android.component.ultraviewpager;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: wangsy
 * Create: 2018-09-19 11:13
 * Description: TODO(描述文件做什么)
 */
public abstract class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    public interface UltraViewPagerCenterListener {
        void center();

        void resetPosition();
    }

    private static final int INFINITE_RATIO = 400;

    /** 外部自定义PagerAdapter参数 */
//    private PagerAdapter adapter;
    /** 是否开启循环模式 */
    protected boolean enableLoop;
    /** 在启用循环模式时，用于确定第一个item是否在parentView的center位置 */
    protected boolean hasCentered;
    /** 一屏显示多个item的比例，一般小于1f，会用于计算width */
    protected float multiScrRatio = Float.NaN;
    /** 设备屏幕的宽度 */
//    private int scrWidth;
    /** item循环的基数 */
    protected int infiniteRatio;
    /** 返回循环模式的第一个Item(即中间的item)的回调监听 */
    protected UltraViewPagerCenterListener centerListener;

    /** 缓存item View */
//    protected SparseArray<View> viewArray = new SparseArray<>();

    public BaseFragmentPagerAdapter(FragmentManager fm, boolean enableLoop) {
        super(fm);

        this.enableLoop = enableLoop;
        infiniteRatio = INFINITE_RATIO;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = position;
        if (enableLoop && getRealCount() != 0) {
            realPosition = position % getRealCount();
        }
        return super.instantiateItem(container, realPosition);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        // only need to set the center position  when the loop is enabled
        if (!hasCentered) {
            if (getRealCount() > 0 && getCount() > getRealCount()) {
                centerListener.center();
            }
        }
        hasCentered = true;

        super.finishUpdate(container);
    }

    /**
     * @return enableLoop==true，则返回getCount()* infiniteRatio,否则返回getCount()
     */
    protected abstract int getRealCount();

    protected abstract View getViewAtPosition(int position);

    public void setEnableLoop(boolean status) {
        this.enableLoop = status;
//        notifyDataSetChanged();

        if (centerListener == null) {
            return;
        }

        if (!status) {
            centerListener.resetPosition();
        } else {
//            centerListener.center();
        }
    }

    public boolean isEnableLoop() {
        return enableLoop;
    }

    public void setMultiScrRatio(float ratio) {
        multiScrRatio = ratio;
    }

    public boolean isEnableMultiScr() {
        return !Float.isNaN(multiScrRatio) && multiScrRatio < 1f;
    }

    public void setCenterListener(UltraViewPagerCenterListener listener) {
        centerListener = listener;
    }

    public void setInfiniteRatio(int infiniteRatio) {
        this.infiniteRatio = infiniteRatio;
    }

}
