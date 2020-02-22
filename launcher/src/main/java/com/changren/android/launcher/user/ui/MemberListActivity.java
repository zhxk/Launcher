package com.changren.android.launcher.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.changren.android.component.slidingwindow.CarouselLayoutManager;
import com.changren.android.component.slidingwindow.CenterSnapHelper;
import com.changren.android.component.slidingwindow.ViewPagerLayoutManager;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.Injection;
import com.changren.android.launcher.database.UserDataSource;
import com.changren.android.launcher.database.entity.Empty;
import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.database.network.HttpConfig;
import com.changren.android.launcher.ui.Launcher;
import com.changren.android.launcher.user.ui.dialog.PromptButton;
import com.changren.android.launcher.user.ui.dialog.PromptDialog;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.ConvertUtils;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.SPUtils;
import com.changren.android.launcher.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Author: wangsy
 * Create: 2018-12-18 20:21
 * Description: 家庭详细资料
 */
public class MemberListActivity extends AppCompatActivity {

    private int mFamilyId;
    //是否未退出登录，后重新进入
    private boolean isRestart = false;

    @BindView(R.id.rv_avatar)
    RecyclerView avatarRv;

    @BindView(R.id.tv_family_name)
    TextView familyNameTv;

    @BindView(R.id.tv_member_name)
    TextView memberNameTv;

    @BindView(R.id.tv_phone_bind)
    TextView phoneBindTv;

//    @BindView(R.id.cl_basic)
//    ConstraintLayout basicLayout;

    @BindView(R.id.cl_phone)
    ConstraintLayout phoneLayout;

//    @BindView(R.id.cl_permission)
//    ConstraintLayout permissionLayout;

//    @BindView(R.id.cl_ills)
//    ConstraintLayout illsLayout;

//    @BindView(R.id.cl_about)
//    ConstraintLayout aboutLayout;

//    @BindView(R.id.cl_switch_family)
//    ConstraintLayout switchLayout;

//    @BindView(R.id.cl_exit_family)
//    ConstraintLayout exitLayout;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    private User mShowUser;
    private int mShowPosition = 0;
    private List<User> userList;
    private MemberAvatarListAdapter avatarAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_member_list);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mFamilyId = bundle.getInt(AppConstants.FID);
            isRestart = bundle.getBoolean(AppConstants.RESTART, false);
        }

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        CarouselLayoutManager layoutManager = new CarouselLayoutManager(this, ConvertUtils.dp2px(50));
        layoutManager.setMaxVisibleItemCount(3);
        layoutManager.setOnPageChangeListener(new ViewPagerLayoutManager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mShowPosition == position) return;

                promptDialog.showLoading(getString(R.string.user_loading));
                mShowUser = userList.get(position);
                mShowPosition = position;
                if (!TextUtils.isEmpty(mShowUser.getPhone())) {
                    phoneBindTv.setText(mShowUser.getPhone());
                } else {
                    phoneBindTv.setText(getString(R.string.user_info_phone_unbind));
                }

                //访问接口，切换Token对应的uid
                Injection.getUserDataRepository(getApplication())
                    .switchMember(mShowUser.getFid(), mShowUser.getUid(), new UserDataSource.DataCallBack<User>() {
                        @Override
                        public void onSucceed(Disposable d, User data) {
                            memberNameTv.setText(!TextUtils.isEmpty(mShowUser.getNickname()) ? mShowUser.getNickname() : mShowUser.getUsername());
                            promptDialog.dismissImmediately();
                            if (!d.isDisposed()) {
                                d.dispose();
                            }
                        }

                        @Override
                        public void onFailed(Disposable d, String msg) {
                            promptDialog.dismissImmediately();
                            d.dispose();
                        }
                    });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                LogUtils.i("onPageScrollStateChanged state==" + state);
            }
        });
        avatarRv.setLayoutManager(layoutManager);
        //TODO 一次滑动一个item,不生效
        CenterSnapHelper pagerSnapHelper = new CenterSnapHelper();
