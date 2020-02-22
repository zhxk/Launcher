package com.changren.android.launcher.database;

import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.database.entity.User;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: wangsy
 * Create: 2018-12-13 11:39
 * Description: TODO(描述文件做什么)
 */
public class LocalDataSource {

    private static LocalDataSource INSTANCE = null;

    private LocalDataSource() {}

    public static LocalDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (LocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSource();
                }
            }
        }
        return INSTANCE;
    }

    public void getFamilyList(final String[] ids, Scheduler scheduler, Observer<List<Family>> observer) {
        Observable.create(new ObservableOnSubscribe<List<Family>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Family>> emitter) {
                emitter.onNext(UserDatabaseManager.getInstance().getFamilyList(ids));
                emitter.onComplete();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(scheduler)
        .subscribe(observer);
    }

    public void getShowUserById(final int family_id, final int user_id, Scheduler scheduler, Observer<User> observer) {
        Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) {
                emitter.onNext(UserDatabaseManager.getInstance().getShowUserById(family_id, user_id));
                emitter.onComplete();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(scheduler)
        .subscribe(observer);
    }

}
