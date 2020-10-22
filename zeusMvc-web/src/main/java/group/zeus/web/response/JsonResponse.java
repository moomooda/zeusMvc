package group.zeus.web.response;

import com.alibaba.fastjson.JSON;
import group.zeus.web.util.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;


/**
 * @Author: maodazhan
 * @Date: 2020/10/22 19:18
 */
public class JsonResponse extends Response{

    @Override
    ByteBuf content() {
        if (this.paramMap !=null && !this.paramMap.isEmpty()){
            ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
            byteBuf.writeCharSequence(JSON.toJSON(paramMap).toString(), CharsetUtil.UTF_8);
            return byteBuf;
        } else{
            return Unpooled.EMPTY_BUFFER;
        }
    }

    @Override
    public FullHttpResponse response() {
        ByteBuf content = this.content();
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, Constants.JSON);
        return response;
    }
}
