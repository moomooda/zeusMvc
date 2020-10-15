package group.zeus.ioc.exception;

/**
 * @Author: maodazhan
 * @Date: 2020/10/15 13:00
 */
public class BeanException extends RuntimeException {

    public BeanException(String msg) {
        super(msg);
    }

    public BeanException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
