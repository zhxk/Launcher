package com.changren.android.launcher.database;

import android.app.Application;

/**
 * Author: wangsy
 * Create: 2018-12-13 15:16
 * Description: 创建DataRepository数据仓库的注入类
 */
public class Injection {

    public static UserDataRepository getUserDataRepository(Application application) {
        return UserDataRepository.getInstance(RemoteDataSource.getInstance(),
                LocalDataSource.getInstance(), application);
    }
}
