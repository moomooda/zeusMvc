package group.zeus.web.resolver;

import group.zeus.web.annotation.RequestParam;
import group.zeus.web.exception.RequestException;
import group.zeus.web.param.NamedValueInfo;
import group.zeus.web.param.RequestParamInfo;
import group.zeus.web.util.Constants;
import group.zeus.web.util.ParameterConveter;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * support for @RequestParam
 * @Author: maodazhan
 * @Date: 2020/10/18 15:19
 */
public class RequestParamResolver implements MethodArgumentResolver{
    @Override
    public boolean support(Parameter parameter) {
        if (parameter.getAnnotation(RequestParam.class)!=null)
            return true;
        return false;
    }

    @Override
    public Object resolveArgument(RequestParamInfo requestParamInfo, Parameter parameter) {
        NamedValueInfo namedValueInfo = buildNamedValueInfo(parameter);
        Map<String, Object> urlParams = requestParamInfo.getUrlParams();
        String value = null;
        if (urlParams == null || urlParams.get(namedValueInfo.getName()) == null) {
            if (namedValueInfo.isRequired() && namedValueInfo.getDefaultValue() == null)
                throw new RequestException("RequestParam for '" + namedValueInfo.getName() + "' value can not be null");
            value = namedValueInfo.getDefaultValue();
        } else
            value = (String) urlParams.get(namedValueInfo.getName());
        return ParameterConveter.convert(parameter.getType(), value);
    }

    private NamedValueInfo buildNamedValueInfo(Parameter parameter){
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        String defaultValue = requestParam.defaultValue().equals(Constants.NONE_VALUE) ? null:requestParam.defaultValue();
        NamedValueInfo namedValueInfo = new NamedValueInfo(requestParam.value(), requestParam.required(), defaultValue);
        return namedValueInfo;
    }
}
