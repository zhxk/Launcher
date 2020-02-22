package com.changren.android.upgrade;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.changren.android.launcher.R;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.ToastUtils;
import com.changren.android.upgrade.entity.VersionConfig;
import com.changren.android.upgrade.util.DeviceUtils;

import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2019-01-16 15:37
 * Description: 升级服务
 */
public class UpgradeService extends IntentService {

    private static final String ACTION_CHECK_VERSION = "service.action.check_version";
    private static final String ACTION_DOWNLOAD = "service.action.download";
    public static final String VERSION_DATA = "version_data";
    private static final String APP_VERSION = "app_version";
    private static final String DOWNLOAD_URL = "downloadUrl";
    private static final String APK_PATH = "apkPath";

    /**
     * 必须有一个空参数的构造实现父类的构造,否则会报异常
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * Used to name the worker thread, important only for debugging.
     */
    public UpgradeService() {
        super("UpgradeService");
    }

    public static void startUpgradeService(Context context) {
        Intent intent = new Intent(context, UpgradeService.class);
        intent.setAction(ACTION_CHECK_VERSION);
        context.startService(intent);
    }

    public static void startDownload(Context context, VersionConfig data) {
        Intent intent = new Intent(context, UpgradeService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(VERSION_DATA, data);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 这个是IntentService的核心方法,它是通过串行来处理任务的，通过Action区分不同任务
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (ACTION_CHECK_VERSION.equals(action)) {
            DownloadManager.getInstance().setContext(this);
            DownloadManager.getInstance().doUpgrade();
//            DownloadManager.getInstance()
//                .checkVersion(DeviceUtils.getVersionName(this), APP_ID,
//                    new DownloadManager.DownloadListener<VersionConfig>() {
//                    @Override
//                    public void onSucceed(Disposable d, VersionConfig data) {
//                        if (data == null || TextUtils.isEmpty(data.getUrl())) {
//                            ToastUtils.showLong("已经是最新版本");
//                        } else {
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailed(Disposable d, String msg) {
//
//                    }
//                });
        } else if (ACTION_DOWNLOAD.equals(action)) {
            //弹出下载进度提示，以及下载通知
//            showNotification(this);
//            VersionConfig versionConfig = (VersionConfig) intent.getSerializableExtra(VERSION_DATA);
//            DownloadManager.getInstance().setContext(this);
//            Disposable disposable = DownloadManager.getInstance().downloadApk(versionConfig);




//            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            builder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle("开始下载")
//                    .setAutoCancel(true)
//                    .setContentText("版本更新");
//
//            notificationManager.notify(0, builder.build());
//
//            String url = intent.getStringExtra(DOWNLOAD_URL);
//            String apkPath = intent.getStringExtra(APK_PATH);
//            handleUpdate(url, apkPath);
        }
    }

    public static void showNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
//        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.csdn.net/itachi85/"));
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
//        builder.setContentIntent(pendingIntent);

//        final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.notification_custom);
//        rv.setTextViewText(R.id.tv_title, "Launcher更新包下载");
//        rv.setTextViewText(R.id.tv_content, "下载中...1%");
//        rv.setTextViewText(R.id.time_tv, DateUtil.format(new Date(System.currentTimeMillis()), "HH:mm"));
//        if (bitmap == null) {
//            rv.setViewVisibility(R.id.content_iv, View.GONE);
//        } else {
//            rv.setViewVisibility(R.id.content_iv, View.VISIBLE);
//            rv.setImageViewBitmap(R.id.content_iv, bitmap);
//        }
//        rv.setOnClickPendingIntent(R.id.btn1, getPendingIntent(context'type1'));
//        rv.setOnClickPendingIntent(R.id.btn2, getPendingIntent(context, 'type2');
//        rv.setOnClickPendingIntent(R.id.btn3, getPendingIntent(context,'type3'));

//        builder.setSmallIcon(R.mipmap.ic_logo);
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo));
        builder.setAutoCancel(false);
//        builder.setContentTitle("Launcher更新包下载");
//        builder.setContentText("下载中...0%");
        builder.setVisibility(Notification.VISIBILITY_PRIVATE);

//        builder.setContent(rv);//自定义布局
//            .setContentIntent(getPendingIntent(context));

        //设置通知自定义扩展布局
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            builder.setCustomBigContentView(rv);
//        }

        //设置通知icon
        if (context.getApplicationInfo().targetSdkVersion >= 21 && Build.VERSION.SDK_INT >= 21){
            builder.setSmallIcon(R.mipmap.ic_logo);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo));
        } else {
            builder.setSmallIcon(R.mipmap.ic_logo);
        }

        //设置点击跳转
        Intent hangIntent = new Intent();
        hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        hangIntent.setClass(context, UpdatePromptsActivity.class);
        //如果描述的PendingIntent已经存在，则在产生新的Intent之前会先取消掉当前的
        PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setFullScreenIntent(hangPendingIntent, true);

        Notification notification = builder.build();
//        if(android.os.Build.VERSION.SDK_INT >= 16) {
//            notification.bigContentView = rv;
//        }
//        notification.contentView = rv;

        notificationManager.notify(2, notification);

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//
//        mBuilder.setPriority(Notification.PRIORITY_MAX);//可以让通知显示在最上面
//        mBuilder.setSmallIcon(R.mipmap.small_icon);
//        mBuilder.setWhen(System.currentTimeMillis());
//        mBuilder.setAutoCancel(true);
//
//        if (shouldRemind(true)) {
//            mBuilder.setDefaults(Notification.DEFAULT_ALL);//使用默认的声音、振动、闪光
//        }
//
//        Bitmap bmIcon = BitmapFactory.decodeResource(
//                context.getResources(), R.mipmap.ic_launcher);
//
//        Bitmap largeIcon = bmIcon;
//        Bitmap bmAvatar = GetImageInputStream(avatar);
//        if (bmAvatar != null) {
//            //如果可以获取到网络头像则用网络头像
//            largeIcon = bmAvatar;
//        }
//
//        content = name + ":" + content; //内容为 xxx:内容
//        //        int unreadCount = RongIMClient.getInstance().getUnreadCount(conversationType, targetId);
//        //        if (unreadCount > 1) {
//        //            //如果未读数大于1，则还有拼接上[x条]
//        //            String num = String.format(UIUtils.getString(R.string.notification_num_format), unreadCount);
//        //            content = num + content;//内容为 [x条] xxx:内容
//        //        }
//
//        mBuilder.setLargeIcon(largeIcon);
//
//        mBuilder.setContentTitle(name);
//
//        Intent intent = getIntent(context, conversationType, targetId, name);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, getRandomNum(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //通知首次出现在通知栏，带上升动画效果的
//        mBuilder.setTicker(content);
//        //内容
//        mBuilder.setContentText(content);
//
//        mBuilder.setContentIntent(pendingIntent);
//        Notification notification = mBuilder.build();
//
//        int notifyId = 0;
//        try {
//            notifyId = Integer.parseInt(targetId);
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//            notifyId = -1;
//        }
//
//        //弹出通知栏
//        notificationManager.notify(notifyId, notification);
    }
}
