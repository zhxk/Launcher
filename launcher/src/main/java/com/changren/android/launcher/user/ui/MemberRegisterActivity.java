package com.changren.android.launcher.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.Injection;
import com.changren.android.launcher.database.UserDataSource;
import com.changren.android.launcher.database.entity.RegisterResult;
import com.changren.android.launcher.database.entity.Token;
import com.changren.android.launcher.user.ui.dialog.PromptDialog;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.RxUtils;
import com.changren.android.launcher.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2019-03-05 16:21
 * Description: 新成员注册
 */
public class MemberRegisterActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView titleTv;

    @BindView(R.id.et_phone)
    EditText mPhoneEt;

    @BindView(R.id.et_password)
    EditText mPasswordEt;

    @BindView(R.id.et_verify)
    EditText mVerifyEt;

    @BindView(R.id.tv_warning)
    TextView mWarningTv;

    @BindView(R.id.btn_verify)
    Button mVerifyBtn;

    private PromptDialog promptDialog;
    protected String mToken;
    protected final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_member_register);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        titleTv.setText(R.string.user_register_new_member);

        mPhoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 11 && !mVerifyBtn.isEnabled()) {
                    mVerifyBtn.setEnabled(!mVerifyBtn.isEnabled());
                } else if (mVerifyBtn.isEnabled()) {
                    mVerifyBtn.setEnabled(!mVerifyBtn.isEnabled());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //创建对象
        promptDialog = new PromptDialog(this);
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(10).loadingDuration(3000);
    }

    protected boolean isRegister = false;

    @OnClick({R.id.iv_back, R.id.btn_verify, R.id.btn_register, R.id.btn_jump})
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.btn_jump) {
            Intent intent = new Intent(MemberRegisterActivity.this, UserProfileActivity.class);
            intent.putExtra(AppConstants.REGISTER_TYPE, AppConstants.MEMBER_REGISTER);
            ActivityUtils.startActivity(intent);
            ActivityUtils.finishActivity(MemberRegisterActivity.this);
        } else if (v.getId() == R.id.btn_verify) {
            mVerifyBtn.setEnabled(false);
            //根据手机号获取服务器验证码
            Injection.getUserDataRepository(getApplication())
                .getVerifyCode(mPhoneEt.getText().toString(), new UserDataSource.DataCallBack<Token>() {
                    @Override
                    public void onSucceed(Disposable d, Token token) {
                        mWarningTv.setVisibility(View.GONE);
                        mToken = token.getAccessToken();
                        LogUtils.i("getVerifyCode accessToken=" + mToken);
                        if (TextUtils.isEmpty(mToken)) {
                            mVerifyBtn.setEnabled(true);
                            mVerifyBtn.setText(getString(R.string.user_regain_verify));
                            return;
                        }

                        //开始验证码读秒(60s)
                        mVerifyBtn.setText(getString(R.string.user_read_seconds, 59));
                        //实现Button显示倒计时60秒
                        mCompositeDisposable.add(RxUtils.countDown(new RxUtils.SuccessCallBack<Long>() {
                            @Override
                            public void onSucceed(Long data) {
                                if (data >= 59) {
                                    mVerifyBtn.setEnabled(true);
                                    mVerifyBtn.setText(getString(R.string.user_regain_verify));
                                    return;
                                }
                                mVerifyBtn.setText(getString(R.string.user_read_seconds, (59 - data)));
                            }
                        }));

                        if (!d.isDisposed()) {
                            d.dispose();
                        }
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        if (d != null) {
                            d.dispose();
                        }

                        ToastUtils.showLong("验证码获取失败，请稍后重试" + msg);
                        mWarningTv.setVisibility(View.VISIBLE);
                        mWarningTv.setText(msg);
                        mVerifyBtn.setEnabled(true);
                        mVerifyBtn.setText(getString(R.string.user_regain_verify));

                    }
                });
        } else if (v.getId() == R.id.btn_register) {
            if (isRegister) {
                return;
            }

            isRegister = true;
            final String phone = mPhoneEt.getText().toString();
            final String pwd = mPasswordEt.getText().toString();
            String verifyCode = mVerifyEt.getText().toString();

            if (TextUtils.isEmpty(phone)) {
                mWarningTv.setVisibility(View.VISIBLE);
                mWarningTv.setText(getString(R.string.user_info_no_empty, mPhoneEt.getHint()));
                isRegister = false;
                return;
            }

            if (TextUtils.isEmpty(pwd)) {
                mWarningTv.setVisibility(View.VISIBLE);
                mWarningTv.setText(getString(R.string.user_info_no_empty, getString(R.string.user_pwd_edit_hint)));
                isRegister = false;
                return;
            }

            if (TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(mToken)) {
                mWarningTv.setVisibility(View.VISIBLE);
                mWarningTv.setText(getString(R.string.user_info_no_empty, mVerifyEt.getHint()));
                isRegister = false;
                return;
            }

            //至少八个字符，至少一个字母和一个数字的规则
            String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,15}$";
            if (!pwd.matches(regex)) {
                mWarningTv.setVisibility(View.VISIBLE);
                mWarningTv.setText(R.string.user_pwd_regex_desc);
                isRegister = false;
                return;
            }

            mWarningTv.setVisibility(View.GONE);

            promptDialog.showLoading(getString(R.string.user_register_during));

            Injection.getUserDataRepository(getApplication())
                .registerMember(phone, verifyCode, mToken, pwd, new UserDataSource.DataCallBack<RegisterResult>() {
                    @Override
                    public void onSucceed(Disposable d, RegisterResult data) {
                        promptDialog.showSuccess(getString(R.string.user_register_success));
                        isRegister = false;

                        if (!d.isDisposed()) {
                            d.dispose();
                        }

                        Intent intent = new Intent(MemberRegisterActivity.this, UserProfileActivity.class);
                        intent.putExtra(AppConstants.UID, data.getMid());
                        intent.putExtra(AppConstants.REGISTER_TYPE, AppConstants.MEMBER_REGISTER);
                        ActivityUtils.startActivity(intent);
                        ActivityUtils.finishActivity(MemberRegisterActivity.this);
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        //校验失败或者注册失败
                        promptDialog.showError(getString(R.string.user_register_failed));
                        mWarningTv.setVisibility(View.VISIBLE);
                        mWarningTv.setText(msg);
                        isRegister = false;

                        if (d != null) {
                            d.dispose();
                        }
                    }
                });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unsubscribe();
    }

    protected void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
