package com.changren.android.component.util;

import android.content.Context;

/**
 * Author: wangsy
 * Create: 2018-09-20 19:40
 * Description: TODO(描述文件做什么)
 */
public class ScreenUtils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
