package com.changren.android.launcher.database;

import android.text.TextUtils;

import com.changren.android.launcher.database.entity.DataSource;
import com.changren.android.launcher.database.entity.Empty;
import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.database.entity.HealthScore;
import com.changren.android.launcher.database.entity.HealthUserScore;
import com.changren.android.launcher.database.entity.InformationBean;
import com.changren.android.launcher.database.entity.LoginResult;
import com.changren.android.launcher.database.entity.MedicalInfoEntity;
import com.changren.android.launcher.database.entity.PlanTodayBean;
import com.changren.android.launcher.database.entity.RegisterResult;
import com.changren.android.launcher.database.entity.Token;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.database.network.HttpConfig;
import com.changren.android.launcher.database.network.RetrofitClient;
import com.changren.android.launcher.database.network.api.ApiService;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.ImageUtils;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.SPUtils;
import com.changren.android.launcher.util.Signature;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Author: wangsy
 * Create: 2018-12-13 11:39
 * Description: 网络请求的数据源封装类
 */
public class RemoteDataSource {

    private static RemoteDataSource INSTANCE = null;
    private ApiService apiService;

    public RemoteDataSource() {
        apiService = RetrofitClient.getInstance().getApiService();
    }

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (RemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 不支持文件上传的Form表单上传
     * @param token 请求头
     * @param infoMap  表单参数集合
     * @param scheduler 观察者所在的线程
     * @param observer  观察者对象
     */
    public void setUserInfo(String token, Map<String, String> infoMap, Scheduler scheduler, Observer<User> observer) {
        apiService.setUserInfo(token, infoMap)
            .subscribeOn(Schedulers.io())
            //doOnNext一般用来保存数据
            .doOnNext(new Consumer<User>() {
                @Override
                public void accept(User user) throws Exception {
                    //User保存到数据库
                    LogUtils.i("insertUsers");
                    if (!UserDatabaseManager.getInstance().insertUsers(user)) {
                        throw new IOException("Insert User Info into db failed.");
                    }
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 不支持文件上传的Form表单上传
     * @param token 请求头
     * @param infoMap  表单参数集合
     * @param scheduler 观察者所在的线程
     * @param observer  观察者对象
     */
    public void updateUserInfo(String token, Map<String, String> infoMap, Scheduler scheduler, Observer<User> observer) {
        apiService.updateUserInfo(token, infoMap)
            .subscribeOn(Schedulers.io())
            //doOnNext一般用来保存数据
            .doOnNext(new Consumer<User>() {
                @Override
                public void accept(User user) throws Exception {
                    //User保存到数据库
                    LogUtils.i("insertUsers");
                    if (!UserDatabaseManager.getInstance().insertUsers(user)) {
                        throw new IOException("Insert User Info into db failed.");
                    }
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 支持同时上传文件（头像）的Form表单提交
     * @param token 请求头
     * @param infoMap  表单参数集合
     * @param path  文件所在路径
     * @param scheduler 观察者所在的线程
     * @param observer  观察者对象
     */
    public void updateUserInfo(final String token, final Map<String, RequestBody> infoMap, final String path, //final MultipartBody.Part file,
                            Scheduler scheduler, Observer<User> observer) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                String new_path = ImageUtils.compressImage(path);
                if (TextUtils.isEmpty(new_path)) {
                    emitter.onError(new IOException("compress Image Failed"));
                    emitter.onComplete();
                } else {
                    emitter.onNext(new_path);
                    emitter.onComplete();
                }
                LogUtils.i("compress Image success.");
            }
        }).flatMap(new Function<String, ObservableSource<User>>() {
            @Override
            public ObservableSource<User> apply(String s) throws Exception {
                return apiService.updateUserInfo(infoMap, getAvatarFile(s), token);
            }
        })
//        apiService.setUserInfo(infoMap, file, token)
        .subscribeOn(Schedulers.io())
        //doOnNext一般用来保存数据
        .doOnNext(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                //User保存到数据库
                LogUtils.i("insertUsers");
                if (!UserDatabaseManager.getInstance().insertUsers(user)) {
                    throw new IOException("Insert User Info into db failed.");
                }
            }
        })
        .observeOn(scheduler)
        .subscribe(observer);
    }

    /**
     * 上传图片的接口，暂时无法在OKHttp的拦截器中自动添加“公共参数”
     * @param args  业务参数
     * @return  Map<String, RequestBody>
     */
    public Map<String, RequestBody> getInfoMap(Map<String, String> args) {
        if (args == null) {
            args = new HashMap<>();
        }

        //这里为公共的参数
        //添加签名参数
        args.put("source", "robot");
        args.put("timestamp", System.currentTimeMillis()/1000 + "");

        String prefix = HttpConfig.REQUEST_METHOD_POST + "&" + HttpConfig.API_USER_UPDATE_INFO + "?";
        String sign = Signature.createSignature(Signature.HMAC_KEY, prefix, args);
        args.put("sign", sign);

        Map<String, RequestBody> params = new HashMap<>();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            params.put(entry.getKey(), RequestBody.create(MultipartBody.FORM, entry.getValue()));
        }

        return params;
    }

    /**
     * 将图片封装成可上传的MultipartBody.Part对象
     * @param path  图片所在路径
     * @return  MultipartBody.Part
     */
    public MultipartBody.Part getAvatarFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        //根据路径获取
        File file = new File(path);
        //表单类型
        RequestBody fileBody = RequestBody.create(MultipartBody.FORM, file);
        //添加图片数据，body创建的请求体
        return MultipartBody.Part.createFormData("avatar", file.getName(), fileBody);
    }

    /**
     * 实时获取既往病史列表
     * @param token 请求头
     * @param scheduler 观察者所在的线程
     * @param observer  观察者对象
     */
    public void getAnamnesisList(String token, Scheduler scheduler, Observer<List<MedicalInfoEntity>> observer) {
        apiService.getAnamnesisList(token)
            .subscribeOn(Schedulers.io())
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 创建家庭
     * @param token 请求头
     * @param infoMap  表单参数集合
     * @param scheduler 观察者所在的线程
     * @param observer  观察者对象
     */
    public void createFamily(String token, Map<String, String> infoMap,
                             Scheduler scheduler, Observer<Family> observer) {
        apiService.createFamily(token, infoMap)
            .subscribeOn(Schedulers.io())
            //doOnNext一般用来保存数据
            .doOnNext(new Consumer<Family>() {
                @Override
                public void accept(Family family) throws Exception {
                    //User保存到数据库
                    LogUtils.i("insertFamily", family.toString());
                    SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_LOGIN_USER_ID, family.getUid());
                    //默认家庭创建后 立即登录
                    family.setLogin(true);
                    UserDatabaseManager.getInstance().insertFamily(family);
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 密码登录
     * @param phone 手机号
     * @param pwd   密码
     * @param scheduler 回调线程
     * @param observer  观察者
     */
    public void login(final String phone, final String pwd, Scheduler scheduler, Observer<LoginResult> observer) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                try {
                    String md5_pwd = Signature.getMD5(pwd);
                    emitter.onNext(md5_pwd);
                    emitter.onComplete();
                } catch (NoSuchAlgorithmException e) {
                    emitter.onError(e);
                    emitter.onComplete();
                }
            }
        }).flatMap(new Function<String, ObservableSource<LoginResult>>() {
            @Override
            public ObservableSource<LoginResult> apply(String s) throws Exception {
                return apiService.login(phone, s);
            }
        })
        .subscribeOn(Schedulers.io())
        //doOnNext一般用来保存数据
        .doOnNext(new Consumer<LoginResult>() {
            @Override
            public void accept(LoginResult loginResult) throws Exception {
                //FamilyList保存到数据库
                LogUtils.i("insert LoginResult");

                SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_LOGIN_USER_ID, loginResult.getUser().getUid());
                if (loginResult.getFamilyList().size() > 0) {
                    UserDatabaseManager.getInstance().insertFamilyList(loginResult.getFamilyList());
                }
            }
        })
        .observeOn(scheduler)
        .subscribe(observer);
    }

