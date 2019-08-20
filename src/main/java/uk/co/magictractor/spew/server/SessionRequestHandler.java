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

import java.util.Random;

// incomplete and unused
public class SessionRequestHandler implements RequestHandler {

    // TODO! id generation
    private final String sessionId = System.currentTimeMillis() + ":" + new Random().nextLong();

    @Override
    public void handleRequest(SpewHttpRequest request, OutgoingResponseBuilder responseBuilder) {
        String cookies = request.getHeader("Cookie");
        System.err.println("cookies: " + cookies + " (" + request.getUrl() + ")");

        if (cookies == null) {
            responseBuilder.withHeader("Set-Cookie", "JSESSIONID=" + sessionId);
        }

    }

}
