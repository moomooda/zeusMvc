package group.zeus.ioc.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例bean的注册中心
 *
 * @Author: maodazhan
 * @Date: 2020/10/13 19:51
 */
public class SingleBeanRegistry {

    private static final Object NULL_OBJECT = new Object();

    // 此处有并发，比如ioc饥饿加载完之后，后续register单例put，和get同时进行或者懒加载的单例引发的冲突
    private static final Map<String, Object> SINGLE_BEANS_CACHE = new ConcurrentHashMap<>(128);

    private static final Map<String, Object> EARLY_SIGLE_BANES_CACHE = new HashMap<>(64);

    private static final Map<String, ObjectFactory<?>> SINGLE_FACTORIES_CACHE = new HashMap<>(64);

    private static final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void addSingleBean(String beanName, Object singleBean) {
        synchronized (SINGLE_BEANS_CACHE) {
            SINGLE_BEANS_CACHE.put(beanName, singleBean != null ? singleBean : NULL_OBJECT);
            // remove成功，发生了循环引用
            EARLY_SIGLE_BANES_CACHE.remove(beanName);
            // remove成功，没有发生循环引用
            SINGLE_FACTORIES_CACHE.remove(beanName);
        }
    }

    public static void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (SINGLE_BEANS_CACHE) {
            if (!SINGLE_BEANS_CACHE.containsKey(beanName)) {
                SINGLE_FACTORIES_CACHE.put(beanName, singletonFactory);
            }
        }
    }

    public static Object getSingleton(String beanName, boolean allowEarlyReference){
        Object singletonObject = SINGLE_BEANS_CACHE.get(beanName);
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)){
            synchronized (SINGLE_BEANS_CACHE){
                singletonObject = EARLY_SIGLE_BANES_CACHE.get(beanName);
                if (singletonObject == null && allowEarlyReference){
                    ObjectFactory<?> singletonFactory = SINGLE_FACTORIES_CACHE.get(beanName);
                    if (singletonFactory !=null){
                        // getObject实际执行getEarlyReference的方法体，用于AOP
                        singletonObject = singletonFactory.getObject();
                        // 一旦二级缓存里有bean，那么就是发生循环引用了
                        EARLY_SIGLE_BANES_CACHE.put(beanName, singletonObject);
                        SINGLE_FACTORIES_CACHE.remove(beanName);
                    }
                }
            }
        }
        return singletonObject != NULL_OBJECT ? singletonObject : null;
    }

    public static Object getSingleton(String beanName, ObjectFactory<?> singleFactory) {
        Object singletonObject = SINGLE_BEANS_CACHE.get(beanName);
        if (singletonObject == null){
            singletonsCurrentlyInCreation.add(beanName);
            // getObject实际执行的是doCreateBean
            singletonObject = singleFactory.getObject();
            singletonsCurrentlyInCreation.remove(beanName);
            addSingleBean(beanName, singletonObject);
        }
        return  singletonObject != NULL_OBJECT ? singletonObject : null;
    }

    public static boolean containsSingleton(String beanName) {
        return SINGLE_BEANS_CACHE.containsKey(beanName);
    }

    public static boolean isSingletonCurrentlyInCreation(String beanName) {
        return singletonsCurrentlyInCreation.contains(beanName);
    }

    public static void removeSingleton(String beanName){
        synchronized (SINGLE_FACTORIES_CACHE){
            SINGLE_FACTORIES_CACHE.remove(beanName);
            EARLY_SIGLE_BANES_CACHE.remove(beanName);
            SINGLE_BEANS_CACHE.remove(beanName);
        }
    }
}
