package group.zeus.web.support;

import group.zeus.web.exception.RequestException;
import group.zeus.web.param.RequestParamInfo;
import group.zeus.web.response.Response;
import group.zeus.web.util.RequestProcessorUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Controller调用Model返回响应
 *
 * @Author: maodazhan
 * @Date: 2020/10/17 20:06
 */
public class HandleMethodAdapter {

    private static final Logger Logger = LoggerFactory.getLogger(HandleMethodAdapter.class);

    /*TODO 目前只支持返回字符串*/
    public void handle(HandlerMethod handlerMethod, RequestParamInfo requestParamInfo, ChannelHandlerContext ctx) {
        Method method = handlerMethod.getMethod();

        /*参数解析*/
        Object[] args = RequestProcessorUtils.getResolvedArguments(method, requestParamInfo);

        /*反射调用方法*/
        Response response;
        try {
            response = (Response) method.invoke(handlerMethod.getBean(), args);
            ctx.channel().writeAndFlush(response.response()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    Logger.info("Send response for request call " + handlerMethod.getBean().getClass() + "-" + method.getName());
                }
            });
        } catch (Exception ex) {
            throw new RequestException("invoke method '" + method.getName() + "' failed", ex);
        }
    }

}
