package group.zeus.aop.utils;

import group.zeus.aop.annotation.After;
import group.zeus.aop.annotation.Aspect;
import group.zeus.aop.annotation.Before;
import group.zeus.commom.exceptions.BeanException;
import group.zeus.commom.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 14:27
 */
public class AspectUtils {

    // adviser的class对象:adviser的实例
    private static Map<Class<?>, Object> advisorInstanceMap = new HashMap<>();

    // 注解的methodName: advice方法
    private static Map<String, Method> advisorBeforeMap = new HashMap<>();

    private static Map<String, Method> advisorAfterMap = new HashMap<>();

    private static Set<String> proxyCapableClasses = new HashSet<>();

    // pointcut是方法名的正则表达式
    static {
        Set<Class<?>> aspectSet = ReflectionUtils.getTypesAnnotatedWith(Aspect.class);
        for (Class<?> asp : aspectSet) {
            try {
                advisorInstanceMap.put(asp, asp.newInstance());
            } catch (Exception e) {
                throw new BeanException("can not create instance for '" + asp.getName() + "'", e);
            }
            for (Method method : asp.getDeclaredMethods()) {
                Before beforeAnnotation = method.getAnnotation(Before.class);
                if (beforeAnnotation != null) {
                    proxyCapableClasses.add(beforeAnnotation.beanName());
                    advisorBeforeMap.put(beforeAnnotation.methodName(), method);
                }
                After afterAnnotation = method.getAnnotation(After.class);
                if (afterAnnotation != null) {
                    proxyCapableClasses.add(afterAnnotation.beanName());
                    advisorAfterMap.put(afterAnnotation.methodName(), method);
                }
            }
        }
    }

    public static Method getBeforeAdvisorMethod(String methodName) {
        for (Map.Entry<String, Method> entry : advisorBeforeMap.entrySet()) {
            if (methodName.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Method getAfterAdvisorMethod(String methodName) {
        for (Map.Entry<String, Method> entry : advisorAfterMap.entrySet()) {
            if (methodName.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Object getAdvisorInstance(Class<?> clazz) {
        return advisorInstanceMap.get(clazz);
    }

    public static Set<String> getProxyCapableClasses(){
        return proxyCapableClasses;
    }

}
