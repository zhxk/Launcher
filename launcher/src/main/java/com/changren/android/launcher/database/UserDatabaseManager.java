package com.changren.android.launcher.database;

import android.content.Context;
import android.database.Cursor;

import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.Utils;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-12-12 19:04
 * Description: 本地数据库工具类
 */
public class UserDatabaseManager {

    private static UserDatabaseManager INSTANCE = null;
    private UserDatabase mUserDatabase;

    private UserDatabaseManager() {}

    public static UserDatabaseManager getInstance() {
        if (INSTANCE == null) {
            synchronized (UserDatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserDatabaseManager();
                }
            }
        }
        return INSTANCE;
    }

    public void createDB(Context context) {
        // 生成数据库实例
        mUserDatabase = UserDatabase.getInstance(context);
    }

    public UserDao getUserDao() {
        if (mUserDatabase == null) {
            throw new NullPointerException("UserDatabaseManager.init(context) has not call, " +
                    "remember call this function in your Application.class or ContentProvider.class");
//            createDB(Utils.getApp());
        }

        if (!mUserDatabase.isOpen()) {
            //是否重新
            LogUtils.e("attempt to re-open an already-closed object");
            UserDatabase.closeDataBase();
            createDB(Utils.getApp());
        }

        return mUserDatabase.userDao();
    }

    public boolean insertUsers(User users) {
        return getUserDao().insertUsers(users) > 0;
    }

    public void insertFamily(final Family family) {
        mUserDatabase.runInTransaction(new Runnable() {
            @Override
            public void run() {
                getUserDao().insertFamily(family);
                getUserDao().updateFamilyIdInUser(family.getFid(), family.getUid());
            }
        });
    }

    public void insertFamilyAndMemberList(final Family family) {
        mUserDatabase.runInTransaction(new Runnable() {
            @Override
            public void run() {
                getUserDao().insertFamily(family);
//                int show_user_id = AppConfig.getShowUserId() != 0
//                        ? AppConfig.getShowUserId()
//                        : family.getUid();
//
//                for (User user: family.getMember_list()) {
//                    user.setFamilyId(family.getFid());
//                    if (user.getUid() == show_user_id) {
//                        user.setSelected(true);
//                    }
////                    LogUtils.i("insertFamilyAndMemberList", user.toString());
//                }
                for (User user: family.getMember_list()) {
                    user.setFid(family.getFid());
                }
                getUserDao().insertUsers(family.getMember_list());
            }
        });
    }

    public void insertFamilyList(List<Family> familyList) {
        getUserDao().insertFamilyList(familyList);
    }

    public List<Family> getFamilyList(String[] ids) {
        return getUserDao().getFamilyListById(ids);
    }

    public int getLoginFamilyId() {
        Cursor cursor = getUserDao().getLoginFamilyId();
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                return cursor.getInt(0);
            } finally {
                cursor.close();
            }
        } else {
            return 0;
        }
    }

    public void updateLoginStatus(int family_id, boolean exit) {
        getUserDao().updateLoginStatus(family_id, exit);
    }

    public User getShowUserById(int family_id, int user_id) {
        return getUserDao().getShowUserById(family_id, user_id);
    }
}
