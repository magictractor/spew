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
package uk.co.magictractor.spew.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.example.flickr.MyFlickrApp;
import uk.co.magictractor.spew.example.google.MyGooglePhotosApp;
import uk.co.magictractor.spew.example.imgur.MyImgurApp;
import uk.co.magictractor.spew.example.twitter.MyTwitterApp;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 * Common interface for NettyCallbackServer and any alternative implementations.
 */
public interface CallbackServer {

    void run(List<RequestHandler> requestHandlers, int port);

    /**
     * <p>
     * Block until the server is stopped.
     * </p>
     * <p>
     * Usually a verification callback to the server will result in a success or
     * failure response, and the server then being stopped.
     */
    void join();

    void shutdown();

    default SpewHttpResponse handleRequest(SpewHttpRequest request, List<RequestHandler> requestHandlers) {

        OutgoingResponseBuilder responseBuilder = new OutgoingResponseBuilder();

        for (RequestHandler handler : requestHandlers) {
            handler.handleRequest(request, responseBuilder);
            if (responseBuilder.isDone()) {
                break;
            }
        }

        if (responseBuilder.isShutdown()) {
            shutdown();
        }

        return responseBuilder.build();
    }

    public static void main(String[] args) {
        // Get a few applications so that they are listed on the /applications.html page
        // Would be nice to get them all. Could be useful for unit tests too.
        MyGooglePhotosApp.get();
        MyImgurApp.get();
        MyTwitterApp.get();
        MyFlickrApp application = MyFlickrApp.get();

        CallbackServer server = SPIUtil.firstAvailable(CallbackServer.class);
        server.run(application.getServerRequestHandlers(), application.port());

        Logger logger = LoggerFactory.getLogger(server.getClass());
        logger.info("Server started");
        server.join();
        logger.info("Server shut down");
    }

}
