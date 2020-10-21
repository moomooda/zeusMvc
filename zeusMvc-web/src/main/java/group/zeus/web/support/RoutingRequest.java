package group.zeus.web.support;

import group.zeus.web.annotation.RequestMethod;

/**
 * @Author: maodazhan
 * @Date: 2020/10/18 12:23
 */
public class RoutingRequest {

    // request path
    private String path;

    // method type
    private RequestMethod []  requestMethods;

    public RoutingRequest(String path, RequestMethod[] requestMethods) {
        this.path = path;
        this.requestMethods = requestMethods;
    }

    public String getPath() {
        return path;
    }


    public RequestMethod[] getRequestMethods() {
        return requestMethods;
    }

}
