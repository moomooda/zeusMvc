package group.zeus.ioc;

import group.zeus.ioc.annotation.Service;

/**
 * @Author: maodazhan
 * @Date: 2020/10/15 13:53
 */
//@Service
public class UserService {

    public void sayHello(){
        System.out.println("Hello, 调用service层方法成功啦!");
    }
}
