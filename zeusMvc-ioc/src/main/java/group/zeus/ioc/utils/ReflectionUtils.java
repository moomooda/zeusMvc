package group.zeus.ioc.utils;

import org.reflections.Reflections;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 参考 https://github.com/ronmamo/reflections
 * @Author: maodazhan
 * @Date: 2020/10/14 10:32
 */
public class ReflectionUtils {

    private static final Reflections reflections = new Reflections("");

    public static Set<Class<?>> getTypesAnnotatedWith(Class <? extends Annotation> clazz) {
        return reflections.getTypesAnnotatedWith(clazz);
    }
}
