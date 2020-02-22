package com.changren.android.launcher.user.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.Injection;
import com.changren.android.launcher.database.UserDataSource;
import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.user.ui.dialog.LoadingDialogFragment;
import com.changren.android.launcher.user.ui.widget.FixedEditText;
import com.changren.android.launcher.user.ui.widget.flowlayout.FlowLayout;
import com.changren.android.launcher.user.ui.widget.flowlayout.TagAdapter;
import com.changren.android.launcher.user.ui.widget.flowlayout.TagFlowLayout;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2018-12-05 13:06
 * Description: 创建家庭并加入
 */
public class CreateFamilyActivity extends AppCompatActivity implements LoadingDialogFragment.OnLoadingListener {

    @BindView(R.id.et_family_name)
    FixedEditText mFamilyNameEt;

    @BindView(R.id.tag_example_family_name)
    TagFlowLayout mTagFamilyNameLayout;

    private LayoutInflater mInflater;
    private int family_id;
    String[] familyNameList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_create_family);

        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.user_activity_create_family);

        ButterKnife.bind(this);
        initView();
    }

    private void init() {
        familyNameList = getResources().getStringArray(R.array.example_family_name);
//        example_name[0] = String.format(example_name[0], "可以替换创建者的名字--xxx的家庭");
        mInflater = LayoutInflater.from(this);

        initView();
    }

    private void initView() {
        mFamilyNameEt.setFixedText(getString(R.string.user_family_name), getResources().getColor(R.color.default_text_color));
        mTagFamilyNameLayout.setAdapter(new TagAdapter<String>(familyNameList) {
            @Override
            public View getView(FlowLayout parent, int position, String str) {
                TextView tv = (TextView) mInflater.inflate(R.layout.user_item_tag, mTagFamilyNameLayout, false);
                tv.setText(str);
                return tv;
            }
        });

        mTagFamilyNameLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
//                LogUtils.i("onSelected", selectPosSet.size());
                if (selectPosSet.size() != 0) {
                    //此处为单选，所以只取第一个元素
                    mFamilyNameEt.setText(familyNameList[selectPosSet.iterator().next()]);
                }
            }
        });
    }

    protected LoadingDialogFragment loadingDialog;
    private Map<String, String> params;

    @OnClick({R.id.iv_back, R.id.btn_next})
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.btn_next) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put("fname", mFamilyNameEt.getText().toString());

            if (loadingDialog == null) {
                LogUtils.i("loadingDialog Created");
                loadingDialog = LoadingDialogFragment.newInstance();
            }
            loadingDialog.show(getFragmentManager(), "loading_dialog");
        }
    }

    private void submitInfo() {
        if (params == null) {
            return;
        }
        Injection.getUserDataRepository(getApplication())
            .createFamily(AppConfig.getToken(), params,
                new UserDataSource.DataCallBack<Family>() {

                    @Override
                    public void onSucceed(Disposable d, Family family) {
                        LogUtils.i("onSucceed", family.toString());
                        family_id = family.getFid();
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
        //直接进入家庭成员列表
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.FID, family_id);
        ActivityUtils.startActivity(bundle, CreateFamilyActivity.this, MemberListActivity.class);
        ActivityUtils.finishActivity(CreateFamilyActivity.this);
    }

    @Override
    public void cancel() {
        //取消上传的网络请求
        LogUtils.i("Dialog cancel 被调用");
    }
}
