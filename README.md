

# zeusMvc

本项目是学习Spring、SpringMVC、Netty框架之后，造的一个小轮子。它是一个轻量级的Web框架，基于Netty提供通信服务，支持RESTful Web服务，同时引入了IOC和AOP敏捷开发。



<!-- TOC -->

- [zeusMvc](#zeusmvc)
  - [如何使用](#如何使用)
  - [IOC实现【只支持单例Bean】](#ioc实现只支持单例bean)
    - [一级缓存](#一级缓存)
    - [二级缓存](#二级缓存)
    - [三级缓存](#三级缓存)
  - [AOP实现](#aop实现)
  - [Web](#web)
    - [Netty服务启动](#netty服务启动)
      - [编解码器](#编解码器)
      - [业务线程池](#业务线程池)
      - [初始化容器](#初始化容器)
    - [请求路由](#请求路由)
    - [方法参数解析和数据绑定](#方法参数解析和数据绑定)
      - [GET](#get)
      - [POST](#post)
      - [@RequestBody](#requestbody)
      - [@RequestParam](#requestparam)
    - [返回值解析和数据绑定](#返回值解析和数据绑定)
  - [功能扩展](#功能扩展)

<!-- /TOC -->


## 如何使用

1. 拷贝项目到本地后，maven编译

   ```shell
   mvn -U idea:idea -DskipTests
   ```

2. 参考module zeusMvc-demo进行使用

   1. 一个Controller类的示例

      ```Java
      @Controller
      @RequestMapping("/test")
      public class LoginController {
          @Resource
          private LoginService loginService;
          @RequestMapping(value = "/login", method = RequestMethod.POST)
          public Response login(@RequestBody User user){
              String data = null;
              if (loginService.login(user))
                  data = String.format("用户: %s 登录成功", user.getName());
              else
                  data = String.format("用户: %s 登录失败", user.getName());
              Response response = new JsonResponse();
              response.put("data",data);
              return response;
          }
      }
      ```

   2. Web服务启动的示例

      ```Java
      public class Application {
          public static void main(String[] args) {
              String address = "127.0.0.1:8800";
              DefaultWebServer server = new DefaultWebServer(address);
              server.start();
          }
      }
      ```

   3.  http请求示例
      
        ```Json
        // Request  方式:post  地址:localhost:8800/test/login Content-Type:application/json
        {
            "name": "lkqqqqq",
            "password": "xxxxxxxxxxx",
            "mobilePhone":13356789872
        }
        // Reponse  Conent-Type:application/json
        {
            "data": "用户: lkqqqqq 登录成功"
        }
        ```
      

3. IOC和AOP说明

   IOC：本框架手动实现的IOC用于管理@Service、@Component、@Repository、@Controller等注解修饰的类的创建和依赖注入，

   而依赖注入在业务逻辑非常复杂的情况会产生循环引用问题，虽然大部分情况下我们极力避免这种情况的发生。本框架原来使用二级缓存解决了循环引用的问题，**但考虑循环引用的Java Bean极端情况下可能还是个Aop的Java Bean，于是继续扩展成和Spring一样的三级缓存**。

   Aop：本项目相比Spring只实现了CGLIB的动态代理，具体使用样例参考zeusMvc-ioc的单元测试
   
   

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
1. 初始化

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
    SINGLE_FACTORIES_CACHE.remove(beanName);
}
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

这个问题执行上文提到的单元测试就知道啦，最终的exposedObject也持有那些属性，**还得说明下的是这里说的持有属性是通过getter方法持有，Field实际上是null的**！

## AOP实现

本框架采用CGLIB提供的动态代理实现AOP。

```Java
protected Object wrapIfNecessary(Object bean){
    Object proxyBean = new ProxyInstance().getProxy(bean.getClass(), bean);
    return proxyBean;
}
```

wrapIfNecessary方法会根据给定的实例bean，返回继承这个bean的代理对象proxyBean

```Java
public Object getProxy(Class<?> clazz, Object target) {
    beanProxy = new DefaultBeanProxy(target);
    Enhancer en = new Enhancer();
    en.setSuperclass(clazz);
    en.setCallbacks(new Callback[]{beanProxy});
    return en.create();
}
```

Enhancer这个对象需要注入目标对象target的运行时类型和Callback数组，最终通过调用create方法生成代理对象。

```Java
@Override
public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    Method beforeMethod = AspectUtils.getBeforeAdvisorMethod(method.getName());
    if (beforeMethod != null) {
        beforeMethod.invoke(AspectUtils.getAdvisorInstance(beforeMethod.getDeclaringClass()), args);
    }
    // Object result = methodProxy.invokeSuper(object, args);
    Object result = methodProxy.invoke(getTarget(), args);
    Method afterMethod = AspectUtils.getAfterAdvisorMethod(method.getName());
    if (afterMethod != null) {
        afterMethod.invoke(AspectUtils.getAdvisorInstance(afterMethod.getDeclaringClass()), args);
    }
    return result;
}
```

而本框架只实现了一种Callback->DefaultBeanProxy，DefaultBeanProxy重写了MethodInterceptor#intercept。

在真正要执行的方法执行之前，用反射调用BeforeMethod，从而织入Before Advice。

在真正要执行的方法执行之后，用反射调用AfterMethod，从而织入Afrter Advice。

## Web

### Netty服务启动

#### 编解码器

Netty给Http提供了现成的编解码器，其中HttpResponseEncoder是出站handler，用于向客户端发送响应；HttpRequestDecoder是入站handler，用于接收来自客户端的响应。

另外，有必要增加一个HttpObjectAggregator用于聚合Http消息。在HttpResponseEncoder和HttpRequestDecoder的父类HttpObjectDecoder 源码注释中提到

```css
 * If the content of an HTTP message is greater than {@code maxChunkSize} or
 * the transfer encoding of the HTTP message is 'chunked', this decoder
 * generates one {@link HttpMessage} instance and its following
 * {@link HttpContent}s per single HTTP message to avoid excessive memory
 * consumption. 
```

在消息体较大的情况下，HttpResponseEncoder和HttpRequestDecoder可能会生成多个消息对象,**尤其是请求方式是Post的时候**。而**HttpObjectAggregator**可以缓冲消息分段，直到聚合成一个完整的消息。

Netty的ChannelHandler配置如下：

```Java
ChannelPipeline cp = ch.pipeline();
cp.addLast("request_decoder", new HttpRequestDecoder());
cp.addLast("response_encoder", new HttpResponseEncoder());
// support body request 最大512KB
cp.addLast("post", new HttpObjectAggregator(512 * 1024));
cp.addLast("dispatcher_handler", new NettyRequestDispatcher(threadPoolExecutor));
```

#### 业务线程池

其中**NettyRequestDispatcher**是一个自定义的ChannelInbound-Handler，也是最核心的ChannelHandler，用于实际处理Http请求和返回Http响应，这块是非IO操作，涉及的处理相对比较耗时，怕**阻塞Eventloop**，于是配置了一个普通的JDK 线程池**（coresize:16,maxsize:32）**

```Java
// 线程池配置
ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.makeServerThreadPool(DefaultWebServer.class.getSimpleName(),16,32);
...
// 服务端委托线程池处理客户端的Http请求
serverHandlerPool.execute(new Runnable() {
    @Override
    public void run() {
        try {
            dispatcherServlet.service((HttpRequest) msg, ctx);
        } catch (Exception ex){
            Logger.info(ex.getMessage(), ex);
        }
        finally {
            // avoid OOM
            ReferenceCountUtil.release(msg);
        }
    }
});
```

某博客分析过，如果客户端的连接数不超过32，安排一个普通的JDK线程池足以；如果客户端连接数非常大，可以考虑给每一个Channel绑定一个**EventExecutorGroup**处理业务逻辑，避免多个Channel共享一个JDK线程池而发生死锁、阻塞等问题。

#### 初始化容器

前面提到过，本框架支持IOC，IOC在Web服务启动阶段具体是通过初始化一个**ApplicationContext**容器来管理所有和Web相关的Java Bean，方便后续管理方法路由信息的HandlerMapping从容器中获取Java Bean

```Java
private volatile boolean initialized = false;
...
public void init (){
    // DCL保证线程安全
    if (isInitialized())
        return;
    synchronized (this.lock) {
        if (isInitialized())
            return;
        ApplicationContextUtils.refresh();
        // 初始化方法路由映射
        handleMapping.init();
        initialized = true;
    }
}
```

通过**DCL 单例模式**，确保ApplicationContextUtils.refresh()**只执行一次**，init方法由**NettyRequestDispatcher#channelRead**进行调用，所以Web服务第一次处理Http请求的时候，由于要完成初始化工作，相对较慢。

### 请求路由

一个Http请求最终要落到一个Java Bean的Method上进行处理，本框架用HttpMethod封装JavaBean和Method。

Http请求通过匹配两个条件找到合适的HttpMethod，分别是URL PATH，请求行 Method（GET、POST等）。

RoutingRequest用于封装两个条件（URL PATH，请求行Method）

所以请求的路由就是去HandlerMapping匹配**RoutingRequest**，获取**HandlerMethod**

```Java
/* Request (url, type) 路由Controller的Method*/
public HandlerMethod getHandler(String url, RequestMethod requestMethod){
    for(Map.Entry<RoutingRequest, HandlerMethod>  entry: mappings.entrySet()){
        RequestMethod[] allowedRequests = entry.getKey().getRequestMethods();
        if (entry.getKey().getPath().equals(url)){
            if (Arrays.asList(allowedRequests).contains(requestMethod))
                return entry.getValue();
        }
    }
    return null;
}
```

### 方法参数解析和数据绑定

**RequestParamInfo**封装了一个Http请求的Body参数和Url参数。

#### GET 

GET请求的请求头没有Content-Type字段，只需要对URL上的参数进行处理即可。

Netty提供了**QueryStringDecoder**对URL进行处理，拆分成一个PATH 字符串和参数键值对

所以GET请求的RequestParamInfo的参数信息到此就整合完毕了

#### POST

除了POST，HTTP1.1新增的几个语义PUT、DELETE等都是HTTP请求头都是可以带Content-Type字段的，除了需要对URL上的参数进行处理外，还需要对Body的参数进行处理。

以Content-Type为application/json为例

```Java
// process different content type of params
case Constants.JSON:
    String content = ((FullHttpRequest) request).content().toString(CharsetUtil.UTF_8);
    JSONObject object = JSON.parseObject(content);
    if (object != null){
        for(Map.Entry<String, Object> entry: object.entrySet())
            requestParamInfo.addBodyParams(new BodyParam(entry.getKey(), entry.getValue()));
    }
    break;
```

通过强制转型HttpRequest为FullHttpRequest获取到content内容，对RequestParamInfo进行进一步填充。

到此无论是GET还是POST的请求，参数信息都整合完毕，后续就是把**Http请求的参数绑定到方法的入参**上

```Java
/*参数解析*/
Object[] args = RequestProcessorUtils.getResolvedArguments(method, requestParamInfo);
...
/*反射调用方法*/
response = (Response) method.invoke(handlerMethod.getBean(), args);

// TODO 返回值解析
```

到此一个请求就处理完啦，而具体如何进行数据绑定根据参数注解分为两种情况

通过反射拿到Method的所有Parameter，遍历一遍，**RequestBodyResolver**是否support该Parameter，**RequestParamResolver**是否support该Parameter，来确定选择哪个Resolver进行数据绑定

#### @RequestBody 

一个方法签名只允许一个@RequestBody注解修饰参数

对应的**RequestBodyResolver**通过反射拿到所有Field，对请求参数进行**类型转换**，绑定到对应的Field上，最终一个Java Bean就完成数据绑定啦

```Java
for (Field field: fields){
    field.setAccessible(true);
    Object value = requestParamInfo.getBodyParams().get(field.getName());
    if (value == null){
        field.set(obj, null);
        continue;
    }
    value = ParameterConveter.convert(field.getType(), value);
    field.set(obj, value);
}
```

#### @RequestParam

这个比起@RequestBody类型的参数数据绑定简单很多，不需要用到反射。

### 返回值解析和数据绑定

这块目前没做，只支持返回Json串

## 功能扩展

- [ ] 支持更多的Content-Type，比如表单application/x-www-form-urlencoded，multipart/form-data等
- [ ] 支持ORM
- [ ] 支持返回值解析，比如@ResponseBody对应的返回值解析，返回给前端
- [ ] 性能测试

