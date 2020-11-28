package group.zeus.web.support;

import group.zeus.commom.exceptions.BeanException;
import group.zeus.commom.utils.ReflectionUtils;
import group.zeus.ioc.annotation.Controller;
import group.zeus.web.annotation.RequestMapping;
import group.zeus.web.annotation.RequestMethod;
import group.zeus.web.util.ApplicationContextUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: maodazhan
 * @Date: 2020/10/17 16:55
 */
public class HandleMapping {

    /* <Path,RequestMethod>:<Bean,Method>*/
    private Map<RoutingRequest, HandlerMethod> mappings = new ConcurrentHashMap<>(64);

    /* ioc初始化mappings*/
    public void init() {
        Set<Class<?>> controllerSet = ReflectionUtils.getTypesAnnotatedWith(Controller.class);
        controllerSet.forEach((clazz) -> {
                    Method[] methods = clazz.getDeclaredMethods();
                    RequestMapping clazzRequestMapping = clazz.getAnnotation(RequestMapping.class);
                    String parentPath = clazzRequestMapping.value();
                    for (Method method : methods) {
                        RequestMapping methodRequestMappingAnnotation = method.getAnnotation(RequestMapping.class);
                        if (methodRequestMappingAnnotation == null)
                            continue;
                        method.setAccessible(true);
                        String path = methodRequestMappingAnnotation.value();
                        RequestMethod[] allowedRequests = methodRequestMappingAnnotation.method();
                        try {
                            mappings.put(new RoutingRequest(parentPath + path, allowedRequests), new HandlerMethod(ApplicationContextUtils.getApplicationContext().getBean(clazz), method));
                        } catch (Exception ex) {
                            throw new BeanException("Init controller failed, Cannot create instance for controller (" + clazz.getName() + ")", ex);
                        }
                    }
                }
        );
    }

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
}
