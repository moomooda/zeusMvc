package group.zeus.ioc;

import group.zeus.aop.processor.DefaultAdvisorAutoProxyCreator;
import group.zeus.ioc.support.BeanFactory;
import group.zeus.ioc.support.impl.DefaultBeanFactory;

/**
 * ioc的最外层容器实现Bean的管理
 *
 * @Author: maodazhan
 * @Date: 2020/10/13 19:36
 */
public class ApplicationContext implements BeanFactory {

    // 内部容器
    private final DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();

    public ApplicationContext() {
        loadBeanDefinitions();
        // 加载后置处理器
        postProcessBeanFactory();
        finishBeanInitialization();
    }

    @Override
    public Object getBean(String name) {
        return defaultBeanFactory.getBean(name);
    }

    @Override
    public Object getBean(Class<?> requiredType) {
        return defaultBeanFactory.getBean(requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return defaultBeanFactory.containsBean(name);
    }

    private void loadBeanDefinitions() {
        AnnotationBeanReader annotationBeanReader = new AnnotationBeanReader();
        annotationBeanReader.readBeanDefinition(defaultBeanFactory);
    }

    private void finishBeanInitialization() {
        defaultBeanFactory.instantiateSingletons();
        // TODO 目前只初始化singleTon
    }

    private void postProcessBeanFactory(){
        // 创建代理对象的beanPostProcessor
        defaultBeanFactory.addBeanPostProcessor(new DefaultAdvisorAutoProxyCreator());
    }
}
