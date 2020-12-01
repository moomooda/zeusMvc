package com.maodazhan.service;

import com.maodazhan.dao.LoginDao;
import com.maodazhan.entity.User;
import group.zeus.ioc.annotation.Resource;
import group.zeus.ioc.annotation.Service;

/**
 * @Author: maodazhan
 * @Date: 2020/11/30 22:27
 */
@Service
public class LoginService {

    @Resource
    private LoginDao loginDao;

    public boolean login(User user){
        User dbUser = loginDao.getUserByid(user.getMobilePhone());
        if (dbUser.getPassword().equals(user.getPassword()))
            return true;
        return false;
    }
}
