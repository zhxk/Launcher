package com.changren.android.component.ultraviewpager;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * @author wangsy
 * @Package com.changren.android.component.ultraviewpager
 * @date 2018-09-14
 * @Description: ViewPager的Adapter封装类，接收外部自定义PagerAdapter参数，
 */
public class UltraViewPagerAdapter extends PagerAdapter {
    public interface UltraViewPagerCenterListener {
        void center();

        void resetPosition();
    }

    private static final int INFINITE_RATIO = 400;

    /** 外部自定义PagerAdapter参数 */
    private PagerAdapter adapter;
    /** 是否开启循环模式 */
    private boolean enableLoop;
    /** 在启用循环模式时，用于确定第一个item是否在parentView的center位置 */
    private boolean hasCentered;
    /** 一屏显示多个item的比例，一般小于1f，会用于计算width */
    private float multiScrRatio = Float.NaN;
    /** 设备屏幕的宽度 */
    private int scrWidth;
    /** item循环的基数 */
    private int infiniteRatio;
    /** 返回循环模式的第一个Item(即中间的item)的回调监听 */
    private UltraViewPagerCenterListener centerListener;

    /** 缓存item View */
    private SparseArray<View> viewArray = new SparseArray<>();

    UltraViewPagerAdapter(PagerAdapter adapter) {
        this.adapter = adapter;
        infiniteRatio = INFINITE_RATIO;
    }

    @Override
    public int getCount() {
        int count;
        if (enableLoop) {
            if (adapter.getCount() == 0) {
                count = 0;
            } else {
                count = adapter.getCount() * infiniteRatio;
            }
        } else {
            count = adapter.getCount();
        }
        return count;
    }


    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int realPosition = position;
        //TODO
        if (enableLoop && adapter.getCount() != 0) {
            realPosition = position % adapter.getCount();
        }

        Object item = adapter.instantiateItem(container, realPosition);
        //TODO
        View childView = null;
        if (item instanceof View)
            childView = (View) item;
        if (item instanceof Fragment) {
            childView = ((Fragment) item).getView();
        }
//        if (item instanceof RecyclerView.ViewHolder)
//            childView = ((RecyclerView.ViewHolder) item).itemView;

//        if (childView == null) {
//            throw new IllegalArgumentException("adapter instantiateItem not return view.");
//        }

        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = container.getChildAt(i);
            if (isViewFromObject(child, item)) {
                viewArray.put(realPosition, child);
                break;
            }
        }

        if (isEnableMultiScr()) {//一屏显示多个item
            if (scrWidth == 0) {
                scrWidth = container.getResources().getDisplayMetrics().widthPixels;
            }
            //初始化RelativeLayout布局
            RelativeLayout relativeLayout = new RelativeLayout(container.getContext());
            if (childView.getLayoutParams() != null) {
                //LayoutParams设置宽高，按multiScrRatio换算宽度
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        (int) (scrWidth * multiScrRatio), ViewGroup.LayoutParams.WRAP_CONTENT);
                //位置居中
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                //给childView更换LayoutParams，所以外部设置LayoutParams无效
                childView.setLayoutParams(layoutParams);
            }

            container.removeView(childView);
            relativeLayout.addView(childView);

            container.addView(relativeLayout);
            return relativeLayout;
        }

        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int realPosition = position;

        //TODO
        if (enableLoop && adapter.getCount() != 0)
            realPosition = position % adapter.getCount();

        if (isEnableMultiScr() && object instanceof RelativeLayout) {
            View child = ((RelativeLayout) object).getChildAt(0);
            ((RelativeLayout) object).removeAllViews();
            adapter.destroyItem(container, realPosition, child);
        } else {
            adapter.destroyItem(container, realPosition, object);
        }

        viewArray.remove(realPosition);
    }

    public View getViewAtPosition(int position) {
        return viewArray.get(position);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        // only need to set the center position  when the loop is enabled
        if (!hasCentered) {
            if (adapter.getCount() > 0 && getCount() > adapter.getCount()) {
                centerListener.center();
            }
        }
        hasCentered = true;
        adapter.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return adapter.isViewFromObject(view, object);
    }

    @Override
    public void restoreState(Parcelable bundle, ClassLoader classLoader) {
        adapter.restoreState(bundle, classLoader);
    }

    @Override
    public Parcelable saveState() {
        return adapter.saveState();
    }

    @Override
    public void startUpdate(ViewGroup container) {
        adapter.startUpdate(container);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int virtualPosition = position % adapter.getCount();
        return adapter.getPageTitle(virtualPosition);
    }

    @Override
    public float getPageWidth(int position) {
        return adapter.getPageWidth(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        adapter.setPrimaryItem(container, position, object);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        adapter.unregisterDataSetObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return adapter.getItemPosition(object);
    }

    public PagerAdapter getAdapter() {
        return adapter;
    }

    public int getRealCount() {
        return adapter.getCount();
    }

    public void setEnableLoop(boolean status) {
        this.enableLoop = status;
        notifyDataSetChanged();
        if (!status) {
            centerListener.resetPosition();
        } else {
            //try {
            //    centerListener.center();
            //} catch (Exception e) {
            //
            //}
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
