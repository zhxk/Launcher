package com.changren.android.launcher.user.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.upgrade.DownloadManager;
import com.changren.android.upgrade.UpgradeService;
import com.changren.android.upgrade.util.DeviceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Author: wangsy
 * Create: 2019-02-28 11:55
 * Description: “关于我们”界面
 */
public class AboutUsActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_app_version)
    TextView mVersionTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_about_us);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mTitleTv.setText(R.string.user_info_about);
        mVersionTv.setText(getString(R.string.upgrade_current_version, DeviceUtils.getVersionName(this)));
    }

    @OnClick({R.id.iv_back, R.id.btn_check_update})
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                EasyPermissions.requestPermissions(this, "需要授权文件存储权限，否则程序无法正常运行。", 0,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                UpgradeService.startUpgradeService(this);
                DownloadManager.getInstance().setShowMsg(true);
            }
        }
    }
}
