package group.zeus.ioc;

import group.zeus.ioc.annotation.Component;
import group.zeus.ioc.annotation.Resource;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 19:31
 */
//@Component
public class B {
    @Resource
    private A a;

    @Resource
    private C c;

    public void setA(A a) {
        this.a = a;
    }

    public A getA() {
        return a;
    }

    public C getC() {
        return c;
    }

    public void setC(C c) {
        this.c = c;
    }
}
