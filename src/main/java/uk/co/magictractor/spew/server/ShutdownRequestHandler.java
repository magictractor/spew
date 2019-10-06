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

import static uk.co.magictractor.spew.api.HttpHeaderNames.CACHE_CONTROL;

import java.util.Optional;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewApplicationCache;

public class ShutdownRequestHandler implements RequestHandler {

    private final String shutdownPath;

    public ShutdownRequestHandler(String shutdownPath) {
        this.shutdownPath = shutdownPath;
    }

    @Override
    public void handleRequest(SpewHttpRequest request, OutgoingResponseBuilder responseBuilder) {
        String path = request.getPath();
        if (path.equals(shutdownPath)) {
            Optional<String> applicationId = request.getQueryStringParam("app");
            if (applicationId.isPresent()) {
                SpewApplication<?> application = SpewApplicationCache.get(applicationId.get());
                if (application != null) {
                    responseBuilder.withShutdown();
                }
            }
        }
        // Make sure it isn't cached, see also Cache-Control for templates
        responseBuilder.withHeader(CACHE_CONTROL, "no-cache, must-revalidate, max-age=0");
    }

}
