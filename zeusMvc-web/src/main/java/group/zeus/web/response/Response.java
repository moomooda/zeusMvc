package group.zeus.web.response;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: maodazhan
 * @Date: 2020/10/22 19:16
 */
public abstract class Response {

    Map<String, Object> paramMap = new HashMap<>();

    public void put(String name, Object data){
        this.paramMap.put(name,data);
    }

    abstract ByteBuf content();

    public abstract FullHttpResponse response();
}
