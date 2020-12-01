package com.maodazhan.controller;

import com.maodazhan.entity.User;
import com.maodazhan.service.LoginService;
import group.zeus.ioc.annotation.Controller;
import group.zeus.ioc.annotation.Resource;
import group.zeus.web.annotation.RequestBody;
import group.zeus.web.annotation.RequestMapping;
import group.zeus.web.annotation.RequestMethod;
import group.zeus.web.response.JsonResponse;
import group.zeus.web.response.Response;

/**
 * @Author: maodazhan
 * @Date: 2020/11/30 22:01
 */
@Controller
@RequestMapping("/test")
public class LoginController {

    @Resource
    private LoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Response login(@RequestBody User user){
        String data = null;
        if (loginService.login(user))
            data = String.format("用户: %s 登录成功", user.getName());
        else
            data = String.format("用户: %s 登录失败", user.getName());
        Response response = new JsonResponse();
        response.put("data",data);
        return response;
    }

}
