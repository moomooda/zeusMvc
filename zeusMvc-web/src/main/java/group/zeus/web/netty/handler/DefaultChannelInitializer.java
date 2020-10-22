package group.zeus.web.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: maodazhan
 * @Date: 2020/10/17 16:51
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ThreadPoolExecutor threadPoolExecutor;

    public DefaultChannelInitializer(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();
        cp.addLast("request_decoder", new HttpRequestDecoder());
        cp.addLast("response_encoder", new HttpResponseEncoder());
        // support body request
        cp.addLast("post", new HttpObjectAggregator(1024 * 1024));
        cp.addLast("dispatcher_handler", new NettyRequestDispatcher(threadPoolExecutor));
    }
}
