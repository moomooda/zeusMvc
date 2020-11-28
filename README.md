

# zeusMvc

本项目是学习Spring、SpringMVC、Netty框架之后，造的一个小轮子。它是一个轻量级的Web框架，基于Netty提供通信服务，支持Http、Https的RESTful Web服务，同时引入了IOC和AOP敏捷开发。



[TOC]

## 如何使用

可以参考zeusMvc-demo的样例。

1. xx
2. xx
3. xx
4. xx
5. xx
## IOC实现【只支持单例Bean】

一个单例Bean的创建过程是如下3步：

1.  实例化

   ```Java
   Object bean = createBeanInstance(beanName, beanDefinition);
   ```

2. 属性注入

   ```Java
   populateBean(beanName, beanDefinition, bean);
   ```

3. 初始化

   ```Java
   exposedObject = initializeBean(bean);
   ```

以下是IOC+循环引用+AOP联调的单元测试，debug一遍就都清楚了

```Java
 @Test
    // 三级缓存+循环引用+AOP测试
    public void testIocAOP(){
        ApplicationContext applicationContext = new ApplicationContext();
        // a是代理对象
        A a = (A) applicationContext.getBean("a");
        B b = (B) applicationContext.getBean("b");
        C c = (C) applicationContext.getBean("c");
        // b 和 c populate的都是最终的代理对象
        assert a.getB() == b && a.getC() == c;
        assert b.getA() == a && b.getC() == c;
        assert c.getA() == a && c.getB() == b;
        a.handle();
        a.printDependentBeans();
        System.out.println("a代理对象: " + a);
        System.out.println(a instanceof A);
    }
```

**本框架的IOC实现是三级缓存，下面说明IOC从一级缓存到三级缓存的实现有何不同。**

### 一级缓存

实际上一级缓存就足以解决单例Bean的**依赖注入**问题，缓存有就去缓存取，缓存没有就创建，保证依赖注入的总是同一个单例Bean

### 二级缓存

实际上二级缓存主要是为了解决单例Bean的**循环引用**问题，增加一个二级缓存，用来存储创建中的单例Bean的中间形态。

比如A依赖B，B也依赖A；A首先实例化后，然后把半成品A【只有实例化，没有初始化完毕】放入二级缓存再去注入B，发现B一级缓存没有，于是去创建B，B实例化完后，去注入A，可是A还没初始化完，一级缓存里面没有A，那么就可以去二级缓存取半成品的A，先注入，完成初始化；那么A就可以注入初始化完毕的B，这样A和B都初始化完毕，B依赖的半成品引用A也是最终的引用A。

### 三级缓存

```Java
    // 一级缓存
	private static final Map<String, Object> SINGLE_BEANS_CACHE = new ConcurrentHashMap<>(128);
	// 二级缓存
    private static final Map<String, Object> EARLY_SIGLE_BANES_CACHE = new HashMap<>(64);
	// 三级缓存
    private static final Map<String, ObjectFactory<?>> SINGLE_FACTORIES_CACHE = new HashMap<>(64);
```

三级缓存主要是为了解决可能有**被AOP增强**的单例Bean**被循环引用**的**一致性**判断。

比如A是一个需要被增强的Bean，B是一个普通Bean，A依赖B，B也依赖A；首先实例化A，然后把半成品A放入三级缓存，再去注入B，B实例化完后，试图去注入A，然而A一级缓存 没有，二级缓存也没有，只能去三级缓存取A。

```Java
if (earlySingletonExposure) {
    // 只有真的发生了循环引用，才会执行getEarlyBeanReference()
    SingleBeanRegistry.addSingletonFactory(beanName, () -> getEarlyReference(bean));
}
```
从三级缓存中取A的时候，返回给B的引用实际上是getEarlyReference(bean)返回的引用

```Java
if (singletonFactory !=null){
    // getObject实际执行getEarlyReference的方法体，用于AOP
    singletonObject = singletonFactory.getObject();
    // 一旦二级缓存里有bean，那么就是发生循环引用了
    EARLY_SIGLE_BANES_CACHE.put(beanName, singletonObject);
    SINGLE_FACTORIES_CACHE.remove(beanName);
}
```

