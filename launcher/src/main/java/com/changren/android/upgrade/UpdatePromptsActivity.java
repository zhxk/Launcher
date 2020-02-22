package com.changren.android.upgrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.upgrade.entity.VersionConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: wangsy
 * Create: 2019-01-18 11:09
 * Description: Dialog主题样式的Activity，用于显示升级提示
 */
public class UpdatePromptsActivity extends AppCompatActivity {

    public static final String VERSION_DATA = "version_data";

    @BindView(R.id.tv_content)
    TextView mContentTv;

    @BindView(R.id.tv_cancel)
    TextView mCancelTv;

    @BindView(R.id.tv_update)
    TextView mUpdateTv;

    @BindView(R.id.space3)
    View mVerticalLine;

    private VersionConfig versionConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.upgrade_dialog_default);
        ButterKnife.bind(this);

        initWindow();

        initView();
    }

    private void initWindow() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.dimAmount = 0.7f;
            window.setAttributes(lp);
            window.setGravity(Gravity.CENTER);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    private void initView() {
        versionConfig = (VersionConfig) getIntent().getSerializableExtra(VERSION_DATA);
        mContentTv.setText(versionConfig.getUpdate_content());
        //强制更新隐藏cancel按钮
        if (versionConfig.getMust_update() == 1) {
            mCancelTv.setVisibility(View.GONE);
            mVerticalLine.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_update})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_cancel) {
            DownloadManager.getInstance().setUpdate(false);
            finish();
        } else {
            //下载apk，提示进度
            Intent intent = new Intent(this, DownloadApkActivity.class);
            intent.putExtra(UpdatePromptsActivity.VERSION_DATA, versionConfig);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        finish();
//        return super.onTouchEvent(event);
//    }
}
