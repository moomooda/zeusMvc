package group.zeus.web;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * @Author: maodazhan
 * @Date: 2020/10/21 20:04
 */
public class TestDispatcherServlet {

    DispatcherServlet dispatcherServlet = new DispatcherServlet();

    @Test
    public void testService0(){
        String uri = "/test/get";
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, uri);
        dispatcherServlet.init();
        dispatcherServlet.service(request, null);
        String uri1 = "/test/get?name=mdz";
        DefaultFullHttpRequest request1 = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, uri1);
        dispatcherServlet.service(request1, null);
    }

    @Test
    public void testService1(){
        Charset charset = Charset.forName("UTF-8");
        ByteBuf content = Unpooled.copiedBuffer("{\n" +
                "\t\"name\": \"lkq\",\n" +
                "\t\"age\": 26\n" +
                "}", charset);
        String uri = "/test/post";
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, uri, content);
        dispatcherServlet.init();
        dispatcherServlet.service(request, null);

    }
}
