package com.changren.android.launcher.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.Injection;
import com.changren.android.launcher.database.UserDataSource;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.database.network.HttpConfig;
import com.changren.android.launcher.user.ui.dialog.LoadingDialogFragment;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2018-11-28 10:31
 * Description: 设置成员头像
 */
public class AvatarSettingActivity extends AppCompatActivity implements LoadingDialogFragment.OnLoadingListener {

    @BindView(R.id.iv_avatar)
    CircleImageView mAvatarIV;

    private String default_avatar;
    private int register_uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_avatar);

        ButterKnife.bind(this);

        init();
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        String sex;
        if (bundle != null) {
            sex = getIntent().getExtras().getString("sex");
            default_avatar = getIntent().getExtras().getString("default_avatar");
        } else {
            sex = "";
            default_avatar = HttpConfig.BASE_URL + HttpConfig.AVATAR_PATH_MALE;
        }

        register_uid = getIntent().getIntExtra(AppConstants.UID, 0);

        if (TextUtils.isEmpty(sex) || "1".equals(sex)) {
            mAvatarIV.setImageResource(R.drawable.default_male_middle);
        } else {
            mAvatarIV.setImageResource(R.drawable.default_female_middle);
        }
    }

    protected LoadingDialogFragment loadingDialog;

    @OnClick({R.id.iv_back, R.id.btn_next})
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.btn_next) {
            if (loadingDialog == null) {
                LogUtils.i("loadingDialog Created");
                loadingDialog = LoadingDialogFragment.newInstance();
            }
            loadingDialog.show(getFragmentManager(), "loading_dialog");
        }
    }

    private Map<String, String> args;
    private String avatar_path;

    private void getUserInfo() {
        //先测试功能可用
        if (args == null) {
            args = new HashMap<>();
        }

        args.put("default_avatar", default_avatar);

//        avatar_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bluetooth/IMG_20181113_171649.jpg";
        LogUtils.i("avatar_path", avatar_path, default_avatar);
    }

    private void submitInfo() {
        if (args == null) {
            return;
        }

        if (TextUtils.isEmpty(avatar_path)) {
            loadCompleted();
            return;
        }

        if (register_uid != 0) {
            args.put("mid", register_uid + "");
            Injection.getUserDataRepository(getApplication())
                .updateMemberInfo(AppConfig.getToken(), args, avatar_path, new UserDataSource.DataCallBack<User>() {

                    @Override
                    public void onSucceed(Disposable d, User user) {
                        LogUtils.i("onSucceed", user.toString());
                        loadingDialog.setSuccessMessage(null);
                        if (!d.isDisposed()) {
                            d.dispose();
                        }
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        LogUtils.i("onFailed", msg);
                        loadingDialog.setFailedMessage(msg);
                        if (d != null) {
                            d.dispose();
                        }
                    }
                });
        } else {
            Injection.getUserDataRepository(getApplication())
                .updateUserInfo(AppConfig.getToken(), args, avatar_path, new UserDataSource.DataCallBack<User>() {

                    @Override
                    public void onSucceed(Disposable d, User user) {
                        LogUtils.i("onSucceed", user.toString());
                        loadingDialog.setSuccessMessage(null);
                        if (!d.isDisposed()) {
                            d.dispose();
                        }
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        LogUtils.i("onFailed", msg);
                        loadingDialog.setFailedMessage(msg);
                        if (d != null) {
                            d.dispose();
                        }
                    }
                });
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
        if (args == null || TextUtils.isEmpty(avatar_path)) {
            getUserInfo();
        }
    }

    @Override
    public void loadCompleted() {
        //跳转到下一个页面
        Intent intent = new Intent(AvatarSettingActivity.this, AnamnesisActivity.class);
        if (register_uid != 0) {
            intent.putExtra(AppConstants.UID, register_uid);
        }
        ActivityUtils.startActivity(intent);
        ActivityUtils.finishActivity(AvatarSettingActivity.this);
    }

    @Override
    public void cancel() {
        //取消上传的网络请求
        LogUtils.i("Dialog cancel 被调用");
    }
}
