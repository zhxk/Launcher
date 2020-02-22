package com.changren.android.launcher.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-09-20 15:49
 * Description: TODO(描述文件做什么)
 */
public class HealthFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private boolean enableLoop = false;

    public HealthFragmentAdapter(FragmentManager fm, List<Fragment> fragments, boolean enableLoop) {
        super(fm);

        this.mFragmentList = fragments;
        this.enableLoop = enableLoop;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        if (mFragmentList == null || mFragmentList.size() == 0) {
            return 0;
        } else {
            return enableLoop ? Integer.MAX_VALUE : mFragmentList.size();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e("instantiateItem", "position=="+position+"innerPosition=="+innerPosition(position));
        return super.instantiateItem(container, innerPosition(position));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.e("destroyItem", "position=="+position+"innerPosition=="+innerPosition(position));
        super.destroyItem(container, innerPosition(position), object);
    }

    private int innerPosition(int position) {
        if (enableLoop && mFragmentList.size() != 0) {
            return position % mFragmentList.size();
        }
        return position;
    }
}
