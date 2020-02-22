package com.changren.android.launcher.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.database.entity.User;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-10-22 14:16
 * Description: 数据库family，user表的访问接口
 */
@Dao
public interface UserDao {

    /**
     * 插入一条Family数据。如果Family已经存在，则覆盖它。
     *
     * @param family the family to be inserted.
     * @return 插入的rowId
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertFamily(Family family);

    /**
     * 插入一条Family数据。如果Family已经存在，则覆盖它。
     *
     * @param family the family to be inserted.
     * @return 插入的rowId
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertFamilyList(List<Family> family);

    /**
     * 根据u_id更新User表中成员所对应的family_id
     * @param family_id 要更新的id
     * @param uid   更新条件
     * @return  插入的rowId
     */
    @Query("update user set family_id = :family_id where user_id = :uid")
    int updateFamilyIdInUser(int family_id, int uid);

    /**
     * 插入多条User数据。如果user已经存在，则覆盖它。
     *
     * @param users the family to be inserted.
     * @return 插入的rowId
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUsers(User users);

    /**
     * 插入多条User数据。如果user已经存在，则覆盖它。
     *
     * @param users the family to be inserted.
     * @return 插入的rowId
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertUsers(List<User> users);

    /**
     * 获取指定的family list移除已经登录family
     * @param ids
     * @return
     */
    @Query("SELECT * FROM family where family_id in ( :ids ) and is_login<>1")
    List<Family> getFamilyListById(String[] ids);

    /**
     * 根据 familyId 获取所有的家庭成员列表
     *
     * @return all users.
     */
    @Query("SELECT * FROM user where family_id in (select family_id from family where is_login=1)")
    Cursor getUsersByFamilyId();

    /**
     * 根据family表is_login=1代表已登录，且user表中is_selected=1代表展示成员
     * @return 当前登录家庭的展示成员
     */
    @Query("select * from user where user_id=:user_id " +
            "and family_id in (select family_id from family where is_login=1)")
    Cursor getShowUserById(int user_id);

    /**
     * 根据family表is_login=1代表已登录，且user表中is_selected=1代表展示成员
     * @return 当前登录家庭的展示成员
     */
    @Query("select * from user where user_id=:user_id and family_id=:family_id")
    User getShowUserById(int family_id, int user_id);

    /**
     * 获取当前已经登录的家庭
     * TODO 注意where条件不是主键相关，返回值需要是Cursor对象，否则获取不到内容
     * @return  family_id
     */
    @Query("select family_id from family where is_login=1")
    Cursor getLoginFamilyId();

    /**
     * 根据u_id更新User表中成员所对应的family_id
     * @param family_id 要更新的id
     * @param exit   是否退出
     * @return  插入的rowId
     */
    @Query("update family set is_login = :exit where family_id = :family_id")
    int updateLoginStatus(int family_id, boolean exit);

    /**
     * 更新成员user信息
     *
     * @param user 要更新的user数据
     * @return the number of users updated. This should always be 1.
     */
    @Update
    int updateUser(User user);

//    /**
//     * Update the complete status of a task
//     *
//     * @param taskId    id of the task
//     * @param completed status to be updated
//     */
//    @Query("UPDATE tasks SET completed = :completed WHERE entryid = :taskId")
//    void updateCompleted(String taskId, boolean completed);

//    /**
//     * Delete a task by id.
//     *
//     * @return the number of tasks deleted. This should always be 1.
//     */
//    @Query("DELETE FROM Tasks WHERE entryid = :taskId")
//    int deleteTaskById(String taskId);
//
//    /**
//     * Delete all tasks.
//     */
//    @Query("DELETE FROM Tasks")
//    void deleteTasks();
//
//    /**
//     * Delete all completed tasks from the table.
//     *
//     * @return the number of tasks deleted.
//     */
//    @Query("DELETE FROM Tasks WHERE completed = 1")
//    int deleteCompletedTasks();

}
