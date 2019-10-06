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

    // DO NOT COMMIT
    // temp for testing static pages
    public static void main(String[] args) {
        // Get a few so that they are listed on the /applications.html page
        MyGooglePhotosApp.get();
        MyImgurApp.get();
        MyTwitterApp.get();
        MyFlickrApp application = MyFlickrApp.get();

        CallbackServer server = SPIUtil.firstAvailable(CallbackServer.class);
        server.run(application.getServerRequestHandlers(), application.port());

        // server.join();
    }

}
