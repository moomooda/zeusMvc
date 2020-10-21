package group.zeus.web.support;

import java.lang.reflect.Method;

/**
 * @Author: maodazhan
 * @Date: 2020/10/18 9:39
 */
public class HandlerMethod {

    // controller bean
    private Object bean;

    private Method method;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }


    public Method getMethod() {
        return method;
    }
}
