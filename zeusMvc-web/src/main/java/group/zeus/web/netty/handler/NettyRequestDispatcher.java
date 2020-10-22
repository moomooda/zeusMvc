package group.zeus.web.netty.handler;

import group.zeus.web.DispatcherServlet;
import group.zeus.web.util.Constants;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: maodazhan
 * @Date: 2020/10/17 16:44
 */
@ChannelHandler.Sharable
public class NettyRequestDispatcher  extends ChannelInboundHandlerAdapter {

    private static final Logger Logger = LoggerFactory.getLogger(NettyRequestDispatcher.class);
    private ThreadPoolExecutor serverHandlerPool;

    private final DispatcherServlet dispatcherServlet = DispatcherServlet.getDispatcherServlet();

    public NettyRequestDispatcher(final ThreadPoolExecutor threadPoolExecutor){
        this.serverHandlerPool = threadPoolExecutor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        dispatcherServlet.init();
        if(msg instanceof HttpRequest){
                HttpRequest request = (HttpRequest) msg;
                if (request.uri().equalsIgnoreCase(Constants.FAVICON_ICO))
                    return; // discard invalid request
                serverHandlerPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dispatcherServlet.service((HttpRequest) msg, ctx);
                        } catch (Exception ex){
                            Logger.info(ex.getMessage(), ex);
                        }
                        finally {
                            // avoid OOM
                            ReferenceCountUtil.release(msg);
                        }
                    }
                });
        }else {
            // discard this request directly.
            ReferenceCountUtil.release(msg);
        }
    }
}
