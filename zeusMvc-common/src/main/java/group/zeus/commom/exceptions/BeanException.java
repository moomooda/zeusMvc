package group.zeus.commom.exceptions;

/**
 * @Author: maodazhan
 * @Date: 2020/11/24 21:36
 */
public class BeanException extends RuntimeException{

    public BeanException(String msg) {
        super(msg);
    }

    public BeanException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
