package com.changren.android.launcher.user.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.Injection;
import com.changren.android.launcher.database.UserDataSource;
import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.database.entity.LoginResult;
import com.changren.android.launcher.user.ui.dialog.PromptDialog;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2018-10-24 14:25
 * Description: 用户登录
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.et_phone)
    EditText mPhoneEt;

    @BindView(R.id.et_password)
    EditText mPasswordEt;

    @BindView(R.id.tv_warning)
    TextView mWarningTv;

    private Context mActivity;
    private boolean isLoading = false;
    private PromptDialog promptDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment_login, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //创建对象
        promptDialog = new PromptDialog(getActivity());
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(10).loadingDuration(3000);
    }

    @OnClick(R.id.btn_login)
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            if (isLoading) {
                return;
            }

            isLoading = true;
            final String phone = mPhoneEt.getText().toString();
            final String pwd = mPasswordEt.getText().toString();

            if (TextUtils.isEmpty(phone)) {
                mWarningTv.setVisibility(View.VISIBLE);
                mWarningTv.setText(getString(R.string.user_info_no_empty, mPhoneEt.getHint()));
                isLoading = false;
                return;
            }

            if (TextUtils.isEmpty(pwd)) {
                mWarningTv.setVisibility(View.VISIBLE);
                mWarningTv.setText(getString(R.string.user_info_no_empty, getString(R.string.user_pwd_edit_hint)));
                isLoading = false;
                return;
            }

            mWarningTv.setVisibility(View.GONE);

            promptDialog.showLoading(getString(R.string.user_login_during));
            Injection.getUserDataRepository((Application) mActivity.getApplicationContext())
                .login(phone, pwd, new UserDataSource.DataCallBack<LoginResult>() {
                    @Override
                    public void onSucceed(Disposable d, LoginResult result) {
                        mWarningTv.setVisibility(View.GONE);
                        promptDialog.showSuccess(getString(R.string.user_login_success));
                        doResult(result);
                        isLoading = false;
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        LogUtils.i("登录失败", msg);
                        mWarningTv.setVisibility(View.VISIBLE);
                        mWarningTv.setText(msg);
                        promptDialog.showError(getString(R.string.user_login_failed));
                        isLoading = false;
                    }
                });
        }
    }

    /**
     * 登录成功
     * 根据family_list的家庭列表数，确定跳转页面
     * @param result 登录后返回的结果
     */
    private void doResult(LoginResult result) {
        if (getActivity() == null) {
            return;
        }

        //注册成功,保存Token用于API访问使用，类似UserID的作用
        SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_TOKEN, result.getToken());
        //注册状态：1待提交基本信息，2待创建/加入家庭，3已完成注册
        int status = result.getUser().getStatus();
        if (status == 1) {
            //填写基本数据，暂不往下传递数据
            ActivityUtils.startActivity(getActivity(), UserProfileActivity.class);
            ActivityUtils.finishActivity(getActivity());
        } else if (status == 2) {
            //创建家庭，暂不往下传递数据
            ActivityUtils.startActivity(getActivity(), CreateFamilyActivity.class);
            ActivityUtils.finishActivity(getActivity());
        } else {
            //登录成功，跳转家庭详细页面
            LogUtils.i("登录成功");
            int size = result.getFamilyList().size();
            if (size == 0) {
                //创建家庭，暂不往下传递数据
                ActivityUtils.startActivity(getActivity(), CreateFamilyActivity.class);
                ActivityUtils.finishActivity(getActivity());
            } else if (size == 1) {
                //直接进入家庭成员列表，传递fid
                Bundle bundle = new Bundle();
                bundle.putInt(AppConstants.FID, result.getFamilyList().get(0).getFid());
                ActivityUtils.startActivity(bundle, getActivity(), MemberListActivity.class);
                ActivityUtils.finishActivity(getActivity());
            } else {
                //多个家庭，进入选择要登录的家庭
                StringBuilder ids = new StringBuilder();
                for (Family family: result.getFamilyList()) {
                    ids.append(family.getFid()).append(",");
                }
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.FID_LIST, ids.substring(0, ids.length() - 1));
                LogUtils.i(AppConstants.FID_LIST, ids.substring(0, ids.length() - 1));
                ActivityUtils.startActivity(bundle, getActivity(), FamilyListActivity.class);
                ActivityUtils.finishActivity(getActivity());
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        unsubscribe();
    }

//    protected void unsubscribe() {
//        mCompositeDisposable.clear();
//    }
}
