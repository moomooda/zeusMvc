package group.zeus.ioc;

import group.zeus.ioc.annotation.Component;
import group.zeus.ioc.annotation.Resource;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 19:31
 */
//@Component
public class A {
    @Resource
    private B b;

    @Resource
    private C c;

    public void setB(B b) {
        this.b = b;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    public void setC(C c) {
        this.c = c;
    }

    public void handle(){
        System.out.println("我在执行a的handle方法");
    }

    public void printDependentBeans(){
        System.out.println("我bean a依赖的bean b: " + b);
        System.out.println("我bean a依赖的bean c: " + c);
    }
}
