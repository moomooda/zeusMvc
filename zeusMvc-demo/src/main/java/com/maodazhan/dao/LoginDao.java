package com.maodazhan.dao;

import com.maodazhan.entity.User;
import group.zeus.ioc.annotation.Repository;
import group.zeus.ioc.annotation.Resource;

/**
 * @Author: maodazhan
 * @Date: 2020/11/30 22:29
 */
@Repository
public class LoginDao {

    @Resource
    private User user;

    // TODO 目前不支持ORM
    public User getUserByid(long id){
        user.setName("maodazhan");
        user.setMobilePhone(13356789872L);
        user.setPassword("xxxxxxxxxxx");
        return user;
    }
}
