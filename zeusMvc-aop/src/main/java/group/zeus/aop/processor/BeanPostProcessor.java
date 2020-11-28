package group.zeus.aop.processor;

/**
 * @Author: maodazhan
 * @Date: 2020/11/27 15:16
 */
public interface BeanPostProcessor {
    Object postProcessAfterInitialization(Object bean);
}
