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
import com.changren.android.launcher.user.ui.dialog.PromptDialog;
import com.changren.android.launcher.user.ui.widget.flowlayout.FlowLayout;
import com.changren.android.launcher.user.ui.widget.flowlayout.TagAdapter;
import com.changren.android.launcher.user.ui.widget.flowlayout.TagFlowLayout;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2019-02-27 10:25
 * Description: 完善既往病史界面
 */
public class UpdateAnamnesisActivity extends AppCompatActivity {

    public static final String ILLS_LIST = "ILLS_LIST";

    @BindView(R.id.tv_title)
    TextView titleTv;

    @BindView(R.id.tag_known_ills)
    TagFlowLayout mTagKnownLayout;

    @BindView(R.id.tag_unknown_ills)
    TagFlowLayout mTagUnknownLayout;

    private LayoutInflater mInflater;
    //    protected final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private List<MedicalInfoEntity> mKnownList;
    private List<MedicalInfoEntity> mUnknownList;

    private PromptDialog promptDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_member_ills);
        ButterKnife.bind(this);

        init();

        loadData();
    }

    private void init() {
        mKnownList = getIntent().getParcelableArrayListExtra(ILLS_LIST);

        titleTv.setText(R.string.user_anamnesis);

        //先弹窗提示"是否退出登录"
        if (promptDialog == null) {
            //创建对象
            promptDialog = new PromptDialog(this);
            //设置自定义属性
            promptDialog.getDefaultBuilder().touchAble(true).round(10).loadingDuration(3000);
        }

        mInflater = LayoutInflater.from(this);

        //设置不能点击
        mTagKnownLayout.setEnabled(false);
    }

    private void loadData() {
        promptDialog.showLoading(getString(R.string.user_loading));
        if (mKnownList != null && mKnownList.size() > 0) {
            mTagKnownLayout.setAdapter(new TagAdapter<MedicalInfoEntity>(mKnownList) {
                @Override
                public View getView(FlowLayout parent, int position, MedicalInfoEntity o) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.user_item_tag, mTagKnownLayout, false);
                    tv.setText(o.getName());
                    return tv;
                }

                @Override
                public boolean setSelected(int position, MedicalInfoEntity medicalInfoEntity) {
                    return true;
                }
            });
        } else {
            //显示no data提示
            MedicalInfoEntity no_data = new MedicalInfoEntity(0, getString(R.string.user_no_history_ills));
            List<MedicalInfoEntity> noDataList = new ArrayList<>();
            noDataList.add(no_data);

            mTagKnownLayout.setAdapter(new TagAdapter<MedicalInfoEntity>(noDataList) {
                @Override
                public View getView(FlowLayout parent, int position, MedicalInfoEntity o) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.user_item_tag_no_data, mTagKnownLayout, false);
                    tv.setText(o.getName());
                    return tv;
                }
            });
        }

        Injection.getUserDataRepository(getApplication())
            .getAnamnesisList(AppConfig.getToken(),
                new UserDataSource.DataCallBack<List<MedicalInfoEntity>>() {
                    @Override
                    public void onSucceed(Disposable d, List<MedicalInfoEntity> data) {
                        data.removeAll(mKnownList);
                        mUnknownList = data;
                        mTagUnknownLayout.setAdapter(new TagAdapter<MedicalInfoEntity>(mUnknownList) {
                            @Override
                            public View getView(FlowLayout parent, int position, MedicalInfoEntity o) {
                                TextView tv = (TextView) mInflater.inflate(R.layout.user_item_tag, mTagUnknownLayout, false);
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
                        ToastUtils.showLong("拉取数据失败," + msg);
                        LogUtils.i("拉取既往病史数据失败", msg);
                        d.dispose();
                    }
                }
            );
    }

    @OnClick({R.id.iv_back, R.id.btn_update})
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else {
            Set<Integer> selectedList =  mTagUnknownLayout.getSelectedList();
            if (selectedList.size() == 0) {
                ToastUtils.showLong(R.string.user_no_data_update);
                return;
            }

            if (mUnknownList.size() < selectedList.size()) {
                //提示“数据异常”
                return;
            }

            promptDialog.showLoading(getString(R.string.user_upload_data));
            StringBuilder selectedStr = new StringBuilder();
            for (MedicalInfoEntity entity: mKnownList) {
                selectedStr.append(entity.getId()).append(",");
            }
            for (Integer id: selectedList) {
                selectedStr.append(mUnknownList.get(id).getId()).append(",");
            }

            LogUtils.i("选中结果", selectedStr.substring(0, selectedStr.length() - 1));
            update(selectedStr.substring(0, selectedStr.length() - 1));
        }
    }

    private void update(String ills_ids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("ills", ills_ids);
        Injection.getUserDataRepository(getApplication())
            .updateUserInfo(AppConfig.getToken(), params, new UserDataSource.DataCallBack<User>() {

                @Override
                public void onSucceed(Disposable d, User user) {
                    LogUtils.i("onSucceed", user.toString());
                    //提示更新成功
                    //已有病史显示选择内容
                    mKnownList.clear();
                    mKnownList = user.getIlls();
                    mTagKnownLayout.setAdapter(new TagAdapter<MedicalInfoEntity>(mKnownList) {
                        @Override
                        public View getView(FlowLayout parent, int position, MedicalInfoEntity o) {
                            TextView tv = (TextView) mInflater.inflate(R.layout.user_item_tag, mTagKnownLayout, false);
                            tv.setText(o.getName());
                            return tv;
                        }

                        @Override
                        public boolean setSelected(int position, MedicalInfoEntity medicalInfoEntity) {
                            return true;
                        }
                    });
                    mUnknownList.removeAll(mKnownList);
                    mTagUnknownLayout.onChanged();
                    ToastUtils.showLong(R.string.user_update_success);
                    promptDialog.dismissImmediately();

                    if (!d.isDisposed()) {
                        LogUtils.i("isDisposed", true);
                        d.dispose();
                    }
                }

                @Override
                public void onFailed(Disposable d, String msg) {
                    LogUtils.i("onFailed", msg);
                    //提示更新失败
                    ToastUtils.showLong(getString(R.string.user_update_failed) + ","+ msg);
                    promptDialog.dismissImmediately();
                    d.dispose();
                }
            });
    }
}
