package group.zeus.ioc.support;

/**
 * IOC容器顶级接口
 * @Author: maodazhan
 * @Date: 2020/10/14 10:48
 */
public interface BeanFactory {

    Object getBean(String name);

    Object getBean(Class<?> requiredType);

    boolean containsBean(String name);
}
