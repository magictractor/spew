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

import uk.co.magictractor.spew.core.response.ResourceResponse;

public class ResourceResponseHandler implements ResponseHandler {

    private final Class<?> relativeToClass;

    public ResourceResponseHandler() {
        relativeToClass = null;
    }

    public ResourceResponseHandler(Class<?> relativeToClass) {
        this.relativeToClass = relativeToClass;
    }

    @Override
    public ResponseNext handleResponse(ServerRequest request) {
        // DO NOT COMMIT  - testing exception handling
        if (1 < 2) {
            throw new IllegalStateException("big badda boom");
        }

        String baseUrl = request.getBaseUrl();
        if (!baseUrl.startsWith("/")) {
            throw new IllegalArgumentException("BaseUrl does not start with a slash");
        }
        String resourceName = baseUrl.substring(1);

        System.err.println("base url: " + baseUrl);
        ResourceResponse response = new ResourceResponse(relativeToClass, resourceName);
        if (!response.exists()) {
            System.err.println("Resource not found for " + baseUrl);
            return null;
        }
        return ResponseNext.response(response);
    }

}
