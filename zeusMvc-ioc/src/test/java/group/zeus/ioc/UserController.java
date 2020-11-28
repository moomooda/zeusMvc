package group.zeus.ioc;

import group.zeus.ioc.annotation.Controller;
import group.zeus.ioc.annotation.Resource;

/**
 * @Author: maodazhan
 * @Date: 2020/10/15 13:53
 */
//@Controller
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserDao userDao;

    public void sayHello(){
        userService.sayHello();
        userDao.sayHello();
        System.out.println("Hello, 调用controller层方法成功啦!");
    }
}
