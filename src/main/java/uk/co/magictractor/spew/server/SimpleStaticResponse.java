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

import java.io.InputStream;

import uk.co.magictractor.spew.core.response.ResourceResponse;

/**
 * Simple representation of a static page.
 */
// TODO! last modified time - available via Files
public class SimpleStaticResponse extends SimpleResponse implements SimpleResourceResponse {

    private final ResourceResponse spewResponse;

    public static SimpleStaticResponse ifExists(Class<?> relativeToClass, String resourceName) {
        ResourceResponse resourceResponse = new ResourceResponse(relativeToClass, resourceName);
        return resourceResponse.exists() ? new SimpleStaticResponse(resourceResponse) : null;
    }

    public SimpleStaticResponse(ResourceResponse spewResponse) {
        if (!spewResponse.exists()) {
            throw new IllegalArgumentException("Resource does not exist");
        }
        this.spewResponse = spewResponse;
    }

    @Override
    public String getHeader(String headerName) {
        return spewResponse.getHeader(headerName);
    }

    @Override
    public InputStream getBodyInputStream() {
        return spewResponse.getBodyInputStream();
    }

}
