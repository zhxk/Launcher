package com.changren.android.launcher.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.changren.android.launcher.BuildConfig;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.LogUtils;

import java.util.ArrayList;

/**
 * Author: wangsy
 * Create: 2018-10-22 16:36
 * Description: A {@link ContentProvider} based on a Room database.
 */
public class UserInfoProvider extends ContentProvider {

    public static final String AUTHORITY = "com.changren.android.launcher.user.provider";
    public static final String USER = "user";
    public static final String TOKEN = "token";
//    public static final String FAMILY = "family";

    /** The URI for the Cheese table. */
    public static final Uri URI_CHEESE = Uri.parse("content://" + AUTHORITY + "/" + USER);

    /** The match code for some items in the Cheese table. */
    private static final int CODE_USER_DIR = 1;

    /** The match code for an item in the Cheese table. */
    private static final int CODE_USER_ITEM = 2;
    private static final int CODE_USER_TOKEN_ITEM = 3;

    /** The URI matcher. */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, USER, CODE_USER_DIR);
        MATCHER.addURI(AUTHORITY, USER + "/1", CODE_USER_ITEM);
        MATCHER.addURI(AUTHORITY, TOKEN, CODE_USER_TOKEN_ITEM);
    }

    @Override
    public boolean onCreate() {

        initLog();
        UserDatabaseManager.getInstance().createDB(getContext());

        return true;
    }

    // init it in ur application
    public void initLog() {
        final LogUtils.Config config = LogUtils.getConfig()
                .setLogSwitch(BuildConfig.DEBUG)// 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置 log 全局标签，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(true)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印 log 时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd.txt"
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1)// log 栈深度，默认为 1
                .setStackOffset(0)// 设置栈偏移，比如二次封装的话就需要设置，默认为 0
                .setSaveDays(3)// 设置日志可保留天数，默认为 -1 表示无限时长
                // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
                .addFormatter(new LogUtils.IFormatter<ArrayList>() {
                    @Override
                    public String format(ArrayList list) {
                        return "LogUtils Formatter ArrayList { " + list.toString() + " }";
                    }
                });
        LogUtils.d(config.toString());
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Context context = getContext();
        if (context == null) {
            return null;
        }

        int code = MATCHER.match(uri);
        Cursor cursor = null;
        UserDao userDao;
        switch (code) {
            case CODE_USER_DIR:
                userDao = UserDatabase.getInstance(context).userDao();
                cursor = userDao.getUsersByFamilyId();
                cursor.setNotificationUri(context.getContentResolver(), uri);
                break;
            case CODE_USER_ITEM:
                userDao = UserDatabase.getInstance(context).userDao();
                cursor = userDao.getShowUserById(AppConfig.getShowUserId());
                cursor.setNotificationUri(context.getContentResolver(), uri);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case CODE_USER_DIR:     //多条记录（集合）
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + USER;
            case CODE_USER_ITEM:    //单条记录
                return "vnd.android.cursor.item/" + AUTHORITY + "." + USER + "/1";
            case CODE_USER_TOKEN_ITEM:    //单条记录
//                return "vnd.android.cursor.item/" + AUTHORITY + "." + TOKEN + "/1";
                return AppConfig.getLoginFamilyId()>0 ? AppConfig.getToken() : null;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
