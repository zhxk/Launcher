package com.changren.android.launcher.user.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.Injection;
import com.changren.android.launcher.database.UserDataSource;
import com.changren.android.launcher.database.entity.MedicalInfoEntity;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.user.ui.dialog.LoadingDialogFragment;
import com.changren.android.launcher.user.ui.dialog.PromptDialog;
import com.changren.android.launcher.user.ui.widget.flowlayout.FlowLayout;
import com.changren.android.launcher.user.ui.widget.flowlayout.TagAdapter;
import com.changren.android.launcher.user.ui.widget.flowlayout.TagFlowLayout;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2018-12-04 15:52
 * Description: 既往病史填写
 */
public class AnamnesisActivity extends AppCompatActivity implements LoadingDialogFragment.OnLoadingListener {

    @BindView(R.id.tag_anamnesis)
    TagFlowLayout mTagAnamnesisLayout;

    private List<MedicalInfoEntity> mDatas;
    private int register_uid;

    private PromptDialog promptDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_anamnesis);

        ButterKnife.bind(this);

        loadData();
    }

    private void loadData() {
        register_uid = getIntent().getIntExtra(AppConstants.UID, 0);
        LogUtils.e("既往病史", register_uid);
        //先弹窗提示"是否退出登录"
        if (promptDialog == null) {
            //创建对象
            promptDialog = new PromptDialog(this);
            //设置自定义属性
            promptDialog.getDefaultBuilder().touchAble(true).round(10).loadingDuration(3000);
        }
        promptDialog.showLoading(getString(R.string.user_loading));
        final LayoutInflater mInflater = LayoutInflater.from(this);
        Injection.getUserDataRepository(getApplication())
            .getAnamnesisList(AppConfig.getToken(),
                new UserDataSource.DataCallBack<List<MedicalInfoEntity>>() {
                    @Override
                    public void onSucceed(Disposable d, List<MedicalInfoEntity> data) {
                        LogUtils.i("onSucceed", data.size());
                        mDatas = data;
                        mTagAnamnesisLayout.setAdapter(new TagAdapter<MedicalInfoEntity>(data) {
                            @Override
                            public View getView(FlowLayout parent, int position, MedicalInfoEntity o) {
                                TextView tv = (TextView) mInflater.inflate(R.layout.user_item_tag, mTagAnamnesisLayout, false);
                                tv.setText(o.getName());
                                return tv;
                            }
                        });
                        promptDialog.dismissImmediately();
                        if (!d.isDisposed()) {
                            LogUtils.i("isDisposed", true);
                            d.dispose();
                        }
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        promptDialog.dismissImmediately();
                        ToastUtils.showLong("拉取既往病史数据失败," + msg);
                        if (d != null) {
                            d.dispose();
                        }
                    }
                }
            );
    }

    protected LoadingDialogFragment loadingDialog;
    private Map<String, String> params;

    @OnClick({R.id.iv_back, R.id.btn_next})
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.btn_next) {
            Set<Integer> selectedList =  mTagAnamnesisLayout.getSelectedList();
            if (selectedList.size() == 0) {
                startActivity();
                return;
            }

            if (mDatas.size() < selectedList.size()) {
                //提示“数据异常”
                return;
            }

            StringBuilder selectedStr = new StringBuilder();
            for (Integer id: selectedList) {
                selectedStr.append(mDatas.get(id).getId()).append(",");
            }

            if (params == null) {
                params = new HashMap<>();
            }
            params.put("ills", selectedStr.substring(0, selectedStr.length() - 1));
            LogUtils.i("选中结果", selectedStr.substring(0, selectedStr.length() - 1));

            if (loadingDialog == null) {
                loadingDialog = LoadingDialogFragment.newInstance();
            }
            loadingDialog.show(getFragmentManager(), "loading_dialog");
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        unsubscribe();
//    }

//    protected void unsubscribe() {
//        mCompositeDisposable.clear();
//    }

    private void submitInfo() {
        if (params == null) {
            return;
        }
        if (register_uid != 0) {
            params.put("mid", register_uid + "");
            Injection.getUserDataRepository(getApplication())
                .updateMemberInfo(AppConfig.getToken(), params, new UserDataSource.DataCallBack<User>() {

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
                .updateUserInfo(AppConfig.getToken(), params, new UserDataSource.DataCallBack<User>() {

                    @Override
                    public void onSucceed(Disposable d, User user) {
                        LogUtils.i("onSucceed", user.toString());
                        loadingDialog.setSuccessMessage(null);
                        if (!d.isDisposed()) {
                            LogUtils.i("isDisposed", true);
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
        submitInfo();
    }

    @Override
    public void reload() {
        submitInfo();
    }

    @Override
    public void loadCompleted() {
        startActivity();
    }

    private void startActivity() {
        if (register_uid != 0) {
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstants.FID, AppConfig.getLoginFamilyId());
            bundle.putBoolean(AppConstants.RESTART, true);
            ActivityUtils.startActivity(bundle, this, MemberListActivity.class);
            ActivityUtils.finishActivity(this);
        } else {
            ActivityUtils.startActivity(AnamnesisActivity.this, CreateFamilyActivity.class);
            ActivityUtils.finishActivity(AnamnesisActivity.this);
        }
    }

    @Override
    public void cancel() {
        //取消上传的网络请求
        LogUtils.i("Dialog cancel 被调用");
    }
}
