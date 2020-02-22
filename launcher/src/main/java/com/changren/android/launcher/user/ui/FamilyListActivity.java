package com.changren.android.launcher.user.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.changren.android.component.adapter.recyclerview.MultiItemTypeAdapter;
import com.changren.android.component.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.Injection;
import com.changren.android.launcher.database.UserDataSource;
import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static butterknife.ButterKnife.bind;

/**
 * Author: wangsy
 * Create: 2018-12-18 20:23
 * Description: 选择家庭并登录
 */
public class FamilyListActivity extends AppCompatActivity {

    @BindView(R.id.rv_family)
    RecyclerView mFamilyRv;

    private FamilyListAdapter mFamilyAdapter;
//    private HeaderAndFooterWrapper footerWrapper;
    private List<Family> mFamilyList;
    private String[] ids;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_family_list);

        bind(this);

        init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //没有横向布局则不需要重新设置setContentView()
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Injection.getUserDataRepository(getApplication())
            .getFamilyList(ids, new UserDataSource.DataCallBack<List<Family>>() {
                @Override
                public void onSucceed(Disposable d, List<Family> familyList) {
                    mFamilyList.addAll(familyList);
                    mFamilyAdapter.notifyDataSetChanged();
//                    mFamilyRv.setAdapter(footerWrapper);

                    if (!d.isDisposed()) {
                        d.dispose();
                    }
                }

                @Override
                public void onFailed(Disposable d, String msg) {
                    d.dispose();
                }
            });
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String fid_list = getIntent().getExtras().getString(AppConstants.FID_LIST, "");
            if (!TextUtils.isEmpty(fid_list)) {
                ids = fid_list.split(",");
            }
        }

        initView();
    }

    private void initView() {
        if (mFamilyList == null) {
            mFamilyList = new ArrayList<>();
        }

        //横竖屏适配
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mFamilyRv.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        } else {
            mFamilyRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }

        mFamilyAdapter = new FamilyListAdapter(this, R.layout.user_item_family, mFamilyList);
        mFamilyRv.setAdapter(mFamilyAdapter);

//        initFootView();
//        mFamilyRv.setAdapter(footerWrapper);

        mFamilyAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                int old_position = mFamilyAdapter.getSelection();
                mFamilyAdapter.setSelectPosition(position);
                if (position != old_position) {
//                    footerWrapper.notifyItemChanged(position);
                    mFamilyAdapter.notifyItemChanged(position);
                    if (old_position != -1) {
//                        footerWrapper.notifyItemChanged(old_position);
                        mFamilyAdapter.notifyItemChanged(old_position);
                    }
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

//    private LayoutInflater inflater;
//
//    private void initFootView() {
//        footerWrapper = new HeaderAndFooterWrapper(mFamilyAdapter);
//
//        if (inflater == null) {
//            inflater = LayoutInflater.from(mFamilyRv.getContext());
//        }
//
//        View footer = inflater.inflate(R.layout.include_user_footer_btn, mFamilyRv, false);
//        footer.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int selection = mFamilyAdapter.getSelection();
//                if (selection != -1 && mFamilyList.size() > 0) {
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(AppConstants.FID, mFamilyList.get(selection).getFid());
//                    ActivityUtils.startActivity(bundle, FamilyListActivity.this, MemberListActivity.class);
//                    ActivityUtils.finishActivity(FamilyListActivity.this);
//                } else {
//                    ToastUtils.showLong(R.string.user_select_family_warn);
//                }
//
//                //TODO 在家庭详细界面，请求家庭成员列表，为了统一其他界面的访问逻辑
////                Injection.getUserDataRepository(getApplication())
////                    .getMemberList(mFamilyList.get(mFamilyAdapter.getSelection()).getFid()+"",
////                            new UserDataSource.DataCallBack<Family>() {
////                        @Override
////                        public void onSucceed(Disposable d, Family family) {
////                            LogUtils.i("onSucceed", family);
////
////                            Bundle bundle = new Bundle();
////                            bundle.putInt("fid", family.getFid());
////                            LogUtils.i("fid", family.getFid());
////                            if (!d.isDisposed()) {
////                                LogUtils.i("isDisposed", true);
////                                d.dispose();
////                            }
////
////                            ActivityUtils.startActivity(bundle, FamilyListActivity.this, MemberListActivity.class);
////                            ActivityUtils.finishActivity(FamilyListActivity.this);
////                        }
////
////                        @Override
////                        public void onFailed(Disposable d, String msg) {
////                            LogUtils.i("onFailed", msg);
////                            d.dispose();
////                        }
////                    });
//            }
//        });
//        footerWrapper.addFootView(footer);
//    }

    @OnClick({R.id.iv_back, R.id.btn_next})
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            ActivityUtils.finishActivity(this);
        } else {
            int selection = mFamilyAdapter.getSelection();
            if (selection != -1 && mFamilyList.size() > 0) {
                Bundle bundle = new Bundle();
                bundle.putInt(AppConstants.FID, mFamilyList.get(selection).getFid());
                ActivityUtils.startActivity(bundle, FamilyListActivity.this, MemberListActivity.class);
                ActivityUtils.finishActivity(FamilyListActivity.this);
            } else {
                ToastUtils.showLong(R.string.user_select_family_warn);
            }
        }
    }
}
