package group.zeus.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import group.zeus.web.annotation.RequestMethod;
import group.zeus.web.exception.RequestException;
import group.zeus.web.param.BodyParam;
import group.zeus.web.param.RequestParamInfo;
import group.zeus.web.support.HandleMapping;
import group.zeus.web.support.HandleMethodAdapter;
import group.zeus.web.support.HandlerMethod;
import group.zeus.web.util.ApplicationContextUtils;
import group.zeus.web.util.Constants;
import group.zeus.web.util.RequestProcessorUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

import java.util.Map;

import static group.zeus.web.util.RequestProcessorUtils.processUrlParams;

/**
 * DispatcherServlet Like a HttpServlet, but not the same
 * Dispatch Request to responding Controller
 * @Author: maodazhan
 * @Date: 2020/10/17 18:25
 */
public class DispatcherServlet {

    private volatile boolean initialized = false;

    private final Object lock = new Object();

    /*路由Controller*/
    private HandleMapping handleMapping = new HandleMapping();

    /*解析方法参数*/
    private HandleMethodAdapter handleMethodAdapter = new HandleMethodAdapter();

    public void init (){
            // DCL保证线程安全
            if (isInitialized())
                return;
            synchronized (this.lock) {
                if (isInitialized())
                    return;
                ApplicationContextUtils.refresh();
                handleMapping.init();
                initialized = true;
            }
    }

    /*dispatch request*/
    public void service(HttpRequest request, ChannelHandlerContext ctx){
        HttpMethod requestMethod = request.method();
        RequestParamInfo requestParamInfo = new RequestParamInfo();
        String url = processUrlParams(request.uri(), requestParamInfo);
        if (requestMethod.equals(HttpMethod.GET))
            doGet(url, ctx, requestParamInfo);
        else if (requestMethod.equals(HttpMethod.POST))
            doPost(request, url, ctx, requestParamInfo);
        else
            throw new RequestException("Such request not allowed");
    }

    public boolean isInitialized() {
        return initialized;
    }

    private void doPost(HttpRequest request, String url, ChannelHandlerContext ctx, RequestParamInfo requestParamInfo){
        HandlerMethod handlerMethod = handleMapping.getHandler(url, RequestMethod.POST);
        switch (RequestProcessorUtils.getRequestContentType(request)){
            // process different content type of params
            case Constants.JSON:
                String content = ((FullHttpRequest) request).content().toString(CharsetUtil.UTF_8);
                JSONObject object = JSON.parseObject(content);
                if (object != null){
                    for(Map.Entry<String, Object> entry: object.entrySet())
                        requestParamInfo.addBodyParams(new BodyParam(entry.getKey(), entry.getValue()));
                }
                break;
            case Constants.FORM:
                throw new RequestException("Such request not supported present");
            case Constants.MULTI_PART:
                throw new RequestException("Such request not supported present");
            default:
                throw new RequestException("Such request not allowed");
        }
        handleMethodAdapter.handle(handlerMethod, requestParamInfo, ctx);
    }

    private void doGet(String url, ChannelHandlerContext ctx, RequestParamInfo requestParamInfo){
        HandlerMethod  handlerMethod = handleMapping.getHandler(url, RequestMethod.GET);
        handleMethodAdapter.handle(handlerMethod, requestParamInfo, ctx);
    }

    public static class InstanceHolder{
        private static final DispatcherServlet INSTANCE = new DispatcherServlet();
    }

    // singleton
    public static DispatcherServlet getDispatcherServlet(){
        return InstanceHolder.INSTANCE;
    }
}
