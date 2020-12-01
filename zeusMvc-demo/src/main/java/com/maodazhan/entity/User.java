package com.maodazhan.entity;

import group.zeus.ioc.annotation.Component;

/**
 * @Author: maodazhan
 * @Date: 2020/11/30 20:35
 */
@Component
public class User {

    private String name;

    private String password;

    private Long mobilePhone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(Long mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}
