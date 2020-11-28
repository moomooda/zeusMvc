package group.zeus.aop.proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 14:39
 */
public class ProxyInstance {

    private DefaultBeanProxy beanProxy;

    public Object getProxy(Class<?> clazz, Object target) {
        beanProxy = new DefaultBeanProxy(target);
        Enhancer en = new Enhancer();
        en.setSuperclass(clazz);
        en.setCallbacks(new Callback[]{beanProxy});
        return en.create();
    }
}
