package com.changren.android.component.ultraviewpager.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author wangsy
 * @Package com.changren.android.component.ultraviewpager.transformer
 * @date 2018-09-14
 * @Description: ViewPager自定义切换动画实现类，垂直方向平移
 */
public class UltraVerticalTransformer implements ViewPager.PageTransformer {
    private float yPosition;

    @Override
    public void transformPage(View view, float position) {
        view.setTranslationX(view.getWidth() * -position);
        yPosition = position * view.getHeight();
        view.setTranslationY(yPosition);
    }

    public float getPosition() {
        return yPosition;
    }
}
