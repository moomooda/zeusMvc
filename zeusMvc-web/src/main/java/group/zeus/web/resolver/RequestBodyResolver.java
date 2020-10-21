package group.zeus.web.resolver;

import group.zeus.ioc.exception.BeanException;
import group.zeus.web.annotation.RequestBody;
import group.zeus.web.param.RequestParamInfo;
import group.zeus.web.util.ParameterConveter;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

/**
 * @Author: maodazhan
 * @Date: 2020/10/20 19:00
 */
public class RequestBodyResolver implements MethodArgumentResolver{
    @Override
    public boolean support(Parameter parameter) {
        if (parameter.getAnnotation(RequestBody.class)!=null)
            return true;
        return false;
    }

    @Override
    public Object resolveArgument(RequestParamInfo requestParamInfo, Parameter parameter) {
        Class<?> targetClazz = parameter.getType();
        Object obj;
        try {
            obj = targetClazz.newInstance();
            Field[] fields = targetClazz.getDeclaredFields();
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
        } catch(Exception ex){
            throw new BeanException("Cannot create instance for (" + targetClazz.getName() + ")", ex);
        }
        return obj;
    }
}
