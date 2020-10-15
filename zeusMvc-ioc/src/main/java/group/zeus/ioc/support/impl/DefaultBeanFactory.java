package group.zeus.ioc.support.impl;

import group.zeus.ioc.BeanDefinition;
import group.zeus.ioc.annotation.Resource;
import group.zeus.ioc.exception.BeanException;
import group.zeus.ioc.support.BeanFactory;
import group.zeus.ioc.support.SingleBeanRegistry;
import group.zeus.ioc.utils.BeanUtils;
import group.zeus.ioc.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: maodazhan
 * @Date: 2020/10/14 10:51
 */
public class DefaultBeanFactory implements BeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>(128);

    @Override
    public Object getBean(String name) {
        return doGetBean(name);
    }

    @Override
    public Object getBean(Class<?> requiredType) {
        return getBean(StringUtils.lowFirst(requiredType.getSimpleName()));
    }

    @Override
    public boolean containsBean(String name) {
        return SingleBeanRegistry.containsSingleton(name) || this.beanDefinitionMap.containsKey(name);
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        // TODO 允许同名bean
        if (beanDefinitionMap.containsKey(beanName))
            throw new BeanException("Not allowed: two beans share the same bean name, although different class type");
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    public void instantiateSingletons() {
        this.beanDefinitionMap.forEach((beanName, beanDef) -> {
            getBean(beanName);
        });
    }

    private Object doGetBean(String beanName) {
        Object bean;
        Object sharedInstance = SingleBeanRegistry.getSingleton(beanName);
        if (sharedInstance != null)
            bean = sharedInstance;
        else {
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            if (beanDefinition == null)
                throw new BeanException("Cannot find the definition of  bean (" + beanName + ")");
            bean = doCreateBean(beanName, beanDefinition);
            SingleBeanRegistry.addSingleBean(beanName, bean);
        }
        return bean;
    }

    private Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        Object bean = createBeanInstance(beanName, beanDefinition);
        populateBean(beanName, beanDefinition, bean);
        return bean;
    }

    private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) {
        Class<?> beanClazz = beanDefinition.getBeanClazz();
        if (beanClazz.isInterface())
            throw new BeanException("Specified class (" + beanName + ") is a interface");
        Constructor<?> constructorToUse;
        try {
            constructorToUse = beanClazz.getDeclaredConstructor((Class<?>[] ) null);
            return BeanUtils.instantiateClass(constructorToUse);
        } catch (Exception e) {
            throw new BeanException("(" + beanName + "), no default constructor found", e);
        }
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, Object beanInstance) {
        Field[] beanFields = beanDefinition.getBeanClazz().getDeclaredFields();
        try {
            for (Field field : beanFields) {
                if (field.getAnnotation(Resource.class) == null)
                    continue;
                // TODO 暂时只允许@Resource 注入使用的变量名为类型名的首字母小写形式。
                if (!containsBean(field.getName()))
                    throw new BeanException("'@Resource' for field '" + field.getClass().getName() + "' can not find");
                field.setAccessible(true);
                field.set(beanInstance, getBean(field.getName()));
            }
        } catch (Exception e) {
            throw new BeanException("Populate bean: " + beanName + "failed");
        }
    }


}
