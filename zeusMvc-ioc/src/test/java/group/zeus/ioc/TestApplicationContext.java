package group.zeus.ioc;

import org.junit.Test;

/**
 *  三级缓存
 * @Author: maodazhan
 * @Date: 2020/10/14 11:00
 */
public class TestApplicationContext {

    @Test
    public void testBeanCreated(){
        ApplicationContext applicationContext = new ApplicationContext();
        UserController userController = (UserController)applicationContext.getBean("userController");
        userController.sayHello();
    }

    @Test
    public void testCircularReference(){

    }

    @Test
    public void testThreadSafe(){

    }
}
