package group.zeus.web.netty;


import group.zeus.web.util.ThreadPoolUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: maodazhan
 * @Date: 2020/10/17 14:23
 */
public class ZeusServer {

    private static final Logger logger = LoggerFactory.getLogger(ZeusServer.class);

    private Thread thread;
    private String serverAddress;

    public ZeusServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void start() {
        thread = new Thread(
                new Runnable() {
                    ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtil.makeServerThreadPool(ZeusServer.class.getSimpleName(),
                            16, 32);

                    @Override
                    public void run() {
//                        EventLoopGroup bossGroup = new NioEventLoopGroup();
//                        EventLoopGroup workerGroup = new NioEventLoopGroup();
//                        try {
//                            ServerBootstrap bootstrap = new ServerBootstrap();
//                            bootstrap.group(bossGroup, workerGroup)
//                                    .channel(ServerSocketChannel.class)
//                                    .childHandler()

                    }
                }
        );
    }
}
