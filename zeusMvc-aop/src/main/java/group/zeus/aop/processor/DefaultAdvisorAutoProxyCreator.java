package group.zeus.aop.processor;

import group.zeus.aop.proxy.ProxyInstance;
import group.zeus.aop.utils.AspectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 14:11
 */
public class DefaultAdvisorAutoProxyCreator extends AbstractAutoProxyCreator{

    private final Map<String, Object> earlyProxyReferences = new ConcurrentHashMap<>(16);

    @Override
    public Object postProcessAfterInitialization(Object bean) {
        String beanName = bean.getClass().getName();
        // 不需要代理
        if (!checkProxyCapable(beanName))
            return bean;
        // 已经增强过了
        if (earlyProxyReferences.containsKey(beanName)){
            return bean;
        }
        return wrapIfNecessary(bean);
    }

    @Override
    public Object getEarlyBeanReference(Object bean) {
        String beanName = bean.getClass().getName();
        if (!checkProxyCapable(beanName))
            return bean;
        earlyProxyReferences.put(beanName, bean.getClass());
        return wrapIfNecessary(bean);
    }

    protected boolean checkProxyCapable(String beanName) {
        for (String capableName : AspectUtils.getProxyCapableClasses())
            if (beanName.matches(capableName)){
                return true;
            }
        return false;
    }

    protected Object wrapIfNecessary(Object bean){
        Object proxyBean = new ProxyInstance().getProxy(bean.getClass(), bean);
        return proxyBean;
    }
}
