package group.zeus.aop;

import group.zeus.ioc.annotation.Component;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 20:47
 */
//@Component
public class Person {

    public void say(){
        System.out.println("我是person，我say了");
    }

    public void hello(){
        System.out.println("我是person，我hello了");
    }
}
