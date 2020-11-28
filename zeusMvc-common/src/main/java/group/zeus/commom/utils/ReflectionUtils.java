package group.zeus.commom.utils;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @Author: maodazhan
 * @Date: 2020/11/24 21:35
 */
public class ReflectionUtils {

    private static final Reflections reflections = new Reflections("");

    public static Set<Class<?>> getTypesAnnotatedWith(Class <? extends Annotation> clazz) {
        return reflections.getTypesAnnotatedWith(clazz);
    }
}
