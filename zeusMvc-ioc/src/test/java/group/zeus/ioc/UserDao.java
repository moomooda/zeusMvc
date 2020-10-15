package group.zeus.ioc;

import group.zeus.ioc.annotation.Repository;

/**
 * @Author: maodazhan
 * @Date: 2020/10/15 13:53
 */
@Repository
public class UserDao {

    public void sayHello(){
        System.out.println("Hello, 调用dao层方法成功啦!");
    }
}
