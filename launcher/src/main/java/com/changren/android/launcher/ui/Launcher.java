package com.changren.android.launcher.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.changren.android.component.BadgeView;
import com.changren.android.component.PagerIndicator.ScrollingPagerIndicator;
import com.changren.android.component.carousellayoutmanager.CarouselLayoutManager;
import com.changren.android.component.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.changren.android.component.carousellayoutmanager.CenterScrollListener;
import com.changren.android.component.slidingwindow.AutoPlayRecyclerView;
import com.changren.android.component.slidingwindow.ScaleLayoutManager;
import com.changren.android.component.slidingwindow.ViewPagerLayoutManager;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.Injection;
import com.changren.android.launcher.database.UserDataSource;
import com.changren.android.launcher.database.entity.DataSource;
import com.changren.android.launcher.database.entity.HealthScore;
import com.changren.android.launcher.database.entity.HealthUserScore;
import com.changren.android.launcher.database.entity.InformationBean;
import com.changren.android.launcher.database.entity.PlanTodayBean;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.database.network.HttpConfig;
import com.changren.android.launcher.ui.adapter.HealthCardAdapter;
import com.changren.android.launcher.ui.fragment.FolderSupportDialogFragment;
import com.changren.android.launcher.user.ui.LoginActivity;
import com.changren.android.launcher.user.ui.MemberListActivity;
import com.changren.android.launcher.user.ui.dialog.PromptButton;
import com.changren.android.launcher.user.ui.dialog.PromptDialog;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.DateUtils;
import com.changren.android.launcher.util.GlideApp;
import com.changren.android.launcher.util.InstallUtils;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.ToastUtils;
import com.changren.android.launcher.util.Utils;
import com.changren.android.launcher.widget.ColorArcProgressBar;
import com.changren.android.upgrade.UpgradeService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Launcher extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    List<DataSource> mDatas;

    @BindView(R.id.rv_banner)
    AutoPlayRecyclerView mRecycler;

    @BindView(R.id.scrollingPagerIndicator)
    ScrollingPagerIndicator mPagerIndicator;

    @BindView(R.id.iv_home_tabs_calendar)
    ImageView mCalendarView;

    @BindView(R.id.tv_system_time)
    TextView mTimeTv;

    @BindView(R.id.progress_bar_battery)
    ProgressBar mProgressBar;

    @BindView(R.id.tv_battery)
    TextView mBatteryTv;

    @BindView(R.id.colorArcProgressBar)
    ColorArcProgressBar mColorProgressBar;

    @BindView(R.id.iv_home_tabs_user)
    ImageView mUserAvatarIv;

    @BindView(R.id.tv_home_tabs_user)
    TextView mUserNameTv;

    private BadgeView mCalendarBadgeView;

    private HealthCardAdapter cardAdapter;

    private BroadcastReceiver systemTimeReceiver;
    private BatteryManager batteryManager;

    private int mOrientation;
    private int mBattery;

    private Handler mHandler;
    private Runnable autoPlayRunnable;
    private int timeInterval = 30 * 1000;
    private boolean runnableAdded = false;
    private int batteryStatus = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity_launcher);

        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE && !runnableAdded && mHandler != null) {
            mHandler.postDelayed(autoPlayRunnable, timeInterval);
            runnableAdded = true;
        }

        //        getSystemTime();

        getSystemTimeTwo();
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "需要授权文件存储权限，否则程序无法正常运行。", 0,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            UpgradeService.startUpgradeService(this);
        }

        loadUserInfo();
    }

    private void initView() {
        mOrientation = getResources().getConfiguration().orientation;

        mDatas = new ArrayList<>(3);
        DataSource healthUserScore = new HealthUserScore();
        DataSource planTodayBean = new PlanTodayBean();
        DataSource informationBean = new InformationBean();
        mDatas.add(healthUserScore);
        mDatas.add(planTodayBean);
        mDatas.add(informationBean);

        cardAdapter = new HealthCardAdapter(this, mDatas);

        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            initVerticalRecyclerView();
        } else {
            initHorizontalRecyclerView();
        }

        mCalendarBadgeView = new BadgeView(this, mCalendarView);
        mCalendarBadgeView.setText("9");
        mCalendarBadgeView.setTextSize(20);
        mCalendarBadgeView.show();
