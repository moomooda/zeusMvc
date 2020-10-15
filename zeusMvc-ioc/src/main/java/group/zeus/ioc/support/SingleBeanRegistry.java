package group.zeus.ioc.support;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例bean的注册中心
 * @Author: maodazhan
 * @Date: 2020/10/13 19:51
 */
public class SingleBeanRegistry {

    private static final Object NULL_OBJECT = new Object();

    private static final Map<String, Object> SINGLE_BEANS_CACHE = new HashMap<>(128);

    public static void addSingleBean(String beanName, Object singleBean) {
        SINGLE_BEANS_CACHE.put(beanName, singleBean);
    }

    public static Object getSingleton(String beanName) {
        Object singletonObject = SINGLE_BEANS_CACHE.get(beanName);
        return singletonObject;
    }

    public static boolean containsSingleton(String beanName){
        return SINGLE_BEANS_CACHE.containsKey(beanName);
    }


}