取完A的代理对象，就把缓存从三级缓存升级到二级缓存，后续A初始化完毕后会判断二级缓存里有没有A，判断是否发生了**循环引用**。

```Java
protected Object getEarlyReference(Object bean){
    for (BeanPostProcessor bp: this.beanPostProcessors){
        if (bp instanceof DefaultAdvisorAutoProxyCreator){
            return ((DefaultAdvisorAutoProxyCreator) bp).getEarlyBeanReference(bean);
        }
    }
    return bean;
}
```

执行getEarlyReference(bean)实际上是遍历后置处理器找到DefaultAdvisorAutoProxyCreator对A进行处理【本框架目前只有一个BeanPostProcessor实现，用于AOP】

```Java
@Override
public Object getEarlyBeanReference(Object bean) {
	String beanName = bean.getClass().getName();
	if (!checkProxyCapable(beanName))
		return bean;
	earlyProxyReferences.put(beanName, bean.getClass());
	return wrapIfNecessary(bean);
}
```

这个getEarlyBeanReference方法是DefaultAdvisorAutoProxyCreator的实例方法，首先进行判断A需不需要增强，如果不需要直接返回即可；如果需要的话，第一步是放进earlyProxyReferences这个Map，表示A已经增强过了，然后执行wrapIfNecessary(bean)，返回一个代理对象的引用。因此，B最终注入的A是代理对象，而不是原始的A。

```Java
synchronized (SINGLE_BEANS_CACHE) {
    SINGLE_BEANS_CACHE.put(beanName, singleBean != null ? singleBean : NULL_OBJECT);
    // remove成功，发生了循环引用
    EARLY_SIGLE_BANES_CACHE.remove(beanName);
    // remove成功，没有发生循环引用
    SINGLE_FACTORIES_CACHE.remove(beanName);}
```

到此B初始化完毕，把B放入一级缓存SINGLE_BEANS_CACHE。

```Java
// 最终返回的bean引用
Object exposedObject = bean;
populateBean(beanName, beanDefinition, bean);
exposedObject = initializeBean(bean);
```

重新回到初始化A的流程，注入B后，A完成了依赖注入的过程，也就是populateBean(beanName, beanDefinition, bean)执行完成，进入initializeBean(bean)

```Java
private Object initializeBean(final Object bean) {
	return applyBeanPostProcessorsAfterInitialization(bean);
}
```
```Java
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
```
应用各种后置处理器对A进行处理，当然本框架实际上只实现了一种：DefaultAdvisorAutoProxyCreator
```Java
@Override
public Object postProcessAfterInitialization(Object bean) {
    String beanName = bean.getClass().getName();
    // 不需要代理
    if (!checkProxyCapable(beanName))
        return bean;
    // 已经增强过了
    if (earlyProxyReferences.containsKey(beanName)){
        return bean;
    }
    return wrapIfNecessary(bean);
}
```

此处和之前提到的DefaultAdvisorAutoProxyCreator#getEarlyBeanReference一样，都是AOP增强Bean的实现。

可以看到通过检查earlyProxyReferences，判断A被循环引用的时候，是否已经执行了getEarlyBeanReference对A进行了增强，已经执行过了，这里就不执行了。

```Java
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
```

A的initialzeBean过程执行完毕后，会进行循环引用的一致性判断。

earlySingletonReference是DefaultAdvisorAutoProxyCreator#getEarlyBeanReference增强过的A的代理对象，从二级缓存中取出来，因此if(earlySingletonReference != null)判断成功。

 if(exposedObject == bean)判断的时候，exposedObject是原始的A，它在initializeBean过程中没有进行增强，因此会判断成功。因此exposedObject = earlySingletonReference 实际上是让最终被放入容器的A引用是之前被B注入的那个代理对象，可能此处会有疑问。

**代理对象被注入到B之后，并没有参与后续的populateBean，而是原始的A参与了后续的populateBean，那么最终放到容器的exposedObject是否持有那些原始的A注入的属性呢。**

这个问题执行上文提到的单元测试就知道啦，最终的exposedObject也持有那些属性！

## AOP实现

## Web

- Http
- Https