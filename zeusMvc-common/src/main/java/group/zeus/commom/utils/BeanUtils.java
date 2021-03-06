package group.zeus.commom.utils;

import group.zeus.commom.exceptions.BeanException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author: maodazhan
 * @Date: 2020/11/24 21:35
 */
public class BeanUtils {

    public static <T> T instantiateClass(Constructor<T> ctor, Object... args){
        try {
            // 如果是私有构造器，则必须setAccessible
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        }
        catch (InstantiationException ex) {
            throw new BeanException("'"+ctor.getName()+"',Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex) {
            throw new BeanException("'"+ctor.getName()+",Is the constructor accessible?", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new BeanException("'"+ctor.getName()+",Illegal arguments for constructor", ex);
        }
        catch (InvocationTargetException ex) {
            throw new BeanException("'"+ctor.getName()+",Constructor threw exception", ex.getTargetException());
        }
    }
}
