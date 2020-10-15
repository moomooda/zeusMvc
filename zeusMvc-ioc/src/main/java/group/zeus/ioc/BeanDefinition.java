package group.zeus.ioc;

/**
 * Bean的元数据管理
 * @Author: maodazhan
 * @Date: 2020/10/13 19:15
 */
public class BeanDefinition {

    private Class<?> beanClazz;

    private boolean lazyInit = false;

    public Class<?> getBeanClazz() {
        return beanClazz;
    }

    public void setClazz(Class<?> beanClazz) {
        this.beanClazz = beanClazz;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
}
