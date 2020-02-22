package com.changren.android.launcher.database;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.entity.DataSource;
import com.changren.android.launcher.database.entity.Empty;
import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.database.entity.HealthScore;
import com.changren.android.launcher.database.entity.LoginResult;
import com.changren.android.launcher.database.entity.MedicalInfoEntity;
import com.changren.android.launcher.database.entity.RegisterResult;
import com.changren.android.launcher.database.entity.Token;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.database.network.HttpConfig;
import com.changren.android.launcher.database.network.api.ApiException;
import com.changren.android.launcher.ui.Launcher;
import com.changren.android.launcher.user.ui.LoginActivity;
import com.changren.android.launcher.util.ActivityUtils;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.SPUtils;
import com.changren.android.launcher.util.ToastUtils;
import com.changren.android.upgrade.util.DeviceUtils;

import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.changren.android.launcher.util.ActivityUtils.startActivity;


/**
 * Author: wangsy
 * Create: 2018-12-13 11:35
 * Description: 数据管理调配资源的仓库（是从网络获取还是本地获取）
 */
public class UserDataRepository {

    private static UserDataRepository INSTANCE = null;

    private final RemoteDataSource mRemoteDataSource;

    private final LocalDataSource mLocalDataSource;

    private static Application sApplication = null;

    private UserDataRepository(@NonNull RemoteDataSource remoteDataSource,
                           @NonNull LocalDataSource localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }

