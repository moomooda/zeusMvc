package group.zeus.web.resolver;

import com.sun.org.apache.xpath.internal.operations.Bool;
import group.zeus.web.param.RequestParamInfo;
import io.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.Parameter;

/**
 * 解析方法参数
 * @Author: maodazhan
 * @Date: 2020/10/18 15:13
 */
public interface MethodArgumentResolver {

    boolean support(Parameter parameter);

    Object resolveArgument(RequestParamInfo requestParamInfo, Parameter parameter);

}
