package com.changren.android.component.ultraviewpager.transformer;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * @author wangsy
 * @Package com.changren.android.component.ultraviewpager.transformer
 * @date 2018-09-14
 * @Description: ViewPager自定义切换动画实现类，比例缩放
 */
public class UltraScaleTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(View page, float position) {
        final int pageWidth = page.getWidth();
        final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));

        if (position < 0) { // [-1,0]
            // Scale the page down (between MIN_SCALE and 1)
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

        } else if (position == 0) {
            page.setScaleX(1);
            page.setScaleY(1);

        } else if (position <= 1) { // (0,1]
            // Scale the page down (between MIN_SCALE and 1)
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
        }

//        if (position < -1 || position > 1) {
//            page.setScaleX(MIN_SCALE);
//            page.setScaleY(MIN_SCALE);
//        } else if (position <= 1) { // [-1,1]
//            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
//            if (position < 0) {
//                float scaleX = 1 + 0.3f * position;
//                page.setScaleX(scaleX);
//                page.setScaleY(scaleX);
//            } else {
//                float scaleX = 1 - 0.3f * position;
//                page.setScaleX(scaleX);
//                page.setScaleY(scaleX);
//            }
//        }
    }
}
