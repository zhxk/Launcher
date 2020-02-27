package com.changren.android.launcher;

import android.app.Application;
import com.tencent.bugly.crashreport.CrashReport;

public class LauncherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Bugly，建议在测试阶段建议设置成true，发布时设置为false
        CrashReport.initCrashReport(getApplicationContext(), "90532a70c8", false);
    }
}
