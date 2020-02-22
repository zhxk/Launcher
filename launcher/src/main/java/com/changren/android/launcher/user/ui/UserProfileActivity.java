package com.changren.android.launcher.user.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.Injection;
import com.changren.android.launcher.database.UserDataSource;
import com.changren.android.launcher.database.entity.Empty;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.database.network.HttpConfig;
import com.changren.android.launcher.user.ui.dialog.LoadingDialogFragment;
import com.changren.android.launcher.user.ui.dialog.PromptButton;
import com.changren.android.launcher.user.ui.dialog.PromptDialog;
import com.changren.android.launcher.user.ui.widget.BaseScaleView;
import com.changren.android.launcher.user.ui.widget.HorizontalScaleScrollView;
import com.changren.android.launcher.user.ui.widget.VerticalScaleScrollView;
import com.changren.android.launcher.user.ui.widget.picker.ConvertUtils;
import com.changren.android.launcher.user.ui.widget.picker.DatePicker;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.DateUtils;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.ToastUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2018-11-27 20:02
 * Description: 填写基本信息并上传服务器
 */
public class UserProfileActivity extends AppCompatActivity implements LoadingDialogFragment.OnLoadingListener {

    @BindView(R.id.et_name)
    EditText mNameEt;
    @BindView(R.id.et_nickname)
    EditText mNicknameEt;
    @BindView(R.id.switch_sex)
    Switch mSexSwitch;
    @BindView(R.id.tv_birthday)
    TextView mBirthdayTv;
    @BindView(R.id.height_scale)
    VerticalScaleScrollView mHeightScaleView;
    @BindView(R.id.weight_scale)
    HorizontalScaleScrollView mWeightScaleView;
    @BindView(R.id.tv_height)
    TextView mHeightTv;
    @BindView(R.id.tv_weight)
    TextView mWeightTv;
    @BindView(R.id.iv_sex_whole)
    ImageView mSexWholeIv;
    @BindView(R.id.btn_next)
    Button mNextBtn;

    private int heightLastScale;
    private int weightLastScale;
    private float weightCurScale;

    private User mUser;
    private boolean isUpdate = false;
    private boolean isMemberRegister = false;
    private int register_uid;
    private Bundle savedState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_profile);

        ButterKnife.bind(this);

        //TODO 偶现短暂黑屏
        init();
    }

    /** 不会回调 */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //TODO 目前采用Activity重新加载的方式，进行横竖屏切换，
        //TODO 原因是刻度尺控件横竖切换时，因为宽高变化，引起刻度初始值不准确
