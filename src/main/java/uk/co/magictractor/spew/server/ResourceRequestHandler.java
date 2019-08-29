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

import java.nio.file.Path;
import java.util.stream.Collectors;

import uk.co.magictractor.spew.util.PathUtil;

// TODO! rename
public class ResourceRequestHandler implements RequestHandler {

    private final Class<?> relativeToClass;

    public ResourceRequestHandler() {
        relativeToClass = null;
    }

    public ResourceRequestHandler(Class<?> relativeToClass) {
        this.relativeToClass = relativeToClass;
    }

    @Override
    public void handleRequest(SpewHttpRequest request, OutgoingResponseBuilder responseBuilder) {
        // DO NOT COMMIT  - testing exception handling
        if (1 < 2) {
            // throw new IllegalStateException("big badda boom");
        }

        String path = request.getPath();
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("path does not start with a slash");
        }
        String resourceName = path.substring(1);

        Path bodyPath = PathUtil.ifExistsRegularFile(relativeToClass, resourceName);
        if (bodyPath == null) {
            return;
        }

        System.err.println(request.getHttpMethod() + " " + request.getPath() + " has headers "
                + request.getHeaders()
                        .stream()
                        .map(h -> h.getName() + "=" + h.getValue())
                        .collect(Collectors.toList()));

        String ifModifiedSince = request.getHeader("If-Modified-Since");
        OutgoingResponse response;
        if (ifModifiedSince != null) {
            PathUtil.getLastModified(bodyPath);
            // TODO! compare timestamps
            response = OutgoingNotModifiedResponse.getInstance();
        }
        else {
            response = new OutgoingStaticResponse(bodyPath);
        }

        responseBuilder.withResponse(response);
    }

}
