package uk.co.magictractor.spew.server.netty;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

/** This is expected to be last in the pipeline. */
public class InboundExceptionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(InboundExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception caught:\n{}", cause);

        // TODO! body
        // TODO! shutdown the server
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
            HTTP_1_1, INTERNAL_SERVER_ERROR);

        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }

}