//        setContentView(R.layout.user_activity_profile);
//        ButterKnife.bind(this);
//        initView();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Bundle bundle = new Bundle();
        bundle.putString("username", mNameEt.getText().toString());
        bundle.putString("nickname", mNicknameEt.getText().toString());
        bundle.putInt("sex", mSexSwitch.isChecked() ? 0 : 1);
        bundle.putInt("height", heightLastScale);
        LogUtils.i("savedState"+ (int)(weightCurScale*10));
        bundle.putInt("weight", (int)(weightCurScale*10));
        bundle.putString("birth", mBirthdayTv.getText().toString());
        return bundle;
    }

    private void init() {
        mUser = getIntent().getParcelableExtra(AppConstants.USER_PROFILE);
        register_uid = getIntent().getIntExtra(AppConstants.UID, 0);

        isMemberRegister = getIntent().getIntExtra(AppConstants.REGISTER_TYPE,
                AppConstants.USER_REGISTER) == AppConstants.MEMBER_REGISTER;
        isUpdate = mUser != null;

        mSexSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSexWholeIv.setImageResource(R.drawable.ic_female_whole);
                } else {
                    mSexWholeIv.setImageResource(R.drawable.ic_male_whole);
                }
            }
        });

        savedState = (Bundle) getLastCustomNonConfigurationInstance();
        if (savedState != null) {
            LogUtils.i("savedState="+savedState.toString());
            mNameEt.setText(savedState.getString("username"));
            mNicknameEt.setText(savedState.getString("nickname"));
            //选中为女
            mSexSwitch.setChecked(savedState.getInt("sex") != 1);
            mBirthdayTv.setText(savedState.getString("birth"));
        } else {
            if (isUpdate) {
                if (TextUtils.isEmpty(mUser.getBirth())) {
                    mBirthdayTv.setText(R.string.user_default_birthday);
                } else {
                    mBirthdayTv.setText(mUser.getBirth());
                }
                mNameEt.setText(mUser.getUsername());
                mNicknameEt.setText(mUser.getNickname());
                //选中为女
                mSexSwitch.setChecked(mUser.getSex() != 1);
                mNextBtn.setText(R.string.user_update_info);
            } else {
                mBirthdayTv.setText(R.string.user_default_birthday);
            }
        }
        initView();
    }

    private void initView() {
        //横竖屏切换时,需要重置刻度
        mHeightScaleView.setOnScrollListener(new BaseScaleView.OnScrollListener() {
            @Override
            public void onScaleScroll(int scale) {
                mHeightTv.setText(getString(R.string.user_height, scale));
                ViewGroup.LayoutParams params = mSexWholeIv.getLayoutParams();

                //467,根据170cm标准换算，每增减1cm，则图片高加减2dp
                if (heightLastScale == 0) {
                    if (savedState != null) {
                        heightLastScale = savedState.getInt("height");
                    } else {
                        if (isUpdate && mUser.getStature() > 0) {
                            heightLastScale = mUser.getStature();
                        } else {
                            heightLastScale = 170;//默认身高170cm
                        }
                    }
                    mHeightScaleView.setCurScale(heightLastScale);//默认身高170cm
                }

                if (mSexWholeIv.getHeight() < 0) {
                    return;
                }
                int dex = scale - heightLastScale;
                params.height = mSexWholeIv.getHeight() + 2 * dex;
                mSexWholeIv.setLayoutParams(params);
                heightLastScale = scale;
//                LogUtils.i("heightLastScale",scale, heightLastScale, mSexWholeIv.getHeight());
            }
        });

        //横竖屏切换时,需要重置刻度
        mWeightScaleView.setOnScrollListener(new BaseScaleView.OnScrollListener() {
            @Override
            public void onScaleScroll(int scale) {
                weightCurScale = (float) scale/10;
                mWeightTv.setText(getString(R.string.user_weight, weightCurScale +""));

                //200,根据60kg标准换算，每增减1kg，则图片宽加减1.5dp
                if (weightLastScale == 0) {
                    if (savedState != null) {
                        weightLastScale = savedState.getInt("weight");
                    } else {
                        if (isUpdate && mUser.getWeight() > 0) {
                            weightLastScale = (int) (mUser.getWeight() * 10);
                        } else {
                            weightLastScale = 600;//默认体重60kg
                        }
                    }
                    mWeightScaleView.setCurScale(weightLastScale);
                }

                float dex = scale - weightLastScale;
//                LogUtils.i("weightCurScale"+ weightCurScale, scale, weightLastScale, mSexWholeIv.getWidth());
                if (Math.abs(dex) < 10) {
                    return;
                }

                ViewGroup.LayoutParams params = mSexWholeIv.getLayoutParams();

                if (mSexWholeIv.getWidth() < 0) {
                    return;
                }
                params.width = mSexWholeIv.getWidth() + (int)(2 * (dex/10));
                mSexWholeIv.setLayoutParams(params);
                weightLastScale = scale;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.iv_back, R.id.btn_next, R.id.tv_birthday})
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            if (!isChanged()) {
                finish();
                return;
            }

            //提示数据未上传，是否上传
            showConfirmDialog();
        } else if (view.getId() == R.id.btn_next) {
            if (!isChanged()) {
                ToastUtils.showLong(R.string.user_no_data_update);
                return;
            }

            if (loadingDialog == null) {
                LogUtils.i("loadingDialog Created");
                loadingDialog = LoadingDialogFragment.newInstance();
            }
            loadingDialog.show(getFragmentManager(), "loading_dialog");
        } else if (view.getId() == R.id.tv_birthday) {
            //不能点击太快，否则会出现多个dialog
            showDatePickerDialog();
        }
    }

    private boolean isChanged() {
        //非更新数据，总提示数据已变化
        if (!isUpdate) {
            return true;
        }

        //数据更新时，源数据丢失，无法提交当成数据没有变化
        if (mUser == null) {
            return false;
        }

        String username = mNameEt.getText().toString();
        String nickname = mNicknameEt.getText().toString();
        String birth = mBirthdayTv.getText().toString();
        boolean isFemale = mSexSwitch.isChecked();
        boolean isFemalePre = mUser.getSex() != 1;

        //对比数据变化
        return (!username.equals(mUser.getUsername())
            || !nickname.equals(mUser.getNickname())
            || !birth.equals(mUser.getBirth())
            || !(isFemalePre == isFemale)//服务器和本地sex不一致，server 2代表女性，本地 0代表女性，所以无法做值比较
            || weightCurScale != mUser.getWeight()
            || heightLastScale != mUser.getStature());
    }

    private PromptDialog promptDialog;

    private void showConfirmDialog() {
        //提示数据未上传，是否上传
        if (promptDialog == null) {
            //创建对象
            promptDialog = new PromptDialog(this);
            //设置自定义属性
            promptDialog.getDefaultBuilder().touchAble(true).round(10).loadingDuration(3000);
        }

        PromptButton cancel = new PromptButton("不保存", new PromptButton.PromptButtonListener() {
            @Override
            public void onClick(PromptButton button) {
                finish();
            }
        });
        cancel.setTextColor(ContextCompat.getColor(this, R.color.user_dialog_btn_color));

        promptDialog.showWarnAlert("您修改的个人信息未保存，是否要保存？", cancel,
            new PromptButton("保存", new PromptButton.PromptButtonListener() {
                @Override
                public void onClick(PromptButton button) {
                    if (loadingDialog == null) {
                        LogUtils.i("loadingDialog Created");
                        loadingDialog = LoadingDialogFragment.newInstance();
                    }
                    loadingDialog.show(getFragmentManager(), "loading_dialog");
                }
            }));
    }

    private DatePicker picker;

    private void showDatePickerDialog() {
        if (picker != null && !picker.isShowing()) {
            picker.show();
            return;
        } else if (picker != null && picker.isShowing()) {
            return;
        }

        picker = new DatePicker(this);
        picker.setCanceledOnTouchOutside(false);
        picker.setUseWeight(true);

        picker.setOffset(2);
        picker.setGravity(Gravity.CENTER);
        picker.setContentPadding(20, 10);
        picker.setTextSize(30);
        picker.setTitleTextSize(30);
        picker.setTopHeight(80);
        picker.setCancelTextSize(30);
        picker.setSubmitTextSize(30);
        picker.setTopPadding(ConvertUtils.toPx(this, 20));

        picker.setRangeStart(1900, 1, 1);

        int[] date;
        if (savedState != null) {
            String birth = savedState.getString("birth");
            picker.setTitleText(birth);
            try {
                date = DateUtils.getYMD(birth);
                picker.setSelectedItem(date[0], date[1], date[2]);
            } catch (Exception e) {
                date = DateUtils.getYMD(getString(R.string.user_default_birthday));
                picker.setSelectedItem(date[0], date[1], date[2]);
            }
        } else {
            if (isUpdate && !TextUtils.isEmpty(mUser.getBirth())) {
                try {
                    date = DateUtils.getYMD(mUser.getBirth());
                    picker.setTitleText(mUser.getBirth());
                    picker.setSelectedItem(date[0], date[1], date[2]);
                } catch (Exception e) {
                    picker.setTitleText(R.string.user_default_birthday);
                    date = DateUtils.getYMD(getString(R.string.user_default_birthday));
                    picker.setSelectedItem(date[0], date[1], date[2]);
                }
            } else {
                picker.setTitleText(R.string.user_default_birthday);
                date = DateUtils.getYMD(getString(R.string.user_default_birthday));
                picker.setSelectedItem(date[0], date[1], date[2]);
            }
        }

        date = DateUtils.getYMD(new Date());
        picker.setRangeEnd(date[0], date[1], date[2]);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                mBirthdayTv.setText(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    protected Map<String, String> paramsMap;
    protected LoadingDialogFragment loadingDialog;

    public void getUserInfo() {
        if (loadingDialog == null) return;

        String username = mNameEt.getText().toString();
        String nickname = mNicknameEt.getText().toString();

        if(TextUtils.isEmpty(username) && TextUtils.isEmpty(nickname)) {
            //二者不能同时为空
            loadingDialog.setErrorMessage(getString(R.string.user_info_name_incomplete));
            return;
        }

        String height = String.valueOf(heightLastScale);
        String weight = weightCurScale + "";
        if(TextUtils.isEmpty(height) || TextUtils.isEmpty(weight)) {
            loadingDialog.setErrorMessage(getString(R.string.user_info_w_h_no_empty));
            return;
        }
        String birth = mBirthdayTv.getText().toString();
        if(TextUtils.isEmpty(birth)) {
            loadingDialog.setErrorMessage(getString(R.string.user_info_no_empty, getString(R.string.user_birthday)));
            return;
        }
        //1是男，0或其他是女
        String sex = mSexSwitch.isChecked() ? "0" : "1";
        String default_avatar = mSexSwitch.isChecked()
                ? HttpConfig.BASE_URL + HttpConfig.AVATAR_PATH_FEMALE
                : HttpConfig.BASE_URL + HttpConfig.AVATAR_PATH_MALE;

        if (paramsMap == null) {
            paramsMap = new HashMap<>();
        }
        paramsMap.put("username", TextUtils.isEmpty(username) ? nickname : username);
        paramsMap.put("nickname", TextUtils.isEmpty(nickname) ? username : nickname);
        paramsMap.put("sex", sex);
        paramsMap.put("stature", height);
        paramsMap.put("weight", weight);
        paramsMap.put("birth", birth);
        paramsMap.put("default_avatar", default_avatar);
        if (register_uid != 0) {
            paramsMap.put("mid", register_uid + "");
        }
    }

    private void submitInfo() {
        if (paramsMap == null) {
            return;
        }

        if (isUpdate) {
            Injection.getUserDataRepository(getApplication())
                .updateUserInfo(AppConfig.getToken(), paramsMap, new UserDataSource.DataCallBack<User>() {

                    @Override
                    public void onSucceed(Disposable d, User user) {
                        mUser = user;
                        loadingDialog.setSuccessMessage(null);
                        if (!d.isDisposed()) {
                            d.dispose();
                        }
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        loadingDialog.setFailedMessage(msg);
                        d.dispose();
                    }
                });
        } else {
            if (isMemberRegister) {
                Injection.getUserDataRepository(getApplication())
                    .setMemberInfo(paramsMap, new UserDataSource.DataCallBack<User>() {

                        @Override
                        public void onSucceed(Disposable d, User user) {
                            register_uid = user.getUid();
                            loadingDialog.setSuccessMessage(null);
                            if (!d.isDisposed()) {
                                d.dispose();
                            }
                        }

                        @Override
                        public void onFailed(Disposable d, String msg) {
                            loadingDialog.setFailedMessage(msg);
                            if (d != null) {
                                d.dispose();
                            }
                        }
                    });
            } else {
                Injection.getUserDataRepository(getApplication())
                    .setUserInfo(AppConfig.getToken(), paramsMap, new UserDataSource.DataCallBack<User>() {

                        @Override
                        public void onSucceed(Disposable d, User user) {
                            loadingDialog.setSuccessMessage(null);
                            if (!d.isDisposed()) {
                                d.dispose();
                            }
                        }

                        @Override
                        public void onFailed(Disposable d, String msg) {
                            loadingDialog.setFailedMessage(msg);
                            if (d != null) {
                                d.dispose();
                            }
                        }
                    });
            }
        }
    }

    @Override
    public void startLoader() {
        getUserInfo();
        submitInfo();
    }

    @Override
    public void reload() {
        //重新上传，用户没有更新信息，所以不需要再次获取User Info
        if (paramsMap == null) {
            getUserInfo();
        }
        submitInfo();
    }

    @Override
    public void loadCompleted() {
        if (isUpdate) {
            ToastUtils.showLong("修改成功");
            ActivityUtils.finishActivity(UserProfileActivity.this);
            return;
        }

        //跳转到下一个页面
        //需要传递default_avatar
        Bundle bundle = new Bundle();
        bundle.putString("sex", paramsMap.get("sex"));
        bundle.putString("default_avatar", paramsMap.get("default_avatar"));
        if (register_uid != 0) {
            bundle.putInt(AppConstants.UID, register_uid);
        }
        ActivityUtils.startActivity(bundle, UserProfileActivity.this, AvatarSettingActivity.class);
        ActivityUtils.finishActivity(UserProfileActivity.this);
    }

    @Override
    public void cancel() {
        //取消上传的网络请求
        LogUtils.i("Dialog cancel 被调用");
    }
}
