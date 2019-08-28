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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import uk.co.magictractor.spew.api.SpewHeader;

public class OutgoingResponseBuilder {

    private OutgoingResponse response;
    private boolean isDone;
    private boolean shutdown;
    private List<SpewHeader> headers;
    private List<Function<String, String>> valueFunctions;

    public OutgoingResponse build() {
        if (response == null && !shutdown) {
            response = OutgoingErrorResponse.notFound();
        }

        if (headers != null) {
            response.addHeaders(headers);
        }

        return response;
    }

    public OutgoingResponseBuilder withResponse(OutgoingResponse response) {
        if (this.response != null) {
            throw new IllegalStateException("A response has already been set");
        }
        if (response == null) {
            // Most likely a non-existent static resource.
            return this;
        }

        this.response = response;
        isDone = true;
        return this;
    }

    public OutgoingResponseBuilder withStaticIfExists(Class<?> relativeToClass, String resourceName) {
        return withResponse(OutgoingStaticResponse.ifExists(relativeToClass, resourceName));
    }

    public OutgoingResponseBuilder withTemplateIfExists(Class<?> relativeToClass, String resourceName) {
        return withResponse(OutgoingTemplateResponse.ifExists(relativeToClass, resourceName, this::getValue));
    }

    public OutgoingResponseBuilder withRedirect(String location) {
        return withResponse(new OutgoingRedirectResponse(location));
    }

    public OutgoingResponseBuilder withHeader(String name, String value) {
        if (headers == null) {
            headers = new ArrayList<>();
        }
        headers.add(new SpewHeader(name, value));

        return this;
    }

    public OutgoingResponseBuilder withValueFunction(Function<String, String> valueFunction) {
        if (valueFunctions == null) {
            valueFunctions = new ArrayList<>();
        }
        valueFunctions.add(valueFunction);

        return this;
    }

    public OutgoingResponseBuilder withShutdown() {
        this.shutdown = true;
        return this;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public boolean isDone() {
        return isDone;
    }

    private String getValue(String key) {
        if (valueFunctions != null) {
            for (Function<String, String> valueFunction : valueFunctions) {
                String value = valueFunction.apply(key);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        ToStringHelper helper = MoreObjects.toStringHelper(getClass())
                .add("response", response);
        if (isDone) {
            helper.add("isDone", true);
        }
        if (isShutdown()) {
            helper.add("shutdown", true);
        }
        if (headers != null) {
            helper.add("headers", headers);
        }
        if (valueFunctions != null) {
            helper.add("valueFunctions.size", valueFunctions.size());
        }

        return helper.toString();
    }

}
