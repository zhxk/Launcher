package com.changren.android.launcher.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.changren.android.component.ultraviewpager.BaseFragmentPagerAdapter;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-09-15 17:45
 * Description: UltraView.getViewPager的PagerAdapter
 */
public class UltraFragmentAdapter extends BaseFragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private FragmentManager fragmentManager;

    public UltraFragmentAdapter(FragmentManager fm, List<Fragment> fragments, boolean enableLoop) {
        super(fm, enableLoop);

        this.fragmentManager = fm;
        this.mFragmentList = fragments;
    }

    public UltraFragmentAdapter(FragmentManager fm, boolean enableLoop) {
        super(fm, enableLoop);
    }

    @Override
    protected int getRealCount() {
        return mFragmentList.size();
    }

    @Override
    protected View getViewAtPosition(int position) {
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        int realPosition = position;
        if (enableLoop && getRealCount() != 0) {
            realPosition = position % getRealCount();
        }
        return mFragmentList.get(realPosition);
    }

    @Override
    public int getCount() {
        if (mFragmentList == null || mFragmentList.size() == 0) {
            return 0;
        } else {
            return enableLoop ? Integer.MAX_VALUE : mFragmentList.size();
        }
    }

}
