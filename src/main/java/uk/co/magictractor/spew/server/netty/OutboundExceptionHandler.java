package uk.co.magictractor.spew.server.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundExceptionHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
            ChannelPromise promise) throws Exception {
        LOGGER.trace("added listener to promise for {}", msg);

        // Fire exception back to input handler so a 500 response can be constructed.
        promise.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

        ctx.write(msg, promise);
    }

}
