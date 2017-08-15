package com.Cecilia.vote.bean;

import com.Cecilia.vote.server.UserMain;

import java.util.Comparator;

/**
 * 用户Bean实体类
 * Created by Cecilia on 2017/8/1.
 */
public class UserBean {

    private String id;//账户

    private String userName;//用户名

    private String password;//密码

    private String type;//用户类型

    private UserMain userMain;//用户主类

    public UserBean() {
    }

    public UserBean(String id, String userName, String password, String type) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.type = type;
        userMain = new UserMain();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
