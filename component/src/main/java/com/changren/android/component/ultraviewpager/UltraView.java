package com.changren.android.component.ultraviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.changren.android.component.R;

/**
 * @author wangsy
 * @Package com.changren.android.component.ultraviewpager
 * @date 2018-09-14
 * @Description: UltraView继承自RelativeLayout,作为容器同时存放ViewPager和Indicator指示器，
 * 可以设置Indicator的显示/隐藏，并设置样式；
 * 可以通过getViewPager()或者自带方法，设置ViewPager属性
 */
public class UltraView extends RelativeLayout implements IUltraViewFeature {

    /** 滑动模式类：横向/纵向 */
    public enum ScrollMode {
        HORIZONTAL(0), VERTICAL(1);
        int id;

        ScrollMode(int id) {
            this.id = id;
        }

        static ScrollMode getScrollMode(int id) {
            for (ScrollMode scrollMode : values()) {
                if (scrollMode.id == id)
                    return scrollMode;
            }
            throw new IllegalArgumentException();
        }
    }

    /** 方向类：横向/纵向 */
    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    /** 滑动方向类：反向/正向/静止 */
    public enum ScrollDirection {
        NONE(0), BACKWARD(1), FORWARD(2);
        int id;

        ScrollDirection(int id) {
            this.id = id;
        }

        static ScrollDirection getScrollDirection(int id) {
            for (ScrollDirection direction : values()) {
                if (direction.id == id)
                    return direction;
            }
            throw new IllegalArgumentException();
        }
    }

    private final Point size;
    private final Point maxSize;

    private float ratio = Float.NaN;

    //Maximum width of child when enable multiScreen.
    private int maxWidth = -1;

    //Maximum height of child when enable multiScreen.
    private int maxHeight = -1;

    private UltraViewPager viewPager;
    private UltraViewPagerIndicator pagerIndicator;

    private TimerHandler timer;

    public UltraView(Context context) {
        super(context);
        size = new Point();
        maxSize = new Point();
        initView();
    }

