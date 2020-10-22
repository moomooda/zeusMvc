package group.zeus.web.util;

import group.zeus.web.exception.RequestException;
import group.zeus.web.param.RequestParamInfo;
import group.zeus.web.param.UrlParam;
import group.zeus.web.resolver.RequestBodyResolver;
import group.zeus.web.resolver.RequestParamResolver;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * @Author: maodazhan
 * @Date: 2020/10/18 13:32
 */
public class RequestProcessorUtils {

    private static final RequestBodyResolver requestBodyResolver = new RequestBodyResolver();

    private static final RequestParamResolver requestParamResolver = new RequestParamResolver();

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcessorUtils.class);

    /* 获取请求的Content type*/
    public static String getRequestContentType(HttpRequest request) {
        /* suppoted for Test Case*/
        if (request.headers().get(Constants.CONTENT_TYPE) == null)
            return Constants.JSON;
        // refer to https://stackoverflow.com/questions/3508338/what-is-the-boundary-in-multipart-form-data
        String contentType = request.headers().get(Constants.CONTENT_TYPE).split(":")[0];
        if (contentType.contains(";")) {
            return contentType.substring(0, contentType.indexOf(";"));
        }
        return contentType;
    }

    /* 获取解析后的方法参数*/
    public static Object[] getResolvedArguments(Method method, RequestParamInfo requestParamInfo) {
        Parameter[] parameters = method.getParameters();
        Object args[] = new Object[parameters.length];
        boolean allowRequestBody = true;
        for (int i = 0; i < parameters.length; i++) {
            if (requestBodyResolver.support(parameters[i])) {
                if (!allowRequestBody)
                    throw new IllegalStateException("More than one @RequestBody modifier in one Method parameters not allowed");
                allowRequestBody = false;
                args[i] = requestBodyResolver.resolveArgument(requestParamInfo, parameters[i]);
            } else if (requestParamResolver.support(parameters[i]))
                args[i] = requestParamResolver.resolveArgument(requestParamInfo, parameters[i]);
            else
                throw new RequestException("Can not find resolver for request method '" + method.getName() + "',please check your method parameters");

        }
        return args;
    }

    /* 解析Url参数 */
    public static String processUrlParams(String url, RequestParamInfo requestParamInfo) {
        QueryStringDecoder decoder = new QueryStringDecoder(url);
        Map<String, List<String>> uriAttributes = decoder.parameters();
        if (url.contains("?"))
            url = url.substring(0, url.indexOf("?"));
        LOGGER.info(String.format("Processing request  for path: %s", url));
        uriAttributes.forEach((key, list) -> {
            if (list != null) {
                if (list.size() == 1)
                    requestParamInfo.addUrlParams(new UrlParam(key, list.get(0)));
                else
                    throw new RequestException("Parameter (one same key,multi different values) not supported present");
            }
        });
        return url;
    }
}
