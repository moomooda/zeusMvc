package com.maodazhan;

import group.zeus.web.netty.DefaultWebServer;

/**
 * @Author: maodazhan
 * @Date: 2020/11/30 22:51
 */
public class Application {

    public static void main(String[] args) {
        String address = "127.0.0.1:8800";
        DefaultWebServer server = new DefaultWebServer(address);
        server.start();
    }
}
