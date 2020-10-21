package group.zeus.web.exception;

/**
 * @Author: maodazhan
 * @Date: 2020/10/18 13:17
 */
public class RequestException extends RuntimeException {

    public RequestException(String msg) {
        super(msg);
    }

    public RequestException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