//        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(avatarRv);

        if (userList == null) {
            userList = new ArrayList<>();
        }
        avatarAdapter = new MemberAvatarListAdapter(this, R.layout.user_item_member_avatar, userList);
        avatarRv.setAdapter(avatarAdapter);

        //先弹窗提示"是否退出登录"
        if (promptDialog == null) {
            //创建对象
            promptDialog = new PromptDialog(this);
            //设置自定义属性
            promptDialog.getDefaultBuilder().touchAble(true).round(10).loadingDuration(3000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        load();
    }

    private PromptDialog promptDialog;

    private void load() {
        promptDialog.showLoading(getString(R.string.user_loading));
        Injection.getUserDataRepository(getApplication())
            .switchMemberAndGetMemberList(mFamilyId, isRestart, new UserDataSource.DataCallBack<Family>() {
                @Override
                public void onSucceed(Disposable d, Family family) {
                    if (!d.isDisposed()) {
                        d.dispose();
                    }

                    familyNameTv.setText(family.getFamily_name());

                    int show_user_id = AppConfig.getShowUserId() != 0
                            ? AppConfig.getShowUserId()
                            : family.getUid();
                    int i = 0;
                    for (User user : family.getMember_list()) {
                        if (user.getUid() == show_user_id) {
                            memberNameTv.setText(user.getNickname());
                            mShowUser = user;
                            mShowPosition = i;
                            break;
                        }
                        i++;
                    }

                    if (!TextUtils.isEmpty(mShowUser.getPhone())) {
                        phoneBindTv.setText(mShowUser.getPhone());
                    } else {
                        phoneBindTv.setText(getString(R.string.user_info_phone_unbind));
                    }

                    userList.clear();
                    userList.addAll(family.getMember_list());
                    avatarAdapter.notifyDataSetChanged();
                    avatarRv.scrollToPosition(mShowPosition);

                    if (swipeLayout.isRefreshing()) {
                        swipeLayout.setRefreshing(false);
                    }
                    promptDialog.dismissImmediately();
                }

                @Override
                public void onFailed(Disposable d, String msg) {
                    if(msg.equals(String.valueOf(HttpConfig.TOKEN_INVALID))){
                        promptDialog.dismiss();
                        LogUtils.i("OkHttp====跳转去登录页面", msg);
                        ToastUtils.showLong( "登录失效，请重新登录");
                        AppConfig.logout();
                        Intent intent = new Intent(MemberListActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        ToastUtils.showLong(msg + "，请重新登录");
                    }
                    d.dispose();

                    if (swipeLayout.isRefreshing()) {
                        swipeLayout.setRefreshing(false);
                    }

                    promptDialog.dismissImmediately();
                }
            });
    }

    @OnClick({R.id.iv_back, R.id.iv_add, R.id.cl_exit_family, R.id.cl_basic, R.id.cl_phone, R.id.cl_ills,
            R.id.cl_about, R.id.cl_switch_family})
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        } else if (view.getId() == R.id.iv_add) {
            Intent intent = new Intent(this, MemberRegisterActivity.class);
            ActivityUtils.startActivity(intent);
        } else if (view.getId() == R.id.cl_exit_family) {
            exitFamily();
        } else if (view.getId() == R.id.cl_basic) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra(AppConstants.USER_PROFILE, mShowUser);
//            intent.putExtra(UserProfileActivity.STATUS_MODE, UserProfileActivity.MODE_READ);
            ActivityUtils.startActivity(intent);
        } else if (view.getId() == R.id.cl_phone) {
            Intent intent;
            if (!TextUtils.isEmpty(mShowUser.getPhone())) {
                intent = new Intent(this, PhoneInfoActivity.class);
                intent.putExtra(PhoneInfoActivity.PHONE, mShowUser.getPhone());
            } else {
                intent = new Intent(this, BindingPhoneActivity.class);
                intent.putExtra(BindingPhoneActivity.UPDATE_PHONE, false);
            }
            ActivityUtils.startActivity(intent);
        } else if (view.getId() == R.id.cl_ills) {
            Intent intent = new Intent(this, UpdateAnamnesisActivity.class);
            intent.putParcelableArrayListExtra(UpdateAnamnesisActivity.ILLS_LIST, mShowUser.getIlls());
            ActivityUtils.startActivity(intent);
        } else if (view.getId() == R.id.cl_about) {
            Intent intent = new Intent(this, AboutUsActivity.class);
            ActivityUtils.startActivity(intent);
        } else if (view.getId() == R.id.cl_switch_family) {
            switchFamily();
        }
    }

    private void exitFamily() {
        PromptButton cancel = new PromptButton("取消", null);
        cancel.setTextColor(ContextCompat.getColor(this, R.color.user_dialog_btn_color));

        promptDialog.showWarnAlert("您确定要退出登录吗？", cancel,
            new PromptButton("确定", new PromptButton.PromptButtonListener() {
                @Override
                public void onClick(PromptButton button) {
                promptDialog.showLoading("正在退出...");
                Injection.getUserDataRepository(getApplication())
                    .logout(mFamilyId, false, new UserDataSource.DataCallBack<Empty>() {
                        @Override
                        public void onSucceed(Disposable d, Empty data) {
                            LogUtils.i("onSucceed", data);
                            ToastUtils.showLong("退出成功");
                            promptDialog.showSuccess("退出成功");
                            finish();
                            if (!d.isDisposed()) {
                                LogUtils.i("isDisposed", true);
                                d.dispose();
                            }
                        }

                        @Override
                        public void onFailed(Disposable d, String msg) {

                            LogUtils.i("OkHttp====onFailed", msg);
                            if(msg.equals(String.valueOf(HttpConfig.TOKEN_INVALID))){
                                promptDialog.dismiss();
                                LogUtils.i("OkHttp====跳转去登录页面", msg);
                                AppConfig.logout();
                                Intent intent = new Intent(MemberListActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                promptDialog.showError("退出失败");
                                ToastUtils.showLong(msg + "，请稍后重试");
                            }

                            d.dispose();
                        }
                    });
                }
            }));
    }

    private void switchFamily() {
        promptDialog.showLoading(getString(R.string.user_loading));
        Injection.getUserDataRepository(getApplication())
            .getFamilyList(new UserDataSource.DataCallBack<List<Family>>() {
                @Override
                public void onSucceed(Disposable d, List<Family> familyList) {
                    if (!d.isDisposed()) {
                        d.dispose();
                    }

                    if (familyList.size() < 2) {
                        ToastUtils.showLong(R.string.user_switch_family_warn);
                        promptDialog.dismissImmediately();
                        return;
                    }

                    //多个家庭，进入选择要登录的家庭
                    StringBuilder ids = new StringBuilder();
                    for (Family family: familyList) {
                        ids.append(family.getFid()).append(",");
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.FID_LIST, ids.substring(0, ids.length() - 1));
                    LogUtils.i(AppConstants.FID_LIST, ids.substring(0, ids.length() - 1));
                    ActivityUtils.startActivity(bundle, MemberListActivity.this, FamilyListActivity.class);
                    ActivityUtils.finishActivity(MemberListActivity.this);

                    promptDialog.dismissImmediately();
                }

                @Override
                public void onFailed(Disposable d, String msg) {
                    ToastUtils.showLong(msg + "，请稍候重试");
                    d.dispose();
                    promptDialog.dismissImmediately();
                }
            });
    }
}
