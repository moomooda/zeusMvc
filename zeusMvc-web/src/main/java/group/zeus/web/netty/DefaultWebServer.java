package group.zeus.web.netty;

import group.zeus.web.netty.handler.DefaultChannelInitializer;
import group.zeus.web.util.ThreadPoolUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: maodazhan
 * @Date: 2020/10/17 14:23
 */
public class DefaultWebServer {

    private static final Logger Logger = LoggerFactory.getLogger(DefaultWebServer.class);

    private String serverAddress;

    public DefaultWebServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void start() {
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.makeServerThreadPool(DefaultWebServer.class.getSimpleName(),
                16, 32);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new DefaultChannelInitializer(threadPoolExecutor))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);


            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            Logger.info("Server started on port {}", port);
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            if (ex instanceof InterruptedException)
                Logger.info("Web Server stop");
            else
                Logger.error("Web Server stop error", ex);
        } finally {
            try {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            } catch (Exception ex) {
                Logger.error(ex.getMessage(), ex);
            }
        }
    }
}
