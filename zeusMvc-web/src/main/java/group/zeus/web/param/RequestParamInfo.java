package group.zeus.web.param;

import group.zeus.web.exception.RequestException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: maodazhan
 * @Date: 2020/10/20 20:13
 */
public class RequestParamInfo {

    private Map<String, Object> bodyparams;
    private Map<String, Object> urlParams;

    public void addBodyParams(BodyParam param) {
        if (this.bodyparams== null) {
            this.bodyparams = new HashMap<>();
        }
        this.bodyparams.putIfAbsent(param.getFieldName(), param.getFieldValue());
    }

    public void addUrlParams(UrlParam param) {
        if (this.urlParams== null) {
            this.urlParams = new HashMap<>();
        }
        this.urlParams.putIfAbsent(param.getFieldName(), param.getFieldValue());
    }

    public Map<String, Object> getBodyParams(){
        return this.bodyparams;
    }

    public Map<String, Object> getUrlParams(){
        return this.urlParams;
    }
}
