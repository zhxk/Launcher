package com.changren.android.launcher.user.ui;

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
import com.changren.android.launcher.database.entity.Token;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.user.ui.dialog.PromptDialog;
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
 * Create: 2019-02-25 13:51
 * Description: 绑定或者更改手机号码
 */
public class BindingPhoneActivity extends AppCompatActivity {

    public static final String UPDATE_PHONE = "UPDATE_PHONE";

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

    @BindView(R.id.btn_bind)
    Button mBindBtn;

    protected String mToken;
    protected final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private PromptDialog promptDialog;
    private boolean isUpdate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_member_unbind_phone);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        isUpdate = getIntent().getBooleanExtra(UPDATE_PHONE, false);
        if (isUpdate) {
            mPasswordEt.setVisibility(View.GONE);
            mBindBtn.setText(R.string.user_update);
            titleTv.setText(R.string.user_info_phone_update);
        } else {
            mPasswordEt.setVisibility(View.VISIBLE);
            mBindBtn.setText(R.string.user_bind);
            titleTv.setText(R.string.user_info_phone_bind);
        }

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

    protected boolean isLoading = false;

    @OnClick({R.id.iv_back, R.id.btn_verify, R.id.btn_bind})
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.btn_verify) {
            mVerifyBtn.setEnabled(false);
            Injection.getUserDataRepository(getApplication())
                .getVerifyCode(mPhoneEt.getText().toString(), "bindphone", new UserDataSource.DataCallBack<Token>() {
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
                        ToastUtils.showLong("验证码获取失败，请稍后重试" + msg);
                        mWarningTv.setVisibility(View.VISIBLE);
                        mWarningTv.setText(msg);
                        mVerifyBtn.setEnabled(true);
                        mVerifyBtn.setText(getString(R.string.user_regain_verify));
                        d.dispose();
                    }
                });
        } else if (v.getId() == R.id.btn_bind) {
            if (isLoading) {
                return;
            }

            isLoading = true;
            final String phone = mPhoneEt.getText().toString();
            final String pwd = mPasswordEt.getText().toString();
            String verifyCode = mVerifyEt.getText().toString();

            if (TextUtils.isEmpty(phone)) {
                mWarningTv.setVisibility(View.VISIBLE);
                mWarningTv.setText(getString(R.string.user_info_no_empty, mPhoneEt.getHint()));
                isLoading = false;
                return;
            }

            if (!isUpdate && TextUtils.isEmpty(pwd)) {
                mWarningTv.setVisibility(View.VISIBLE);
                mWarningTv.setText(getString(R.string.user_info_no_empty, getString(R.string.user_pwd_edit_hint)));
                isLoading = false;
                return;
            }

            if (TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(mToken)) {
                mWarningTv.setVisibility(View.VISIBLE);
                mWarningTv.setText(getString(R.string.user_info_no_empty, mVerifyEt.getHint()));
                isLoading = false;
                return;
            }

            if (!isUpdate) {
                //至少八个字符，至少一个字母和一个数字的规则
                String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,15}$";
                if (!pwd.matches(regex)) {
                    mWarningTv.setVisibility(View.VISIBLE);
                    mWarningTv.setText(R.string.user_pwd_regex_desc);
                    isLoading = false;
                    return;
                }
            }

            mWarningTv.setVisibility(View.GONE);

            bindOrUpdate(phone, mToken, verifyCode, pwd);
        }
    }

    private void bindOrUpdate(String phone, String accessToken, String code, final String pwd) {
        promptDialog.showLoading(getString(R.string.user_wait_during));
        Injection.getUserDataRepository(getApplication())
            .bindPhone(phone, accessToken, code, pwd, new UserDataSource.DataCallBack<User>() {
                @Override
                public void onSucceed(Disposable d, User data) {
                    promptDialog.dismissImmediately();
                    if (isUpdate) {
                        ToastUtils.showLong(R.string.user_update_success);
                    } else {
                        ToastUtils.showLong(R.string.user_bind_success);
                    }

                    if (!d.isDisposed()) {
                        d.dispose();
                    }

                    finish();
                }

                @Override
                public void onFailed(Disposable d, String msg) {
                    promptDialog.dismissImmediately();
                    if (isUpdate) {
                        ToastUtils.showLong(R.string.user_update_failed);
                    } else {
                        ToastUtils.showLong(R.string.user_bind_failed);
                    }
                    d.dispose();
                }
            });
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
