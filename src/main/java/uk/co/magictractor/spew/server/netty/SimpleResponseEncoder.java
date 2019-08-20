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

import static io.netty.handler.codec.http.HttpResponseStatus.SEE_OTHER;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;

import com.google.common.io.ByteStreams;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.server.SimpleErrorResponse;
import uk.co.magictractor.spew.server.SimpleRedirectResponse;
import uk.co.magictractor.spew.server.SimpleResourceResponse;
import uk.co.magictractor.spew.server.SimpleResponse;
import uk.co.magictractor.spew.server.SimpleStaticResponse;
import uk.co.magictractor.spew.server.SimpleTemplateResponse;
import uk.co.magictractor.spew.util.ExceptionUtil;

@Sharable
public class SimpleResponseEncoder extends MessageToMessageEncoder<SimpleResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, SimpleResponse next, List<Object> out) throws Exception {
        DefaultHttpResponse nettyResponse = encode(next);
        if (nettyResponse != null) {
            out.add(nettyResponse);
        }
    }

    private DefaultHttpResponse encode(SimpleResponse simpleResponse) {
        DefaultHttpResponse nettyResponse = null;

        if (simpleResponse instanceof SimpleStaticResponse) {
            nettyResponse = response((SimpleStaticResponse) simpleResponse);
        }
        else if (simpleResponse instanceof SimpleTemplateResponse) {
            nettyResponse = template((SimpleTemplateResponse) simpleResponse);
        }
        else if (simpleResponse instanceof SimpleRedirectResponse) {
            nettyResponse = redirect((SimpleRedirectResponse) simpleResponse);
        }
        else if (simpleResponse instanceof SimpleErrorResponse) {
            nettyResponse = error((SimpleErrorResponse) simpleResponse);
        }
        else {
            throw new IllegalStateException(
                "Code needs modified to handle " + simpleResponse.getClass().getSimpleName());
        }

        HttpHeaders nettyHeaders = nettyResponse.headers();
        for (SpewHeader header : simpleResponse.getHeaders()) {
            nettyHeaders.add(header.getName(), header.getValue());
        }

        return nettyResponse;
    }

    private DefaultHttpResponse response(SimpleStaticResponse staticResponse) {
        // TODO! add if modified since
        return resource(staticResponse);
    }

    private DefaultHttpResponse template(SimpleTemplateResponse templateResponse) {
        return resource(templateResponse);
    }

    private DefaultHttpResponse resource(SimpleResourceResponse simpleResponse) {
        // TODO! Not the only code where the whole stream gets read into a byte array - change to getBody() -> byte[]?
        byte[] contentBytes = ExceptionUtil.call(() -> ByteStreams.toByteArray(simpleResponse.getBodyInputStream()));
        ByteBuf content = Unpooled.wrappedBuffer(contentBytes);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
            HttpResponseStatus.valueOf(simpleResponse.getHttpStatus()), content);

        // TODO! headers...

        // TODO! not here, should already be in pre-send code
        // response.headers().add("Content-Type", simpleResponse.getHeader("Content-Type"));
        // response.headers().add("Content-Length", contentBytes.length);

        return response;
    }

    private DefaultHttpResponse redirect(SimpleRedirectResponse redirectResponse) {

        DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, SEE_OTHER);
        response.headers().add("Location", redirectResponse.getLocation());

        return response;
    }

    private DefaultHttpResponse error(SimpleErrorResponse errorResponse) {

        SimpleTemplateResponse errorTemplate = new SimpleTemplateResponse(errorResponse.getClass(),
            "error.html.template", key -> errorResponse.getValue(key));
        errorTemplate.setHttpStatus(errorResponse.getHttpStatus());

        return template(errorTemplate);
    }

}
