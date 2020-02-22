package com.changren.android.launcher.database.network.api;

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
import com.changren.android.upgrade.entity.VersionConfig;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Author: wangsy
 * Create: 2018-11-23 14:55
 * Description: 网络请求的Retrofit接口
 */
public interface ApiService {

    /**
     * 获取短信验证码 POST
     * @param phone 手机号码
     * @return
     */
    @POST(HttpConfig.API_SMS_SEND)
    @FormUrlEncoded
    Observable<Token> getVerifyCode(@Field("phone") String phone);

    /**
     * 获取短信验证码 POST
     * @param phone 手机号码
     * @return
     */
    @POST(HttpConfig.API_SMS_SEND)
    @FormUrlEncoded
    Observable<Token> getVerifyCodeWithAction(@Header(HttpConfig.TOKEN) String token, @Field("phone") String phone, @Field("action") String action);

    /**
     * 获取短信验证码 GET
     * @param phone 手机号码
     * @return
     */
    @GET(HttpConfig.API_SMS_SEND)
    Observable<ResponseBody> getVerifyCodeWithAction(@Query("phone") String phone, @Query("action") String action);

    /**
     * 验证码校验
     * @param phone 手机号
     * @param code  验证码
     * @param accessToken  验证的Token
     * @return
     */
    @POST(HttpConfig.API_SMS_CHECK)
    @FormUrlEncoded
    Observable<Token> checkVerifyCode(@Field("phone") String phone, @Field("code") String code,
                                      @Field("accessToken") String accessToken);

    /**
     * 手机号注册
     * @param phone 手机号
     * @param pwd   MD5加密的密码
     * @param accessToken  验证码校验通过后的Token
     * @return
     */
    @POST(HttpConfig.API_USER_REGISTER)
    @FormUrlEncoded
    Observable<Token> register(@Field("phone") String phone, @Field("pwd") String pwd,
                               @Field("accessToken") String accessToken);

    /**
     * 手机号+密码 登录
     * @param phone 手机号
     * @param pwd   MD5加密密码
     * @return
     */
    @POST(HttpConfig.API_USER_LOGIN)
    @FormUrlEncoded
    Observable<LoginResult> login(@Field("phone") String phone, @Field("pwd") String pwd);

    /**
     * 获取既往病史列表
     * @param token 登录或者注册成功后，返回的token
     * @return
     */
    @GET(HttpConfig.API_DISEASE_LIST)
    Observable<List<MedicalInfoEntity>> getAnamnesisList(@Header(HttpConfig.TOKEN) String token);

    /**
     * token	string	必须		登录令牌(http请求header头信息)
     * timestamp	int	必须		当前时间戳
     * source	int	必须		来源（robot：机器人 app：手机APP）
     * username	string	必须	null	姓名
     * nickname	string	必须	null	昵称
     * sex	int	必须	1	性别：1男,2女
     * stature	int	必须	0	身高
     * weight	float	必须	0	体重
     * birth	date	必须	1970-01-01	出生日期：YYYY-MM-DD
     * avatar	file	可选	null	头像:无需参与签名校验，允许的类型为jpg/jpeg、png、bmp、gif，大小不超过2M
     * default_avatar	string	可选	null	默认头像URL地址
     * ills	string	可选	null	病史ID列表字符串:1,3,4
     */
    @POST(HttpConfig.API_USER_SET_INFO)
    @FormUrlEncoded
    Observable<User> setUserInfo(@Header(HttpConfig.TOKEN) String token, @Field("username") String username,
                                 @Field("nickname") String nickname, @Field("sex") String sex, @Field("stature") String height,
                                 @Field("weight") String weight, @Field("birth") String birth, @Field("default_avatar") String default_avatar,
                                 @Field("ills") String ills);

    /**
     * 上传基本用户信息，不包含头像
     * @param token 登录/注册成功后的Token
     * @param infoMap 第一次设置基本用户信息是必填参数的集合
     * @return
     */
    @POST(HttpConfig.API_USER_SET_INFO)
    @FormUrlEncoded
    Observable<User> setUserInfo(@Header(HttpConfig.TOKEN) String token, @FieldMap Map<String, String> infoMap);

    /**
     * 修改用户基本信息，不包含上传头像
     * @param token 登录/注册成功后的Token
     * @param infoMap   需要修改的参数集合
     * @return
     */
    @POST(HttpConfig.API_USER_UPDATE_INFO)
    @FormUrlEncoded
    Observable<User> updateUserInfo(@Header(HttpConfig.TOKEN) String token, @FieldMap Map<String, String> infoMap);

    /**
     * 修改用户基本信息，同时支持上传头像和携带String参数
     * @param params    String参数集合
     * @param file  头像文件
     * @param token 登录/注册成功后的Token
     * @return
     */
    //设置用户信息,post表单上传
    @POST(HttpConfig.API_USER_UPDATE_INFO)
    @Multipart
    Observable<User> updateUserInfo(@PartMap Map<String, RequestBody> params, @Part MultipartBody.Part file,
                                    @Header(HttpConfig.TOKEN) String token);

    /**
     * 创建家庭
     * fname    string	必须		家庭名称
     * type	    int	    可选	 1	家庭类型 1-普通家庭、2-药店家庭
     *
     * @param token 登录/注册成功后的Token
     * @param infoMap   需要修改的参数集合
     * @return
     */
    @POST(HttpConfig.API_FAMILY_CREATE)
    @FormUrlEncoded
    Observable<Family> createFamily(@Header(HttpConfig.TOKEN) String token, @FieldMap Map<String, String> infoMap);

