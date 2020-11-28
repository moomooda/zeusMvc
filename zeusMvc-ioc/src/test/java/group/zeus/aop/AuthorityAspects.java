package group.zeus.aop;

import group.zeus.aop.annotation.After;
import group.zeus.aop.annotation.Aspect;
import group.zeus.aop.annotation.Before;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 21:18
 */
//@Aspect
public class AuthorityAspects {

    @Before(beanName = "group.zeus.ioc.A", methodName = "handle")
    public void logBefore(){
        System.out.println("实际方法执行之前，进行权限校验");
    }

    @After(beanName = "group.zeus.ioc.A", methodName = "handle")
    public void logAfter(){
        System.out.println("实际方法执行之后，进行权限校验");
    }
}
