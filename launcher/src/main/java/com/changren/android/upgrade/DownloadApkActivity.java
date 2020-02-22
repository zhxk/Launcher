package com.changren.android.upgrade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.network.FileDownLoadObserver;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.ToastUtils;
import com.changren.android.upgrade.entity.VersionConfig;
import com.changren.android.upgrade.rxbus.RxBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2019-01-18 11:09
 * Description: Dialog主题样式的Activity，用于显示升级提示
 */
public class DownloadApkActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.tv_progress)
    TextView mProgressTv;

    @BindView(R.id.btn_cancel)
    Button mCancelBtn;

    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.upgrade_dialog_progress);
        ButterKnife.bind(this);

        initWindow();

        init();
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

    private void init() {
        mProgressBar.setMax(100);
        RxBus.getDefault().subscribe(this, "download_apk", AndroidSchedulers.mainThread(),
                new RxBus.Callback<Integer>() {
            @Override
            public void onEvent(Integer progress) {
                LogUtils.i("接受下载进度："+progress);
                mProgressBar.setProgress(progress);
                mProgressTv.setText(getString(R.string.upgrade_update_progress, progress));
            }
        });
        VersionConfig versionConfig = (VersionConfig) getIntent().getSerializableExtra(UpdatePromptsActivity.VERSION_DATA);
        DownloadManager.getInstance().setContext(this);
        disposable = DownloadManager.getInstance().downloadApk(versionConfig,
                new FileDownLoadObserver<File>() {
            @Override
            public void onDownLoadSuccess(File file) {
                DownloadManager.getInstance().setUpdate(true);
                installApk(DownloadApkActivity.this, file);
                finish();
            }
            @Override
            public void onDownLoadFail(Throwable throwable) {
                LogUtils.eTag("onDownLoadFail", "下载失败"+throwable.getMessage());
                DownloadManager.getInstance().setUpdate(true);
                ToastUtils.showShort("下载失败，请重试" + throwable.getMessage());
                finish();
            }

            /** 这个方法不是运行在主线程，不能用于通知UI更新进度 */
            @Override
            public void onProgress(int progress,long total) {}
        });
    }

    @OnClick({R.id.btn_cancel})
    public void onClick(View view) {
        if (view.getId() == R.id.btn_cancel) {
            disposable.dispose();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(this);
    }

    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