    public UltraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        size = new Point();
        maxSize = new Point();
        initView();
        initView(context, attrs);
    }

    public UltraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        size = new Point();
        maxSize = new Point();
        initView();
    }

    private void initView() {
        //初始化ViewPager
        viewPager = new UltraViewPager(getContext());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            viewPager.setId(viewPager.hashCode());
        } else {
            viewPager.setId(View.generateViewId());
        }

        // 添加ViewPager到容器，LayoutParams参数是在容器的位置
        addView(viewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /** 获取layout布局中的属性值 */
    private void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UltraView);
        setAutoScroll(ta.getInt(R.styleable.UltraView_upv_autoscroll, 0));
        setInfiniteLoop(ta.getBoolean(R.styleable.UltraView_upv_infiniteloop, false));
        setRatio(ta.getFloat(R.styleable.UltraView_upv_ratio, Float.NaN));
        setScrollMode(ScrollMode.getScrollMode(ta.getInt(R.styleable.UltraView_upv_scrollmode, 0)));
        disableScrollDirection(ScrollDirection.getScrollDirection(ta.getInt(R.styleable.UltraView_upv_disablescroll, 0)));
        setMultiScreen(ta.getFloat(R.styleable.UltraView_upv_multiscreen, 1f));
        setAutoMeasureHeight(ta.getBoolean(R.styleable.UltraView_upv_automeasure, false));
        setItemRatio(ta.getFloat(R.styleable.UltraView_upv_itemratio, Float.NaN));
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!Float.isNaN(ratio)) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize / ratio), MeasureSpec.EXACTLY);
        }
        size.set(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        if (maxWidth >= 0 || maxHeight >= 0) {
            maxSize.set(maxWidth, maxHeight);
            constrainTo(size, maxSize);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(size.x, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(size.y, MeasureSpec.EXACTLY);
        }
        if (viewPager.getConstrainLength() > 0) {
            if (viewPager.getConstrainLength() == heightMeasureSpec) {
                viewPager.measure(widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(size.x, size.y);
            } else {
                if (viewPager.getScrollMode() == ScrollMode.HORIZONTAL) {
                    super.onMeasure(widthMeasureSpec, viewPager.getConstrainLength());
                } else {
                    super.onMeasure(viewPager.getConstrainLength(), heightMeasureSpec);
                }
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTimer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTimer();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        stopTimer();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        startTimer();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (timer != null) {
            final int action = ev.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                stopTimer();
            }
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                startTimer();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public IUltraIndicatorBuilder initIndicator() {
        disableIndicator();
        pagerIndicator = new UltraViewPagerIndicator(getContext());
        pagerIndicator.setViewPager(viewPager);
        pagerIndicator.setIndicatorBuildListener(new UltraViewPagerIndicator.UltraViewPagerIndicatorListener() {
            @Override
            public void build() {
                removeView(pagerIndicator);
                //添加Indicator到容器，和ViewPager重叠，可以通过Gravity设置内容显示位置
                addView(pagerIndicator, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
        return pagerIndicator;
    }

    @Override
    public IUltraIndicatorBuilder initIndicator(int focusColor, int normalColor, int radiusInPixel, int gravity) {
        return initIndicator()
                .setFocusColor(focusColor)
                .setNormalColor(normalColor)
                .setRadius(radiusInPixel)
                .setGravity(gravity);
    }

    @Override
    public IUltraIndicatorBuilder initIndicator(int focusColor, int normalColor, int strokeColor, int strokeWidth, int radiusInPixel, int gravity) {
        return initIndicator()
                .setFocusColor(focusColor)
                .setNormalColor(normalColor)
                .setStrokeWidth(strokeWidth)
                .setStrokeColor(strokeColor)
                .setRadius(radiusInPixel)
                .setGravity(gravity);
    }

    @Override
    public IUltraIndicatorBuilder initIndicator(int focusResId, int normalResId, int gravity) {
        return initIndicator()
                .setFocusResId(focusResId)
                .setNormalResId(normalResId)
                .setGravity(gravity);
    }

    @Override
    public IUltraIndicatorBuilder initIndicator(Bitmap focusBitmap, Bitmap normalBitmap, int gravity) {
        return initIndicator()
                .setFocusIcon(focusBitmap)
                .setNormalIcon(normalBitmap)
                .setGravity(gravity);
    }

    @Override
    public void disableIndicator() {
        if (pagerIndicator != null) {
            removeView(pagerIndicator);
            pagerIndicator = null;
        }
    }

    public IUltraIndicatorBuilder getIndicator() {
        return pagerIndicator;
    }

    private TimerHandler.TimerHandlerListener mTimerHandlerListener = new TimerHandler.TimerHandlerListener() {
        @Override
        public int getNextItem() {
            return UltraView.this.getNextItem();
        }

        @Override
        public void callBack() {
            scrollNextPage();
        }
    };

    @Override
    public void setAutoScroll(int intervalInMillis) {
        if (0 == intervalInMillis) {
            return;
        }
        if (timer != null) {
            disableAutoScroll();
        }
        timer = new TimerHandler(mTimerHandlerListener, intervalInMillis);
        startTimer();
    }

    @Override
    public void setAutoScroll(int intervalInMillis, SparseIntArray intervalArray) {
        if (0 == intervalInMillis) {
            return;
        }
        if (timer != null) {
            disableAutoScroll();
        }
        timer = new TimerHandler(mTimerHandlerListener, intervalInMillis);
        timer.specialInterval = intervalArray;
        startTimer();
    }

    @Override
    public void disableAutoScroll() {
        stopTimer();
        timer = null;
    }

    @Override
    public void setScrollMode(ScrollMode scrollMode) {
        viewPager.setScrollMode(scrollMode);
    }

    @Override
    public void setInfiniteLoop(boolean enableLoop) {
        viewPager.setEnableLoop(enableLoop);
    }

    @Override
    public void setMaxWidth(int width) {
        maxWidth = width;
    }

    /**
     * 按给定的宽高比例ratio，以屏幕的高为基数，设置Item的高度
     * w/h = ratio
     * @param ratio
     */
    @Override
    public void setRatio(float ratio) {
        this.ratio = ratio;
        viewPager.setRatio(ratio);
    }

    /**
     * 设置Item的间隔
     * TODO 同时设置@see{setMultiScreen}一屏显示多个item，并想控制Item间距，效果有点奇怪
     * TODO pixel需要设置负数，为@see{setMultiScreen}换算后的两倍间距，否则出现无法显示多个Item，还没有找到原因？
     * @param pixel Item的间隔
     */
    @Override
    public void setHGap(int pixel) {
        //TODO 不需要设置setMultiScreen的比例
//        int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
//        viewPager.setMultiScreen((screenWidth - pixel) / (float) screenWidth);
        viewPager.setPageMargin(-pixel);
    }

    @Override
    public void setMaxHeight(int height) {
        maxHeight = height;
    }

    @Override
    public void disableScrollDirection(UltraView.ScrollDirection direction) {

    }

    @Override
    public boolean scrollLastPage() {
        boolean isChange = false;
        if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 0) {
            final int curr = viewPager.getCurrentItemFake();
            int lastPage = 0;
            if (curr > 0) {
                lastPage = curr - 1;
                isChange = true;
            }
            viewPager.setCurrentItemFake(lastPage, true);
        }
        return isChange;
    }

    @Override
    public boolean scrollNextPage() {
        boolean isChange = false;
        if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 0) {
            final int curr = viewPager.getCurrentItemFake();
            int nextPage = 0;
            if (curr < viewPager.getAdapter().getCount() - 1) {
                nextPage = curr + 1;
                isChange = true;
            }
            viewPager.setCurrentItemFake(nextPage, true);
        }
        return isChange;
    }

    @Override
    public void setMultiScreen(float ratio) {
        if (ratio <= 0 || ratio > 1) {
            throw new IllegalArgumentException("");
        }
        if (ratio <= 1f) {
            viewPager.setMultiScreen(ratio);
        }
    }

    @Override
    public void setAutoMeasureHeight(boolean status) {
        viewPager.setAutoMeasureHeight(status);
    }

    @Override
    public void setItemRatio(double ratio) {
        viewPager.setItemRatio(ratio);
    }

    @Override
    public void setItemMargin(int left, int top, int right, int bottom) {
        viewPager.setItemMargin(left, top, right, bottom);
    }

    @Override
    public void setScrollMargin(int left, int right) {
        viewPager.setPadding(left, 0, right, 0);
    }

    /**
     * delegate viewpager
     */

    public void setAdapter(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
    }

    public void setOffscreenPageLimit(int limit) {
        viewPager.setOffscreenPageLimit(limit);
    }

    public PagerAdapter getAdapter() {
        return viewPager.getAdapter() == null ? null : ((UltraViewPagerAdapter) viewPager.getAdapter()).getAdapter();
    }

    public PagerAdapter getWrapAdapter() {
        return viewPager.getAdapter();
    }

    public void refresh() {
        if (viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (pagerIndicator == null) {
            //avoid registering the same listener twice
            viewPager.removeOnPageChangeListener(listener);
            viewPager.addOnPageChangeListener(listener);
        } else {
            pagerIndicator.setPageChangeListener(listener);
        }
    }

    public void setCurrentItem(int item) {
        viewPager.setCurrentItem(item);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }

    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    public int getNextItem() {
        return viewPager.getNextItem();
    }

    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        viewPager.setPageTransformer(reverseDrawingOrder, transformer);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    private void constrainTo(Point size, Point maxSize) {
        if (maxSize.x >= 0) {
            if (size.x > maxSize.x) {
                size.x = maxSize.x;
            }
        }
        if (maxSize.y >= 0) {
            if (size.y > maxSize.y) {
                size.y = maxSize.y;
            }
        }
    }

    private void startTimer() {
        if (timer == null || viewPager == null || !timer.isStopped) {
            return;
        }
        timer.listener = mTimerHandlerListener;
        timer.removeCallbacksAndMessages(null);
        timer.tick(0);
        timer.isStopped = false;
    }

    private void stopTimer() {
        if (timer == null || viewPager == null || timer.isStopped) {
            return;
        }
        timer.removeCallbacksAndMessages(null);
        timer.listener = null;
        timer.isStopped = true;
    }

    @Override
    public void setInfiniteRatio(int infiniteRatio) {
        if (viewPager.getAdapter() != null
                && viewPager.getAdapter() instanceof UltraViewPagerAdapter) {
            ((UltraViewPagerAdapter) viewPager.getAdapter()).setInfiniteRatio(infiniteRatio);
        }
    }

}
