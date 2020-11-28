package group.zeus.ioc;

import group.zeus.ioc.annotation.Component;
import group.zeus.ioc.annotation.Resource;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 19:31
 */
//@Component
public class C {

    @Resource
    private A a;

    @Resource
    private B b;

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}
