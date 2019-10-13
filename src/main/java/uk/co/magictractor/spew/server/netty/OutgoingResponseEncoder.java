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

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import uk.co.magictractor.spew.core.http.header.SpewHeader;
import uk.co.magictractor.spew.server.OutgoingResponse;

@Sharable
public class OutgoingResponseEncoder extends MessageToMessageEncoder<OutgoingResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, OutgoingResponse next, List<Object> out) throws Exception {
        DefaultHttpResponse nettyResponse = encode(next);
        if (nettyResponse != null) {
            out.add(nettyResponse);
        }
    }

    /* default */ DefaultHttpResponse encode(OutgoingResponse simpleResponse) {

        HttpResponseStatus nettyStatus = HttpResponseStatus.valueOf(simpleResponse.getStatus());

        DefaultHttpResponse nettyResponse;

        ByteBuf nettyContent = null;
        byte[] body = simpleResponse.getBodyBytes();
        if (body.length == 0) {
            nettyResponse = new DefaultHttpResponse(HTTP_1_1, nettyStatus);
        }
        else {
            nettyContent = Unpooled.wrappedBuffer(body);
            nettyResponse = new DefaultFullHttpResponse(HTTP_1_1, nettyStatus, nettyContent);
        }

        HttpHeaders nettyHeaders = nettyResponse.headers();
        for (SpewHeader header : simpleResponse.getHeaders()) {
            nettyHeaders.add(header.getName(), header.getValue());
        }

        return nettyResponse;
    }

}