    /**
     * 获取家庭成员列表
     * @param token
     * @param family_id
     * @return
     */
    @POST(HttpConfig.API_USER_GET_MEMBER_LIST)
    @FormUrlEncoded
    Observable<Family> getMemberList(@Header(HttpConfig.TOKEN) String token, @Field("fid") int family_id);

//    @POST(HttpConfig.API_USER_LOGOUT)
//    Observable<Empty> logout(@Header(HttpConfig.TOKEN) String token, @Body User user);

    /**
     * 用户（家庭）退出登录
     * @param token
     * @return
     */
    @POST(HttpConfig.API_USER_LOGOUT)
    Observable<Empty> logout(@Header(HttpConfig.TOKEN) String token);

    /**
     * 检查是否有版本更新
     * @param curVersion    用户当前安装的app版本号
     * @param appId 		服务器定义的，不同App的识别号，本app默认：10002
     * @return  版本更新内容
     */
    @GET(HttpConfig.API_CHECK_VERSION)
    Observable<VersionConfig> checkVersion(@Query("v") String curVersion, @Query("appid") int appId);

    /**
     * 单个文件下载
     * @param url
     * @return
     */
    @GET
    @Streaming
    Observable<ResponseBody> downLoadFile(@Url String url);

    /**
     * 家庭成员切换
     * @return 成员信息
     */
    @POST(HttpConfig.API_USER_MEMBER_SWITCH)
    @FormUrlEncoded
    Observable<User> switchMember(@Header(HttpConfig.TOKEN) String token,
                                  @Field("fid") int family_id, @Field("uid") int user_id);

    /**
     * 绑定或者更改手机号
     *  accessToken	必须	访问令牌
     *  phone	必须	手机号码
     *  code	必须	短信验证码
     *  pwd	    可选	MD5加密后的用户密码
     * @return 成员信息
     */
    @POST(HttpConfig.API_USER_BIND_PHONE)
    @FormUrlEncoded
    Observable<User> bindPhone(@Header(HttpConfig.TOKEN) String token, @FieldMap Map<String, String> paramMap);

    /**
     * 获取家庭列表
     * @param token
     * @param user_id   当前登录账号对应的uid
     * @return  家庭列表
     */
    @POST(HttpConfig.API_FAMILY_LIST)
    @FormUrlEncoded
    Observable<List<Family>> getFamilyList(@Header(HttpConfig.TOKEN) String token, @Field("uid") int user_id);

    /**
     * 根据Token返回当前的登录账号Uid，而不是绑定的Uid
     * @param token
     * @return  初始的登录账号Uid
     */
    @POST(HttpConfig.API_USER_LOGIN_UID)
    Observable<User> getLoginUid(@Header(HttpConfig.TOKEN) String token);

    /**
     *  家庭新成员手机密码注册接口
     * @param token
     * @param paramMap
     * @return
     */
    @POST(HttpConfig.API_USER_MEMBER_REGISTER)
    @FormUrlEncoded
    Observable<RegisterResult> registerMember(@Header(HttpConfig.TOKEN) String token, @FieldMap Map<String, String> paramMap);

    /**
     * 上传新成员的基本信息，不包含头像
     * @param infoMap 第一次设置基本用户信息是必填参数的集合
     * @return
     */
    @POST(HttpConfig.API_USER_MEMBER_SET_INFO)
    @FormUrlEncoded
    Observable<User> setMemberInfo(@Header(HttpConfig.TOKEN) String token, @FieldMap Map<String, String> infoMap);

    /**
     * 修改新成员所有信息(PS:只能在注册新成员时调用)，不包含上传头像
     * @param token 登录/注册成功后的Token
     * @param infoMap   需要修改的参数集合
     * @return
     */
    @POST(HttpConfig.API_USER_MEMBER_UPDATE_INFO)
    @FormUrlEncoded
    Observable<User> updateMemberInfo(@Header(HttpConfig.TOKEN) String token, @FieldMap Map<String, String> infoMap);

    /**
     * 修改新成员所有信息(PS:只能在注册新成员时调用)，同时支持上传头像和携带String参数
     * @param params    String参数集合
     * @param file  头像文件
     * @param token 登录/注册成功后的Token
     * @return
     */
    //设置用户信息,post表单上传
    @POST(HttpConfig.API_USER_MEMBER_UPDATE_INFO)
    @Multipart
    Observable<User> updateMemberInfo(@PartMap Map<String, RequestBody> params, @Part MultipartBody.Part file,
                                    @Header(HttpConfig.TOKEN) String token);

    /**
     * 总评分的健康指数获取
     */
    @GET(HttpConfig.API_HEALTH_ALL_SCORE)
    Observable<HealthScore> getHealthScore(@Header(HttpConfig.TOKEN) String token);

    /**
     * 单项检测分数获取
     */
    @GET(HttpConfig.API_HEALTH_USER_SCORE)
    Observable<HealthUserScore> getHealthUserScore(@Header(HttpConfig.TOKEN) String token);

    //add for plan line by shibo.zheng 20190222 start
    /**
     * 获取用户今日计划
     * @param token  登录或者注册成功后，返回的token
     * @return
     */
    @GET(HttpConfig.API_GET_USER_SETTINGS)
    Observable<List<PlanTodayBean.PlanBean>> getUserSettings(@Header(HttpConfig.TOKEN) String token);

    /**
     * 获取资讯信息
     * @param pageSize
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConfig.API_GET_ARTICLE_LIST)
    Observable<InformationBean> getArticleList(@Header(HttpConfig.TOKEN) String token, @Field("pagesize") int pageSize);

    //add for plan line by shibo.zheng 20190222 end
}
