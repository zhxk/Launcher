package com.changren.android.component.ultraviewpager;

import android.graphics.Bitmap;
import android.util.SparseIntArray;

/**
 * @author wangsy
 * @Package com.changren.android.component.ultraviewpager
 * @date 2018-09-14
 * @Description: UltraView对外拓展的接口类
 */
interface IUltraViewFeature {
    /**
     * Constructs a indicator with no options. this indicator support set-Method in chained mode.
     * meanwhile focusColor and normalColor are necessary,or the indicator won't be show.
     */
    IUltraIndicatorBuilder initIndicator();

    /**
     * Set options for indicator
     *
     * @param focusColor    defines the color when indicator is focused.
     * @param normalColor   defines the color when indicator is in the default state (not focused).
     * @param radiusInPixel defines the radius of indicator item.
     * @param gravity       specifies how to align the indicator. for example, using Gravity.BOTTOM | Gravity.RIGHT
     */
    IUltraIndicatorBuilder initIndicator(int focusColor, int normalColor, int radiusInPixel, int gravity);

    /**
     * Set options for indicator
     *
     * @param focusColor    defines the color when indicator is focused.
     * @param normalColor   defines the color when indicator is in the default state (not focused).
     * @param strokeColor   stroke color
     * @param strokeWidth   stroke width
     * @param radiusInPixel the radius of indicator item.
     * @param gravity       specifies how to align the indicator. for example, using Gravity.BOTTOM | Gravity.RIGHT
     */
    IUltraIndicatorBuilder initIndicator(int focusColor, int normalColor, int strokeColor, int strokeWidth, int radiusInPixel, int gravity);

    /**
     * Set options for indicator
     *
     * @param focusResId  defines the resource id when indicator is focused.
     * @param normalResId defines the resource id  when indicator is in the default state (not focused).
     * @param gravity     specifies how to align the indicator. for example, using Gravity.BOTTOM | Gravity.RIGHT
     */
    IUltraIndicatorBuilder initIndicator(int focusResId, int normalResId, int gravity);

    /**
     * @param focusBitmap  defines the bitmap when indicator is focused
     * @param normalBitmap defines the bitmap when indicator is in the default state (not focused).
     * @param gravity      specifies how to align the indicator. for example, using Gravity.BOTTOM | Gravity.RIGHT
     * @return
     */
    IUltraIndicatorBuilder initIndicator(Bitmap focusBitmap, Bitmap normalBitmap, int gravity);

    /**
     * Remove indicator
     */
    void disableIndicator();

    /**
     * Enable auto-scroll mode
     *
     * @param intervalInMillis The interval time to scroll in milliseconds.
     */
    void setAutoScroll(int intervalInMillis);

    /**
     * Enable auto-scroll mode with special interval times
     * @param intervalInMillis The default time to scroll
     * @param intervalArray The special interval to scroll, in responding to each frame
     */
    void setAutoScroll(int intervalInMillis, SparseIntArray intervalArray);

    /**
     * Disable auto-scroll mode
     */
    void disableAutoScroll();

    /**
     * Set an infinite loop
     *
     * @param enable enable or disable
     */
    void setInfiniteLoop(boolean enable);

    /**
     * Supply a maximum width for this ViewPager.
     *
     * @param width width
     */
    void setMaxWidth(int width);

    /**
     * Supply a maximum height for this ViewPager.
     *
     * @param height height
     */
    void setMaxHeight(int height);

    /**
     * Set the aspect ratio for UltraViewPager.
     *
     * @param ratio
     */
    void setRatio(float ratio);

    /**
     * Set scroll mode for UltraViewPager.
     *
     * @param scrollMode UltraViewPager.ScrollMode.HORIZONTAL or UltraViewPager.ScrollMode.VERTICAL
     */
    void setScrollMode(UltraView.ScrollMode scrollMode);

    /**
     * Disable scroll direction. the default value is ScrollDirection.NONE
     *
     * @param direction NONE, BACKWARD, FORWARD
     */
    void disableScrollDirection(UltraView.ScrollDirection direction);

    /**
     * Scroll to the last page, and return to the first page when the last page is reached.
     */
    boolean scrollLastPage();

    /**
     * Scroll to the next page, and return to the first page when the last page is reached.
     */
    boolean scrollNextPage();

    /**
     * Set multi-screen mode , the aspect ratio of PageViewer should less than or equal to 1.0f
     */
    void setMultiScreen(float ratio);

    /**
     * Adjust the height of the ViewPager to the height of child automatically.
     */
    void setAutoMeasureHeight(boolean status);

    /**
     * Adjust the height of child item view with aspect ratio.
     *
     * @param ratio aspect ratio
     */
    void setItemRatio(double ratio);

    /**
     * Set the gap between two pages in pixel
     *
     * @param pixel
     */
    void setHGap(int pixel);

    /**
     * Set item margin
     *
     * @param left   the left margin in pixels
     * @param top    the top margin in pixels
     * @param right  the right margin in pixels
     * @param bottom the bottom margin in pixels
     */
    void setItemMargin(int left, int top, int right, int bottom);

    /**
     * Set margins for this ViewPager
     *
     * @param left  the left margin in pixels
     * @param right the right margin in pixels
     */
    void setScrollMargin(int left, int right);

    /**
     * The items.size() would be scale to item.size()*infiniteRatio in fact
     *
     * @param infiniteRatio
     */
    void setInfiniteRatio(int infiniteRatio);
}
