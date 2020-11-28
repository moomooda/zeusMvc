package group.zeus.aop.processor;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 14:16
 */
public abstract class AbstractAutoProxyCreator implements BeanPostProcessor{

    public abstract Object postProcessAfterInitialization(Object bean);

    public abstract Object getEarlyBeanReference(Object bean);
}
