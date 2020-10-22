package group.zeus.web;

import group.zeus.web.netty.DefaultWebServer;
import org.junit.Test;

/**
 * @Author: maodazhan
 * @Date: 2020/10/22 15:47
 */
public class TestDefaultWebServer {

//    @Test
    public void testStart(){
        String address = "127.0.0.1:8800";
        DefaultWebServer server = new DefaultWebServer(address);
        server.start();
    }
}
