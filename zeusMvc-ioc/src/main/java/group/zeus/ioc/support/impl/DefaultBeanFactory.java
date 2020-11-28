package group.zeus.ioc.support.impl;

import group.zeus.aop.processor.BeanPostProcessor;
import group.zeus.aop.processor.DefaultAdvisorAutoProxyCreator;
import group.zeus.commom.exceptions.BeanException;
import group.zeus.commom.utils.BeanUtils;
import group.zeus.commom.utils.StringUtils;
import group.zeus.ioc.BeanDefinition;
import group.zeus.ioc.annotation.Resource;
import group.zeus.ioc.support.BeanFactory;
import group.zeus.ioc.support.SingleBeanRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: maodazhan
 * @Date: 2020/10/14 10:51
 */
public class DefaultBeanFactory implements BeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>(128);

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

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
        // 允许取半成品的bean，允许循环引用
        Object sharedInstance = SingleBeanRegistry.getSingleton(beanName, true);
        if (sharedInstance != null)
            bean = sharedInstance;
        else {
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            if (beanDefinition == null)
                throw new BeanException("Cannot find the definition of  bean (" + beanName + ")");
//            bean = doCreateBean(beanName, beanDefinition);
//            SingleBeanRegistry.addSingleBean(beanName, bean);
            bean = SingleBeanRegistry.getSingleton(beanName, () -> {
                try {
                    return doCreateBean(beanName, beanDefinition);
                } catch (Exception ex) {
                    // 删除全部缓存
                    SingleBeanRegistry.removeSingleton(beanName);
                    throw ex;
                }
            });
        }
        return bean;
    }

    private Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        // 此时仅仅实例化，还没初始化
        Object bean = createBeanInstance(beanName, beanDefinition);
        boolean earlySingletonExposure = SingleBeanRegistry.isSingletonCurrentlyInCreation(beanName);
        if (earlySingletonExposure) {
            // 只有真的发生了循环引用，才会执行getEarlyBeanReference()
            SingleBeanRegistry.addSingletonFactory(beanName, () -> getEarlyReference(bean)
            );
        }
        // 最终返回的bean引用
        Object exposedObject = bean;
        populateBean(beanName, beanDefinition, bean);
        exposedObject = initializeBean(bean);
        // 有可能发生循环引用，所以需要判断
        if (earlySingletonExposure){
            Object earlySingletonReference = SingleBeanRegistry.getSingleton(beanName, false);
            // 判断为真，则发生了循环引用
            if(earlySingletonReference != null){
                // 判断为真，则执行initializeBean()的时候没有发生aop
                if(exposedObject == bean){
                    // earlySingletonExposure可能是代理bean
                    exposedObject = earlySingletonReference;
                }
                else
                    throw new BeanException("Error, Other beans Might depend wrong bean reference");
            }
        }
        return exposedObject;
    }

    private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) {
        Class<?> beanClazz = beanDefinition.getBeanClazz();
        if (beanClazz.isInterface())
            throw new BeanException("Specified class (" + beanName + ") is a interface");
        Constructor<?> constructorToUse;
        try {
            constructorToUse = beanClazz.getDeclaredConstructor((Class<?>[]) null);
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
            throw new BeanException("Populate bean: " + beanName + " failed");
        }
    }

    private Object initializeBean(final Object bean) {
        return applyBeanPostProcessorsAfterInitialization(bean);
    }

    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean){
        Object result = existingBean;
        for(BeanPostProcessor processor : getBeanPostProcessors()){
            Object current = processor.postProcessAfterInitialization(result);
            if (current == null){
                return result;
            }
            result = current;
        }
        return result;
    }

    public List<BeanPostProcessor> getBeanPostProcessors(){
        return this.beanPostProcessors;
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor){
        this.beanPostProcessors.add(beanPostProcessor);
    }

    protected Object getEarlyReference(Object bean){
        for (BeanPostProcessor bp: this.beanPostProcessors){
            if (bp instanceof DefaultAdvisorAutoProxyCreator){
                return ((DefaultAdvisorAutoProxyCreator) bp).getEarlyBeanReference(bean);
            }
        }
        return bean;
    }

}