    static UserDataRepository getInstance(@NonNull RemoteDataSource remoteDataSource,
            @NonNull LocalDataSource localDataSource, Application application) {
        if (INSTANCE == null) {
            synchronized (UserDataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserDataRepository(remoteDataSource, localDataSource);
                    sApplication = application;
                }
            }
        }
        return INSTANCE;
    }

    private <T> Observer<T> createObserver(final UserDataSource.DataCallBack<T> callBack) {
        return new Observer<T>() {
            private Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(T t) {
                callBack.onSucceed(disposable, t);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {//服务器返回错误
                    if(((ApiException) e).getErrorCode()== HttpConfig.TOKEN_INVALID) {
                        callBack.onFailed(disposable, String.valueOf(HttpConfig.TOKEN_INVALID));
                        //token失效跳转到登录页面
//                        SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_TOKEN, "");
//                        SPUtils.getInstance(SPUtils.SP_USER).clear();
//                        ToastUtils.showLong("登录失效，请重新登录!");
//                        Intent intent = new Intent(sApplication, LoginActivity.class);
//                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                        sApplication.getApplicationContext().startActivity(intent);
                    }else{
                        callBack.onFailed(disposable, e.getMessage());
                    }
                } else {
                    callBack.onFailed(disposable, sApplication.getString(R.string.program_error) + e.getMessage());
                }
            }

            @Override
            public void onComplete() {
                LogUtils.i("完成");
            }
        };
    }

    public void setUserInfo(String token, Map<String, String> infoMap, final UserDataSource.DataCallBack<User> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.setUserInfo(token, infoMap, AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void updateUserInfo(String token, Map<String, String> infoMap, final UserDataSource.DataCallBack<User> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.updateUserInfo(token, infoMap, AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void updateUserInfo(String token, Map<String, String> infoMap, String avatar_path,
                            final UserDataSource.DataCallBack<User> callBack) {
        // /storage/emulated/0/bluetooth/IMG_20181113_171649.jpg
        if (TextUtils.isEmpty(avatar_path)) {
            updateUserInfo(token, infoMap, callBack);
            return;
        }

        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.updateUserInfo(token, mRemoteDataSource.getInfoMap(infoMap),
                avatar_path, AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void getAnamnesisList(String token, final UserDataSource.DataCallBack<List<MedicalInfoEntity>> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.getAnamnesisList(token, AndroidSchedulers.mainThread(),
                createObserver(callBack));
    }

    public void createFamily(String token, Map<String, String> infoMap,
                             final UserDataSource.DataCallBack<Family> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.createFamily(token, infoMap,
            AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void login(String phone, String pwd, final UserDataSource.DataCallBack<LoginResult> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.login(phone, pwd, AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void getFamilyList(String[] family_ids, final UserDataSource.DataCallBack<List<Family>> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mLocalDataSource.getFamilyList(family_ids, AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void getMemberList(int family_id, final UserDataSource.DataCallBack<Family> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        String token = AppConfig.getToken();
        mRemoteDataSource.getMemberList(token, family_id, AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    /**
     * 1.“刚登录”或者“同一账号用户，切换家庭”，需要调用switchMember接口，然后再获取家庭成员列表
     * 2.“同一家庭，家庭内成员切换成show_user”,直接获取家庭成员列表
     * @param family_id
     * @param callBack
     */
    public void switchMemberAndGetMemberList(final int family_id, boolean isRestart,
                                             final UserDataSource.DataCallBack<Family> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        String token = AppConfig.getToken();
        int user_id = AppConfig.getLoginUserId();
        if (isRestart) {
            mRemoteDataSource.getMemberList(token, family_id, AndroidSchedulers.mainThread(), createObserver(callBack));
        } else {
            mRemoteDataSource.switchMemberAndGetMemberList(token, family_id, user_id,
                    AndroidSchedulers.mainThread(), createObserver(callBack));
        }
    }

    public void logout(int family_id, boolean exit, final UserDataSource.DataCallBack<Empty> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.logout(AppConfig.getToken(), family_id, exit,
                AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void switchMember(int family_id, int user_id, final UserDataSource.DataCallBack<User> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.switchMember(AppConfig.getToken(), family_id, user_id,
                AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void getVerifyCode(String phone, final UserDataSource.DataCallBack<Token> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.getVerifyCode(AppConfig.getToken(), phone, "register",
                AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void getVerifyCode(String phone, String action, final UserDataSource.DataCallBack<Token> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.getVerifyCode(AppConfig.getToken(), phone, action,
                AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void bindPhone(String phone, String accessToken, String code, String pwd,
                          final UserDataSource.DataCallBack<User> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.bindPhone(AppConfig.getToken(), AppConfig.getLoginFamilyId(), phone,
                accessToken, code, pwd, AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void getFamilyList(final UserDataSource.DataCallBack<List<Family>> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.getFamilyList(AppConfig.getToken(), AppConfig.getLoginUserId(),
                AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void registerMember(String phone, String code, String accessToken, String pwd,
                               final UserDataSource.DataCallBack<RegisterResult> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.registerMember(AppConfig.getToken(), phone, accessToken, code, pwd,
                AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void setMemberInfo(Map<String, String> infoMap, final UserDataSource.DataCallBack<User> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.setMemberInfo(AppConfig.getToken(), AppConfig.getLoginFamilyId(), infoMap,
                AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void updateMemberInfo(String token, Map<String, String> infoMap, final UserDataSource.DataCallBack<User> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.updateMemberInfo(token, infoMap, AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void updateMemberInfo(String token, Map<String, String> infoMap, String avatar_path,
                               final UserDataSource.DataCallBack<User> callBack) {
        // /storage/emulated/0/bluetooth/IMG_20181113_171649.jpg
        if (TextUtils.isEmpty(avatar_path)) {
            updateMemberInfo(token, infoMap, callBack);
            return;
        }

        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.updateMemberInfo(token, mRemoteDataSource.getInfoMap(infoMap),
                avatar_path, AndroidSchedulers.mainThread(), createObserver(callBack));
    }

    public void getLauncherBannerData(int pageSize, UserDataSource.DataCallBack<List<DataSource>> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.getLauncherBannerData(AppConfig.getToken(), pageSize,
                AndroidSchedulers.mainThread(), createObserver(callBack));

    }

    public void getShowUser(UserDataSource.DataCallBack<User> callBack) {
        if (AppConfig.isLogin()) {
            mLocalDataSource.getShowUserById(AppConfig.getLoginFamilyId(), AppConfig.getShowUserId(),
                    AndroidSchedulers.mainThread(), createObserver(callBack));
        } else {
            callBack.onFailed(null, AppConstants.RESTART_LOGIN);
        }

    }

    public void getHealthScore(final UserDataSource.DataCallBack<HealthScore> callBack) {
        if (!DeviceUtils.checkNetwork(sApplication)) {
            callBack.onFailed(null, sApplication.getString(R.string.no_network));
            return;
        }

        mRemoteDataSource.getHealthScore(AppConfig.getToken(), AndroidSchedulers.mainThread(), createObserver(callBack));
    }

//    public void getHealthUserScore(final UserDataSource.DataCallBack<HealthUserScore> callBack) {
//        if (!DeviceUtils.checkNetwork(sApplication)) {
//            callBack.onFailed(null, sApplication.getString(R.string.no_network));
//            return;
//        }
//
//        mRemoteDataSource.getHealthUserScore(AppConfig.getToken(),
//                AndroidSchedulers.mainThread(), createObserver(callBack));
//    }
//    //add for plan line by shibo.zheng 20190222 start
//
//    /**
//     * 获取今日计划接口回调
//     * @param token
//     * @param callBack
//     */
//
//    public void getUserSettings(String token, final UserDataSource.DataCallBack<List<PlanTodayBean.PlanBean>> callBack){
//        if (!DeviceUtils.checkNetwork(sApplication)) {
//            callBack.onFailed(null, sApplication.getString(R.string.no_network));
//            return;
//        }
//
//        mRemoteDataSource.getUserSettings(token, AndroidSchedulers.mainThread(), createObserver(callBack));
//    }
//
//    /**
//     * 获取资讯信息
//     * @param pageSize
//     * @param callBack
//     */
//    public void getArticleList(String token, int pageSize, final UserDataSource.DataCallBack<InformationBean> callBack){
//        if (!DeviceUtils.checkNetwork(sApplication)) {
//            callBack.onFailed(null, sApplication.getString(R.string.no_network));
//            return;
//        }
//
//        mRemoteDataSource.getArticleList(token, pageSize, AndroidSchedulers.mainThread(), createObserver(callBack));
//    }
//    //add for plan line by shibo.zheng 20190222 end
}
