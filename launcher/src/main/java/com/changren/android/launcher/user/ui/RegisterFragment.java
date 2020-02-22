package com.changren.android.launcher.user.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.entity.Token;
import com.changren.android.launcher.database.network.RetrofitClient;
import com.changren.android.launcher.database.network.api.ApiException;
import com.changren.android.launcher.database.network.api.ApiService;
import com.changren.android.launcher.user.ui.dialog.PromptDialog;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.SPUtils;
import com.changren.android.launcher.util.Signature;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: wangsy
 * Create: 2018-10-24 14:25
 * Description: TODO(描述文件做什么)
 */
public class RegisterFragment extends Fragment {

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

    private Context mActivity;
    private PromptDialog promptDialog;
//    protected Disposable disposable;
    protected final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected String mToken;

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
        View view = inflater.inflate(R.layout.user_fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        promptDialog = new PromptDialog(getActivity());
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(10).loadingDuration(3000);
    }

    protected boolean isRegister = false;

    @OnClick({R.id.btn_verify, R.id.btn_register})
    public void onClick(View v) {
        if (v.getId() == R.id.btn_verify) {
            mVerifyBtn.setEnabled(false);
            //根据手机号获取服务器验证码
            mCompositeDisposable.add(
                getApiService().getVerifyCode(mPhoneEt.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Token>() {
                        @Override
                        public void accept(Token token) throws Exception {
//                            LogUtils.i("getVerifyCode="+token.toString());
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
                            mCompositeDisposable.add(
                                //实现Button显示倒计时60秒
                                Observable.interval(1, TimeUnit.SECONDS)
                                    //interval创建的I_Observable监听timer创建T_Observable，T_Observable开始发送数据，I_Observable就不再发送数据。
                                    .takeUntil(Observable.timer(62, TimeUnit.SECONDS))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
//                                            LogUtils.a("interval count==" + aLong);
                                            if (aLong >= 59) {
                                                mVerifyBtn.setEnabled(true);
                                                mVerifyBtn.setText(getString(R.string.user_regain_verify));
                                                return;
                                            }
                                            mVerifyBtn.setText(getString(R.string.user_read_seconds, (59 - aLong)));
                                        }
                                    })
                            );
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.i(throwable.toString(), throwable.getMessage());
                            if (throwable instanceof ApiException) {
                                ApiException exception = (ApiException) throwable;
                                if (exception.isRegistered()) {
                                    mWarningTv.setVisibility(View.VISIBLE);
                                    mWarningTv.setText(R.string.user_phone_number_registered);
                                } else {
                                    mWarningTv.setVisibility(View.VISIBLE);
                                    mWarningTv.setText(throwable.getMessage());
                                }
                            }

                            mVerifyBtn.setEnabled(true);
                            mVerifyBtn.setText(getString(R.string.user_regain_verify));
                        }
                    }));
        } else if (v.getId() == R.id.btn_register) {
            if (isRegister) {
                return;
            }

//            //暂时直接跳转
//            ActivityUtils.startActivity(getActivity(), UserProfileActivity.class);
//            ActivityUtils.finishActivity(getActivity());

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
            //和server校验验证码
            mCompositeDisposable.add(getApiService().checkVerifyCode(phone, verifyCode, mToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .flatMap(new Function<Token, ObservableSource<Token>>() {
                        @Override
                        public ObservableSource<Token> apply(Token token) throws Exception {
                            LogUtils.i("checkVerifyCode accessToken=" + token.getToken());
                            //用户注册
                            String md5_pwd = Signature.getMD5(pwd);
                            return getApiService().register(phone, md5_pwd, token.getAccessToken());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Token>() {
                        @Override
                        public void accept(Token token) throws Exception {
                            LogUtils.i("register token=" + token.getToken());
                            //注册成功,保存Token用于API访问使用，类似UserID的作用
                            promptDialog.showSuccess(getString(R.string.user_register_success));
                            SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_TOKEN, token.getToken());
                            if (getActivity() != null) {
                                ActivityUtils.startActivity(getActivity(), UserProfileActivity.class);
                                ActivityUtils.finishActivity(getActivity());
                            }
                            isRegister = false;
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("accept: error :" + throwable.getMessage());
                            //校验失败或者注册失败
                            promptDialog.showError(getString(R.string.user_register_failed));
                            mWarningTv.setVisibility(View.VISIBLE);
                            mWarningTv.setText(throwable.getMessage());
                            isRegister = false;
                        }
                    })
            );
        }
    }

    private ApiService getApiService() {
        return RetrofitClient.getInstance(mActivity, null).getApiService();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unsubscribe();
    }

    protected void unsubscribe() {
//        if (disposable != null && !disposable.isDisposed()) {
//            disposable.dispose();
//        }
        mCompositeDisposable.clear();
    }
}
