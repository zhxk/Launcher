package com.changren.android.launcher.database.entity;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-12-07 16:26
 * Description: TODO(描述文件做什么)
 */
public class LoginResult {

    private String token;
    private User user;
    private List<Family> family_list;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Family> getFamilyList() {
        return family_list;
    }

    public void setFamilyList(List<Family> family_list) {
        this.family_list = family_list;
    }
}