//        mCalendarBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
    }

    private void initHorizontalRecyclerView() {
        ScaleLayoutManager scaleLayoutManager = new ScaleLayoutManager(this, 0,
                ViewPagerLayoutManager.HORIZONTAL);
        scaleLayoutManager.setInfinite(true);
        scaleLayoutManager.setMaxVisibleItemCount(3);
        scaleLayoutManager.setMinScale(0.8f);
        scaleLayoutManager.setItemSpace(-55);
        mRecycler.setLayoutManager(scaleLayoutManager);
        mRecycler.setAdapter(cardAdapter);
        //一次滑动一个item
        //        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        //        pagerSnapHelper.attachToRecyclerView(mRecycler);
        mRecycler.scrollToPosition(0);
        mPagerIndicator.setLooped(true);
        mPagerIndicator.setVisibleDotCount(3);
        mPagerIndicator.attachToRecyclerView(mRecycler);
    }

    private void initVerticalRecyclerView() {
        // enable zoom effect. this line can be customized
        CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.VERTICAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(2);

        mRecycler.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        mRecycler.setHasFixedSize(true);
        // sample adapter with random data
        mRecycler.setAdapter(cardAdapter);
        // enable center post scrolling
        mRecycler.addOnScrollListener(new CenterScrollListener());

        autoMoveRecyclerView(layoutManager);
    }

    private void autoMoveRecyclerView(final CarouselLayoutManager layoutManager) {
        mHandler = new Handler();

        autoPlayRunnable = new Runnable() {
            @Override
            public void run() {
                final int currentPosition = layoutManager.getCenterItemPosition();
                if (currentPosition == mDatas.size() - 1) {
//                    mRecycler.scrollToPosition(0);
                    mRecycler.smoothScrollToPosition(0);
                } else {
                    mRecycler.smoothScrollToPosition(currentPosition + 1);
                }

                mHandler.postDelayed(autoPlayRunnable, timeInterval);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (runnableAdded && mHandler != null) {
            mHandler.removeCallbacks(autoPlayRunnable);
            runnableAdded = false;
        }

        unregisterReceiver(systemTimeReceiver);
        systemTimeReceiver = null;
    }

    /**
     * 监听"Intent.ACTION_TIME_TICK"广播,一分钟一次，但是不是所有系统都适用
     */
    private void getSystemTime() {
        String sysTimeStr = DateUtils.formatDate("HH:mm");
        mTimeTv.setText(sysTimeStr); //更新时间
        if (systemTimeReceiver == null) {//不能重复注册
            systemTimeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_TIME_TICK)) {
                        //刷新UI
                        CharSequence sysTimeStr = DateUtils.formatDate("HH:mm");
                        mTimeTv.setText(sysTimeStr); //更新时间
                    } else if (!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                        //监听系统电量变化广播
                        //刷新UI
                        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                        mBattery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                        int level = intent.getIntExtra("level", 0);//获取当前电量
                        int scale = intent.getIntExtra("scale", 0);//获取总电量
                        int stat = intent.getIntExtra("status", 0);//获取充电状态

                        LogUtils.i("OkHttp====电量", mBattery);
                        LogUtils.i("OkHttp====level", level);
                        LogUtils.i("OkHttp====scale", scale);
                        LogUtils.i("OkHttp====stat", stat);
                        mBatteryTv.setText("" + mBattery + "%");
                        //设置电量百分比
                        mProgressBar.setProgress(mBattery);
                        //设置电池图形显示百分比
                        int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                        //充电状态检测图标颜色切换
                        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                            mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_charged));
                            mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_charged));
                        } else {
                            mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_uncharged));
                            mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_uncharged));
                        }

                        if (mBattery <= 20) {
                            mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_low_battery_progress_changed));
                            mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_low_battery_progress_changed));

                        }
                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(systemTimeReceiver, filter);
    }


    private void getSystemTimeTwo() {
        String sysTimeStr = DateUtils.formatDate("HH:mm");
        mTimeTv.setText(sysTimeStr); //更新时间
        if (systemTimeReceiver == null) {//不能重复注册
            systemTimeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_TIME_TICK)) {
                        //刷新UI
                        CharSequence sysTimeStr = DateUtils.formatDate("HH:mm");
                        mTimeTv.setText(sysTimeStr); //更新时间
                    } else if (!TextUtils.isEmpty(action) && action.equals("com.changren.check.jwl.batterypower")) {
                        //监听系统电量变化广播
                        //刷新UI
                        mBattery = intent.getIntExtra("battery",0);
                        LogUtils.i("OkHttp==getSystemTimeTwo==电量", mBattery);
                        mBatteryTv.setText("" + mBattery + "%");
                        //设置电量百分比
                        mProgressBar.setProgress(mBattery);
                        if (mBattery <= 20) {
                            mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_low_battery_progress_changed));
                            mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_low_battery_progress_changed));

                        }
                    }else if(!TextUtils.isEmpty(action) && action.equals("com.changren.check.jwl.batterystatus")){
                        //设置电池图形显示百分比
                        batteryStatus = intent.getIntExtra("batteryStatus", 0);
                        LogUtils.i("OkHttp==getSystemTimeTwo==充电状态", batteryStatus);
                        //充电状态检测图标颜色切换
                        if (batteryStatus == 1) {
                            mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_charged));
                            mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_charged));
                        } else {
                            if (mBattery <= 20) {
                                mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_low_battery_progress_changed));
                                mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_low_battery_progress_changed));
                            }else{
                                mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_uncharged));
                                mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_uncharged));
                            }
                        }

                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction("com.changren.check.jwl.batterypower");
        filter.addAction("com.changren.check.jwl.batterystatus");
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(systemTimeReceiver, filter);
    }

    //R.id.iv_home_tabs_calendar,
    @OnClick({R.id.tv_home_tabs_personal, R.id.iv_home_tabs_personal, R.id.tv_home_tabs_service,
            R.id.iv_home_tabs_service, R.id.iv_home_tabs_user, R.id.iv_home_tabs_calendar,
            R.id.tv_home_tabs_setting, R.id.iv_home_tabs_setting, R.id.colorArcProgressBar})
    public void OnClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_home_tabs_personal || id == R.id.iv_home_tabs_personal) {
            //个人健康
            FolderSupportDialogFragment fragment = FolderSupportDialogFragment.newInstance(
                    1,
                    12,
                    6,
                    true,
                    false,
                    false,
                    false
            );
            fragment.show(getSupportFragmentManager(), "blur_sample");
        } else if (id == R.id.tv_home_tabs_service || id == R.id.iv_home_tabs_service) {
            //健康服务
            FolderSupportDialogFragment fragment = FolderSupportDialogFragment.newInstance(
                    2,
                    12,
                    6,
                    true,
                    false,
                    false,
                    false
            );
            fragment.show(getSupportFragmentManager(), "blur_sample");
            //add for sort app by shibo.zheng 20190307 start
        } else if (id == R.id.tv_home_tabs_setting || id == R.id.iv_home_tabs_setting) {
            //应用
            FolderSupportDialogFragment fragment = FolderSupportDialogFragment.newInstance(
                    3,
                    12,
                    6,
                    true,
                    false,
                    false,
                    false
            );
            fragment.show(getSupportFragmentManager(), "blur_sample");
            //add for sort app by shibo.zheng 20190307 end
        } else if (id == R.id.iv_home_tabs_user) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.colorArcProgressBar) {
            if (InstallUtils.isAvilible(Launcher.this, AppConstants.MONITOR_PACKAGE_NAME)) {
                Intent intent = new Intent();
                ComponentName cn = new ComponentName(AppConstants.MONITOR_PACKAGE_NAME, AppConstants.MONITOR_GENERAL_REPORT);
                intent.setComponent(cn);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {//应用未安装
                ToastUtils.showLong("健康检测应用未安装");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 当权限被成功申请的时候执行回调，requestCode是代表你权限请求的识别码，list里面装着申请的权限的名字：
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode) {
            case 0:
                UpgradeService.startUpgradeService(this);
                break;
        }
    }

    /**
     * 当权限申请失败的时候执行的回调，requestCode是代表你权限请求的识别码，list里面装着申请的权限的名字：
     * 官方还建议用EasyPermissions.somePermissionPermanentlyDenied(this, perms)方法
     * 来判断是否有权限被勾选了不再询问并拒绝，还提供了一个AppSettingsDialog来给我们使用，
     * 在这个对话框里面解释了APP需要这个权限的原因，用户按下是的话会跳到APP的设置界面，可以去设置权限
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //处理权限名字字符串
        StringBuilder sb = new StringBuilder();
        for (String str : perms) {
            sb.append(str);
            sb.append("\n");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //必须要有的权限，需要不断提示用户给予权限，否则无法正常使用app
            Toast.makeText(this, "已拒绝权限" + sb + "并不再询问", Toast.LENGTH_SHORT).show();
            new AppSettingsDialog
                    .Builder(this)
                    .setTitle("权限请求")
                    .setRationale("程序正常运行需要" + sb + "权限，否则无法正常使用，是否打开设置")
                    .setPositiveButton("去设置")
                    .setNegativeButton("算了")
                    .build()
                    .show();
        }
    }

    /**
     * Android4.0以后，无论是在onCreate()还是onAttachedToWindow()中都不能重设window的Type，想用此法屏蔽Home键无效！
     */
    @Override
    public void onAttachedToWindow() {
        //getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {
        //屏蔽Back键
        //super.onBackPressed();
    }

    private void loadUserInfo() {
        Injection.getUserDataRepository(getApplication())
                .getShowUser(new UserDataSource.DataCallBack<User>() {

                    @Override
                    public void onSucceed(Disposable d, User user) {
                        if (!d.isDisposed()) {
                            d.dispose();
                        }
                        LogUtils.i("====Show User", user.toString());
                        mUserNameTv.setText(user.getName());
                        GlideApp.with(Launcher.this)
                                .load(user.getAvatar())
                                .placeholder(user.getSex() == 1
                                        ? R.drawable.default_male_middle
                                        : R.drawable.default_female_middle)
                                .into(mUserAvatarIv);

                        //加载数据，成员头像，健康指数，Banner数据
                        loadBanner();
                        refreshHealthScore();
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        if (d != null) {
                            d.dispose();
                        }
                        if (AppConstants.RESTART_LOGIN.equals(msg)) {
                            mUserNameTv.setText(R.string.home_tabs_default_user);
                            mUserAvatarIv.setImageResource(R.drawable.home_tabs_default_user);
                            mColorProgressBar.setCurrentValues(0);
                            for (DataSource dataSource : mDatas) {
                                dataSource.clear();
                            }
                            cardAdapter.notifyDataSetChanged();
                            showLoginDialog();
                        } else {
                            ToastUtils.showLong(msg);
                        }
                    }
                });
    }

    private void loadBanner() {
        Injection.getUserDataRepository(getApplication())
                .getLauncherBannerData(3, new UserDataSource.DataCallBack<List<DataSource>>() {

                    @Override
                    public void onSucceed(Disposable d, List<DataSource> dataSources) {
                        if (!d.isDisposed()) {
                            d.dispose();
                        }
                        mDatas.clear();
                        mDatas.addAll(dataSources);
                        cardAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        if (d != null) {
                            d.dispose();
                        }
                        if(msg.equals(String.valueOf(HttpConfig.TOKEN_INVALID))){
                            promptDialog.dismiss();
                            LogUtils.i("OkHttp====跳转去登录页面", msg);
                            ToastUtils.showLong( "登录失效，请重新登录");
                            AppConfig.logout();
                            Intent intent = new Intent(Launcher.this, LoginActivity.class);
                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else{
                            LogUtils.i("====访问失败==" + msg);
                            //下载数据失败
                            ToastUtils.showLong("动态轮播栏数据加载失败，" + msg);
                        }
                    }
                });
    }

    private void refreshHealthScore() {
        Injection.getUserDataRepository(Utils.getApp())
                .getHealthScore(new UserDataSource.DataCallBack<HealthScore>() {

                    @Override
                    public void onSucceed(Disposable d, HealthScore data) {
                        if (!d.isDisposed()) {
                            d.dispose();
                        }
                        mColorProgressBar.setCurrentValues(data.getIndex());
                        LogUtils.i("健康指数==" + data.toString());
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        if (d != null) {
                            d.dispose();
                        }

                        if(msg.equals(String.valueOf(HttpConfig.TOKEN_INVALID))){
                            promptDialog.dismiss();
                            LogUtils.i("OkHttp====跳转去登录页面", msg);
                            ToastUtils.showLong( "登录失效，请重新登录");
                            AppConfig.logout();
                            Intent intent = new Intent(Launcher.this, LoginActivity.class);
                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else {
                            LogUtils.i("====访问失败==" + msg);
                            //下载数据失败
                            ToastUtils.showLong("健康指数加载失败，" + msg);
                        }
                    }
                });
    }

    private PromptDialog promptDialog;

    private void showLoginDialog() {
        //先弹窗提示"是否退出登录"
        if (promptDialog == null) {
            //创建对象
            promptDialog = new PromptDialog(Launcher.this);
            //设置自定义属性
            promptDialog.getDefaultBuilder().touchAble(true).round(10).loadingDuration(3000);
        }

        PromptButton cancel = new PromptButton("取消", null);
        cancel.setTextColor(ContextCompat.getColor(Launcher.this, R.color.user_dialog_btn_color));
        promptDialog.showWarnAlert("您还未登录，请先登录体验更多功能!", cancel,
                new PromptButton("去登录", new PromptButton.PromptButtonListener() {
                    @Override
                    public void onClick(PromptButton button) {
                        Intent intent = new Intent(Launcher.this, LoginActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }));
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.home_activity_launcher);
        ButterKnife.bind(this);
        initView();
        if(mBattery>0){
            mBatteryTv.setText("" + mBattery + "%");
            //设置电量百分比
            mProgressBar.setProgress(mBattery);
            if (mBattery <= 20) {
                mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_low_battery_progress_changed));
                mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_low_battery_progress_changed));
            }
        }
        //充电状态检测图标颜色切换
        if (batteryStatus == 1) {
            mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_charged));
            mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_charged));
        } else {
            mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_uncharged));
            mProgressBar.setProgressDrawable(ContextCompat.getDrawable(Launcher.this, R.drawable.home_battery_progress_uncharged));
        }
    }
}
