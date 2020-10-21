package group.zeus.web.support;

import group.zeus.web.exception.RequestException;
import group.zeus.web.param.RequestParamInfo;
import group.zeus.web.util.RequestProcessorUtils;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

/**
 * Controller调用Model返回响应
 * @Author: maodazhan
 * @Date: 2020/10/17 20:06
 */
public class HandleMethodAdapter {

    /*TODO 目前只支持返回字符串*/
    public Object handle (HandlerMethod handlerMethod, RequestParamInfo requestParamInfo, ChannelHandlerContext ctx){
        Method method = handlerMethod.getMethod();

        /*参数解析*/
        Object [] args = RequestProcessorUtils.getResolvedArguments(method, requestParamInfo);

        /*反射调用方法*/
        String returnValue;
        try{
            returnValue = (String) method.invoke(handlerMethod.getBean(), args);
            return  returnValue;
        } catch (Exception ex){
            throw new RequestException("invoke method '" + method.getName() + "' failed", ex);
        }
    }

}