    /**
     * 根据family_id获取成员列表
     * @param token         访问token
     * @param family_id     登录家庭
     * @param scheduler     回调线程
     * @param observer      观察者
     */
    public void getMemberList(String token, int family_id, Scheduler scheduler, Observer<Family> observer) {
        apiService.getMemberList(token, family_id)
            .subscribeOn(Schedulers.io())
            //doOnNext一般用来保存数据
            .doOnNext(new Consumer<Family>() {
                @Override
                public void accept(Family family) throws Exception {
                    //User保存到数据库
                    LogUtils.i("insertFamily and Member list", family.toString());
                    SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_LOGIN_FAMILY_ID, family.getFid());
                    //默认家庭创建后 立即登录
                    family.setLogin(true);
                    UserDatabaseManager.getInstance().insertFamilyAndMemberList(family);
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 根据family_id获取成员列表
     * @param token         访问token
     * @param scheduler     回调线程
     * @param observer      观察者
     */
    public void logout(String token, final int family_id, final boolean exit, Scheduler scheduler, Observer<Empty> observer) {
        apiService.logout(token)
            .subscribeOn(Schedulers.io())
            //doOnNext一般用来保存数据
            .doOnNext(new Consumer<Empty>() {
                @Override
                public void accept(Empty result) throws Exception {
                    UserDatabaseManager.getInstance().updateLoginStatus(family_id, exit);
                    AppConfig.logout();
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }
    /**
     * 获取健康指数
     *
     * @param token
     * @param scheduler
     * @param observer
     */
    public void getHealthScore(String token, Scheduler scheduler, Observer<HealthScore> observer) {
        apiService.getHealthScore(token)
                .subscribeOn(Schedulers.io())
                .observeOn(scheduler)
                .subscribe(observer);
    }

    /**
     * 统一获取当前show_user用户的"健康计划今日计划内容"，"资讯信息","身体检查数据"
     * @param token
     * @param pageSize
     * @param scheduler
     * @param observer
     */
    public void getLauncherBannerData(String token, int pageSize, Scheduler scheduler, Observer<List<DataSource>> observer) {
        Observable<HealthUserScore> scoreObservable = apiService.getHealthUserScore(token);
        Observable<List<PlanTodayBean.PlanBean>> planObservable = apiService.getUserSettings(token);
        Observable<InformationBean> infoObservable = apiService.getArticleList(token, pageSize);
        //将多个 Observable 的数据结合为一个数据源，从而实现多个接口数据共同更新UI
        Observable.zip(scoreObservable, planObservable, infoObservable,
                new Function3<HealthUserScore, List<PlanTodayBean.PlanBean>, InformationBean, List<DataSource>>() {
                    @Override
                    public List<DataSource> apply(HealthUserScore healthUserScore,
                                                  List<PlanTodayBean.PlanBean> planBeans,
                                                  InformationBean informationBean) throws Exception {
                        List<DataSource> dataSources = new ArrayList<>(3);
                        LogUtils.i("健康检测数据=="+ healthUserScore.toString());
                        LogUtils.i("健康计划=="+ planBeans.toString());
                        LogUtils.i("健康资讯=="+ informationBean.toString());
                        dataSources.add(healthUserScore);
                        dataSources.add(new PlanTodayBean(planBeans));
                        dataSources.add(informationBean);
                        return dataSources;
                    }
                })
            .subscribeOn(Schedulers.io())
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 切换家庭成员
     * @param token
     * @param family_id
     * @param user_id
     * @param scheduler
     * @param observer
     */
    public void switchMember(String token, final int family_id, int user_id, Scheduler scheduler, Observer<User> observer) {
        apiService.switchMember(token, family_id, user_id)
            .subscribeOn(Schedulers.io())
            //doOnNext一般用来保存数据
            .doOnNext(new Consumer<User>() {
                @Override
                public void accept(User user) throws Exception {
                    SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_SHOW_USER_ID, user.getUid());
                    user.setFid(family_id);
                    UserDatabaseManager.getInstance().insertUsers(user);
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    public void switchMemberAndGetMemberList(final String token, final int family_id, final int user_id,
                                             Scheduler scheduler, Observer<Family> observer) {
        apiService.switchMember(token, family_id, user_id)
            .subscribeOn(Schedulers.io())
            .doOnNext(new Consumer<User>() {
                @Override
                public void accept(User user) throws Exception {
                    SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_SHOW_USER_ID, user.getUid());
                }
            })
            .observeOn(Schedulers.io())
            .flatMap(new Function<User, ObservableSource<Family>>() {
                @Override
                public ObservableSource<Family> apply(User user) throws Exception {
                    if (user.getUid() == user_id) {
                        return apiService.getMemberList(token, family_id);
                    }
                    return null;
                }
            })
            .doOnNext(new Consumer<Family>() {
                @Override
                public void accept(Family family) throws Exception {
                    //User保存到数据库
                    LogUtils.i("insertFamily and Member list", family.toString());
                    SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_LOGIN_FAMILY_ID, family.getFid());
                    //默认家庭创建后 立即登录
                    family.setLogin(true);
                    UserDatabaseManager.getInstance().insertFamilyAndMemberList(family);
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 获取验证码
     * @param phone
     * @param scheduler
     * @param observer
     */
    public void getVerifyCode(String token, String phone, String action, Scheduler scheduler, Observer<Token> observer) {
        apiService.getVerifyCodeWithAction(token, phone, action)
            .subscribeOn(Schedulers.io())
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 绑定或者更改手机号码
     * @param token
     * @param phone
     * @param accessToken
     * @param code
     * @param pwd
     * @param scheduler
     * @param observer
     */
    public void bindPhone(final String token, final int family_id, String phone, String accessToken,
                          String code, final String pwd, Scheduler scheduler, Observer<User> observer) {
        final Map<String, String> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("phone", phone);
        params.put("code", code);

        Observable<User> observable;
        if (!TextUtils.isEmpty(pwd)) {
            observable = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> emitter) {
                    try {
                        String md5_pwd = Signature.getMD5(pwd);
                        emitter.onNext(md5_pwd);
                        emitter.onComplete();
                    } catch (NoSuchAlgorithmException e) {
                        emitter.onError(e);
                        emitter.onComplete();
                    }
                }
            })
            .flatMap(new Function<String, ObservableSource<User>>() {
                @Override
                public ObservableSource<User> apply(String s) throws Exception {
                    params.put("pwd", s);
                    return apiService.bindPhone(token, params);
                }
            });
        } else {
            observable = apiService.bindPhone(token, params);
        }

        observable.subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            //doOnNext一般用来保存数据
            .doOnNext(new Consumer<User>() {
                @Override
                public void accept(User user) throws Exception {
                    user.setFid(family_id);
                    UserDatabaseManager.getInstance().insertUsers(user);
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 获取家庭列表
     * @param token
     * @param login_user_id
     * @param scheduler
     * @param observer
     */
    public void getFamilyList(final String token, final int login_user_id, Scheduler scheduler, Observer<List<Family>> observer) {
        Observable<List<Family>> observable;
        if (login_user_id == 0) {
            observable = apiService.getLoginUid(token)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<User, ObservableSource<List<Family>>>() {
                    @Override
                    public ObservableSource<List<Family>> apply(User user) throws Exception {
                        LogUtils.i("记录Uid", user.getUid());
                        if (user.getUid() != 0) {
                            SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_LOGIN_USER_ID, user.getUid());
                            return apiService.getFamilyList(token, user.getUid());
                        }
                        return null;
                    }
                });
        } else {
            observable = apiService.getFamilyList(token, login_user_id);
        }

        observable.subscribeOn(Schedulers.io())
            //doOnNext一般用来保存数据
            .doOnNext(new Consumer<List<Family>>() {
                @Override
                public void accept(List<Family> familyList) throws Exception {
                    //FamilyList保存到数据库
                    LogUtils.i("家庭列表size",familyList.size());
                    for (Family family: familyList) {
                        if (family.getFid() == AppConfig.getLoginFamilyId()) {
                            family.setLogin(true);
                            break;
                        }
                    }
                    UserDatabaseManager.getInstance().insertFamilyList(familyList);
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 注册新成员时，提交手机号，密码及验证码，但是后台server不会自动加入家庭
     */
    public void registerMember(final String loginToken, final String phone, String accessToken, String code,
                               final String pwd, Scheduler scheduler, Observer<RegisterResult> observer) {
        apiService.checkVerifyCode(phone, code, accessToken)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap(new Function<Token, ObservableSource<RegisterResult>>() {
                @Override
                public ObservableSource<RegisterResult> apply(Token token) throws Exception {
                    LogUtils.i("checkVerifyCode accessToken=" + token.getToken());
                    //用户注册
                    String md5_pwd = Signature.getMD5(pwd);
                    Map<String, String> params = new HashMap<>();
                    params.put("phone", phone);
                    params.put("pwd", md5_pwd);
                    params.put("accessToken", token.getAccessToken());
                    return apiService.registerMember(loginToken, params)
                                .observeOn(Schedulers.io());
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 设置家庭新成员基本信息，后台server会自动加入家庭，同时“后台会自动将Token关联到新注册的成员上”
     * （不支持文件上传的Form表单上传）
     */
    public void setMemberInfo(String token, final int family_id, Map<String, String> infoMap, Scheduler scheduler, Observer<User> observer) {
        apiService.setMemberInfo(token, infoMap)
            .subscribeOn(Schedulers.io())
            .doOnNext(new Consumer<User>() {
                @Override
                public void accept(User user) throws Exception {
                    LogUtils.i("insertUsers", user.toString());
                    //后台会自动将Token关联到新注册的成员上
                    SPUtils.getInstance(SPUtils.SP_USER).put(SPUtils.SP_SHOW_USER_ID, user.getUid());
                    //User保存到数据库
                    user.setFid(family_id);
                    if (!UserDatabaseManager.getInstance().insertUsers(user)) {
                        throw new IOException("Insert User Info into db failed.");
                    }
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 修改家庭新成员信息（不支持文件上传的Form表单上传）
     * @param token 请求头
     * @param infoMap  表单参数集合
     * @param scheduler 观察者所在的线程
     * @param observer  观察者对象
     */
    public void updateMemberInfo(String token, Map<String, String> infoMap, Scheduler scheduler, Observer<User> observer) {
        apiService.updateMemberInfo(token, infoMap)
            .subscribeOn(Schedulers.io())
            .doOnNext(new Consumer<User>() {
                @Override
                public void accept(User user) throws Exception {
                    LogUtils.i("insertUsers", user.toString());
                    if (!UserDatabaseManager.getInstance().insertUsers(user)) {
                        throw new IOException("Insert User Info into db failed.");
                    }
                }
            })
            .observeOn(scheduler)
            .subscribe(observer);
    }

    /**
     * 修改家庭新成员信息（支持同时上传文件（头像）的Form表单提交）
     * @param token 请求头
     * @param infoMap  表单参数集合
     * @param path  文件所在路径
     * @param scheduler 观察者所在的线程
     * @param observer  观察者对象
     */
    public void updateMemberInfo(final String token, final Map<String, RequestBody> infoMap, final String path, //final MultipartBody.Part file,
                               Scheduler scheduler, Observer<User> observer) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                String new_path = ImageUtils.compressImage(path);
                if (TextUtils.isEmpty(new_path)) {
                    emitter.onError(new IOException("compress Image Failed"));
                    emitter.onComplete();
                } else {
                    emitter.onNext(new_path);
                    emitter.onComplete();
                }
                LogUtils.i("compress Image success.");
            }
        }).flatMap(new Function<String, ObservableSource<User>>() {
            @Override
            public ObservableSource<User> apply(String s) throws Exception {
                return apiService.updateMemberInfo(infoMap, getAvatarFile(s), token);
            }
        })
        .subscribeOn(Schedulers.io())
        .doOnNext(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                LogUtils.i("insertUsers", user.toString());
                if (!UserDatabaseManager.getInstance().insertUsers(user)) {
                    throw new IOException("Insert User Info into db failed.");
                }
            }
        })
        .observeOn(scheduler)
        .subscribe(observer);
    }
}
