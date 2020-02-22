package com.changren.android.launcher.user.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.LogUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: wangsy
 * Create: 2018-10-15 14:09
 * Description: 登录页面
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tab)
    TabLayout mTabLayout;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.tv_title)
    TextView mTitleView;

    private ArrayList<Fragment> fragments;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppConfig.isLogin()) {
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstants.FID, AppConfig.getLoginFamilyId());
            bundle.putBoolean(AppConstants.RESTART, true);
            ActivityUtils.startActivity(bundle, this, MemberListActivity.class);
            ActivityUtils.finishActivity(this);
            return;
        }

        setContentView(R.layout.user_activity_login);

        ButterKnife.bind(this);
        initView();
    }

    /** 不会回调 */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //TODO 横竖屏切换采用Activity重载方式，因为在ViewPager中Fragment不重新加载的情况下，还没有想到方式remove fragment
//        LogUtils.i("onConfigurationChanged");
    }

    private void initView() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        if (fragments == null) {
            fragments = new ArrayList<>(2);
            fragments.add(new LoginFragment());
            fragments.add(new RegisterFragment());
        }

        if (pagerAdapter == null) {
            pagerAdapter = new FragmentAdapter(getSupportFragmentManager(), this, fragments);
        }
        mViewPager.setAdapter(pagerAdapter);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        mTitleView.setText(R.string.user_login);
                        break;

                    default:
                        mTitleView.setText(R.string.user_register);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //关联ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @OnClick(R.id.iv_back)
    public void onClick(View v) {
        finish();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        //点击back键finish当前activity
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return true;
//    }

    static class FragmentAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragmentList;
        private Context context;

        FragmentAdapter(FragmentManager fm, Context context, ArrayList<Fragment> fragments) {
            super(fm);

            this.context = context;
            this.mFragmentList = fragments;
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
                return mFragmentList.size();
            }
        }

        /**
         * 用于TabLayout中TabItem的title文字
         * @param position  下标
         * @return
         */
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return context.getResources().getString(R.string.user_login);
            } else {
                return context.getResources().getString(R.string.user_register);
            }
        }
    }
}
