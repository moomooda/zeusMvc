package group.zeus.aop.proxy;

import group.zeus.aop.utils.AspectUtils;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 14:41
 */
public class DefaultBeanProxy implements MethodInterceptor {

    // 代理的实际对象
    private Object target;

    public DefaultBeanProxy(Object target){
        this.target = target;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Method beforeMethod = AspectUtils.getBeforeAdvisorMethod(method.getName());
        if (beforeMethod != null) {
            beforeMethod.invoke(AspectUtils.getAdvisorInstance(beforeMethod.getDeclaringClass()), args);
        }

//        Object result = methodProxy.invokeSuper(object, args);
        Object result = methodProxy.invoke(getTarget(), args);

        Method afterMethod = AspectUtils.getAfterAdvisorMethod(method.getName());
        if (afterMethod != null) {
            afterMethod.invoke(AspectUtils.getAdvisorInstance(afterMethod.getDeclaringClass()), args);
        }

        return result;
    }

    protected Object getTarget(){
        return this.target;
    }
}
