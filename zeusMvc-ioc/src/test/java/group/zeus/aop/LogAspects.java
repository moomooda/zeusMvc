package group.zeus.aop;

import group.zeus.aop.annotation.After;
import group.zeus.aop.annotation.Aspect;
import group.zeus.aop.annotation.Before;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 20:48
 */
//@Aspect
public class LogAspects {

    //    @Before(beanName = "com.maodazhan.test.beans.Person", methodName = "([\\s\\S]*)")
    @Before(beanName = "group.zeus.aop.Person", methodName = "say")
    public void logBefore(){
        System.out.println("实际方法执行之前，打日志");
    }

    //    @After(beanName = "com.maodazhan.test.beans.Person", methodName = "([\\s\\S]*)")
    @After(beanName = "group.zeus.aop.Person", methodName = "say")
    public void logAfter(){
        System.out.println("实际方法执行之后，打日志");
    }
}
