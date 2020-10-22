package group.zeus.web.resolver;

import group.zeus.web.param.RequestParamInfo;

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
