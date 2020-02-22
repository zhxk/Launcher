package com.changren.android.launcher.util;

import android.util.DisplayMetrics;

/**
 * Author: wangsy
 * Create: 2018-12-25 11:41
 * Description: 转换工具类
 */
public class ConvertUtils {

    private static DisplayMetrics dm = null;

    public static DisplayMetrics displayMetrics() {
        if (null != dm) {
            return dm;
        }

        dm = Utils.getApp().getResources().getDisplayMetrics();

        return dm;
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(final float dpValue) {
        final float scale = displayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Value of px to value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    public static int px2dp(final float pxValue) {
        final float scale = displayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Value of sp to value of px.
     *
     * @param spValue The value of sp.
     * @return value of px
     */
    public static int sp2px(final float spValue) {
        final float fontScale = displayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * Value of px to value of sp.
     *
     * @param pxValue The value of px.
     * @return value of sp
     */
    public static int px2sp(final float pxValue) {
        final float fontScale = displayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
