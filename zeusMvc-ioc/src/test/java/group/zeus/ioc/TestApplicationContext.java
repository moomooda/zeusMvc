package group.zeus.ioc;

import group.zeus.aop.Person;
import org.junit.Test;

/**
 *  三级缓存IOC + AOP
 * @Author: maodazhan
 * @Date: 2020/10/14 11:00
 */
public class TestApplicationContext {

//    @Test
    // 一级缓存测试
    public void testBeanCreated(){
        ApplicationContext applicationContext = new ApplicationContext();
        UserController userController = (UserController)applicationContext.getBean("userController");
        userController.sayHello();
    }

//    @Test
    // 三级缓存+循环引用测试
    public void testCircularReference(){
        ApplicationContext applicationContext = new ApplicationContext();
        A a = (A) applicationContext.getBean("a");
        B b = (B) applicationContext.getBean("b");
        C c = (C) applicationContext.getBean("c");
        assert a.getB() == b && a.getC() == c;
        assert b.getA() == a && b.getC() == c;
        assert c.getA() == a && c.getB() == b;
//        System.out.println("bean B: " + b);
//        System.out.println("被依赖的bean B: " + a.getB());
//        System.out.println("bean A: " + a);
//        System.out.println("被依赖的bean A: " + b.getA());
    }

//    @Test
//    public void testThreadSafe(){
//
//    }

//    @Test
    // 简单AOP测试
    public void testAop(){
        ApplicationContext applicationContext = new ApplicationContext();
        Person person = (Person) applicationContext.getBean("person");
        person.say();
        person.hello();
        System.out.println(person);
    }

//    @Test
    // 三级缓存+循环引用+AOP测试
    public void testIocAOP(){
        ApplicationContext applicationContext = new ApplicationContext();
        // a是代理对象
        A a = (A) applicationContext.getBean("a");
        B b = (B) applicationContext.getBean("b");
        C c = (C) applicationContext.getBean("c");
        // b 和 c populate的都是最终的代理对象
        assert a.getB() == b && a.getC() == c;
        assert b.getA() == a && b.getC() == c;
        assert c.getA() == a && c.getB() == b;
        a.handle();
        a.printDependentBeans();
        System.out.println("a代理对象: " + a);
        System.out.println(a instanceof A);
    }
}
