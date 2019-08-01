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

public class SimpleResponseBuilder {

    private SimpleResponse response;
    private boolean isDone;
    private boolean shutdown;

    public SimpleResponse build() {
        return response;
    }

    public SimpleResponseBuilder withResponse(SimpleResponse response) {
        if (this.response != null) {
            throw new IllegalStateException("A response has already been set");
        }
        if (response == null) {
            // Most likely a non-existant static resource.
            return this;
        }

        this.response = response;
        isDone = true;
        return this;
    }

    public SimpleResponseBuilder withStaticIfExists(Class<?> relativeToClass, String resourceName) {
        return withResponse(SimpleStaticResponse.ifExists(relativeToClass, resourceName));
    }

    public SimpleResponseBuilder withRedirect(String location) {
        return withResponse(new SimpleRedirectResponse(location));
    }

    public SimpleResponseBuilder withShutdown() {
        this.shutdown = true;
        return this;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public boolean isDone() {
        return isDone;
    }

}
