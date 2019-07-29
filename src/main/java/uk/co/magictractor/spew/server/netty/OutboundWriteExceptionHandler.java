/**
 * Copyright 2019 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.spew.server.netty;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import uk.co.magictractor.spew.server.SimpleErrorResponse;

/**
 * See https://github.com/netty/netty/issues/4721.
 */
public class OutboundWriteExceptionHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundWriteExceptionHandler.class);

    private HttpResponseStatus errorStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    private String errorMessage = "There was a problem";

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(f -> operationComplete(ctx, (ChannelPromise) f));
        super.write(ctx, msg, promise);
    }

    private void operationComplete(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (!promise.isSuccess()) {
            writeError(ctx, promise.cause());
        }
    }

    private void writeError(ChannelHandlerContext ctx, Throwable throwable) {
        DefaultHttpResponse response = null;
        try {
            response = buildErrorResponse();
        }
        catch (RuntimeException e) {
            throwable.addSuppressed(e);
            response = buildFallbackErrorResponse();
        }

        LOGGER.error("Exception in outbound handler", throwable);

        ctx.writeAndFlush(response);
    }

    /**
     * <p>
     * Build error response using SimpleResponseEncoder so that the error is
     * presented is consistent with general error presentation (using template
     * etc).
     * </p>
     */
    private DefaultHttpResponse buildErrorResponse() {
        SimpleErrorResponse simpleError = new SimpleErrorResponse(errorStatus.code(), errorMessage);
        return new SimpleResponseEncoder().encode(simpleError);
    }

    /**
     * <p>
     * Build error response with no dependencies on other classes in case those
     * are the cause of the original exception too.
     */
    private DefaultHttpResponse buildFallbackErrorResponse() {
        ByteBuf content = Unpooled.copiedBuffer(errorMessage, StandardCharsets.UTF_8);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, errorStatus, content);

        return response;
    }

}
