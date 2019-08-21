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

}
