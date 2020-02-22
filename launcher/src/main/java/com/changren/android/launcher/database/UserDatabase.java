package com.changren.android.launcher.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.database.typeconverter.MedicalInfoConverter;

/**
 * Author: wangsy
 * Create: 2018-10-22 14:13
 * Description: 创建包括家庭表，成员表的数据库
 */
@Database(entities = {Family.class, User.class}, version = 2)
@TypeConverters(MedicalInfoConverter.class)
public abstract class UserDatabase extends RoomDatabase {

    private static UserDatabase INSTANCE;

    public abstract UserDao userDao();

    private static final Object sLock = new Object();

    public static UserDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    UserDatabase.class, "user.db")
                        .fallbackToDestructiveMigration().build();
            }
            return INSTANCE;
        }
    }

    public static void closeDataBase() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user "
                    + " ADD COLUMN accid TEXT");
            database.execSQL("ALTER TABLE user "
                    + " ADD COLUMN token TEXT");
            Log.i("UserDatabase", "migrate: MIGRATION_1_2");
        }
    };
//    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            //do something
//            //创建表
//            database.execSQL(
//                    "CREATE TABLE student_new (student_id TEXT, student_name TEXT, phone_num INTEGER, PRIMARY KEY(student_id))");
//            //复制表
//            database.execSQL(
//                    "INSERT INTO student_new (student_id, student_name, phone_num) SELECT student_id, student_name, phone_num FROM student");
//            //删除表
//            database.execSQL("DROP TABLE student");
//            //修改表名称
//            database.execSQL("ALTER TABLE student_new RENAME TO students");
//        }
//    };
//
//    //Room 可以处理大于 1 的版本增量：我们可以一次性定义一个从1 到4 的 migration，提升迁移的速度
//    static final Migration MIGRATION_1_4 = new Migration(1, 4) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            //创建表
//            database.execSQL(
//                    "CREATE TABLE student_new (student_id TEXT, student_name TEXT, phone_num INTEGER, PRIMARY KEY(student_id))");
//            //复制表
//            database.execSQL(
//                    "INSERT INTO student_new (student_id, student_name, phone_num) SELECT student_id, student_name, phone_num FROM student");
//            //删除表
//            database.execSQL("DROP TABLE student");
//            //修改表名称
//            database.execSQL("ALTER TABLE student_new RENAME TO students");
//        }
//    };



//    database = Room.databaseBuilder(context.getApplicationContext(),StudentDatabase.class, "Demo.db")
//            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_1_4)
//        .build();


}
