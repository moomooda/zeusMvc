package group.zeus.ioc;

import group.zeus.commom.utils.ReflectionUtils;
import group.zeus.commom.utils.StringUtils;
import group.zeus.ioc.annotation.Component;
import group.zeus.ioc.annotation.Controller;
import group.zeus.ioc.annotation.Lazy;
import group.zeus.ioc.annotation.Repository;
import group.zeus.ioc.annotation.Service;
import group.zeus.ioc.support.impl.DefaultBeanFactory;

import java.util.Set;

/**
 * @Author: maodazhan
 * @Date: 2020/10/14 10:31
 */
public class AnnotationBeanReader {
    public void readBeanDefinition(DefaultBeanFactory defaultBeanFactory) {
        Set<Class<?>> allClasses = getAllClasses();
        Set<Class<?>> lazyClasses = ReflectionUtils.getTypesAnnotatedWith(Lazy.class);
        // 是否存在懒加载的Bean
        boolean lazy = lazyClasses.isEmpty() ? false:true;
        allClasses.forEach((clazz) -> {
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setClazz(clazz);
            if (lazy && lazyClasses.contains(clazz))
                beanDefinition.setLazyInit(true);
            // TODO 允许自定义bean name
            String beanName = StringUtils.lowFirst(clazz.getSimpleName());
            defaultBeanFactory.registerBeanDefinition(beanName, beanDefinition);
                }
        );
    }

    private Set<Class<?> > getAllClasses() {
        Set<Class<?>> allClasses = ReflectionUtils.getTypesAnnotatedWith(Component.class);
        allClasses.addAll(ReflectionUtils.getTypesAnnotatedWith(Controller.class));
        allClasses.addAll(ReflectionUtils.getTypesAnnotatedWith(Service.class));
        allClasses.addAll(ReflectionUtils.getTypesAnnotatedWith(Repository.class));
        return allClasses;
    }

}
