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

import java.util.Optional;
import java.util.function.Function;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewApplicationCache;

public class ApplicationValuesRequestHandler implements RequestHandler {

    @Override
    public void handleRequest(SpewHttpRequest request, OutgoingResponseBuilder responseBuilder) {

        Optional<String> applicationId = request.getQueryStringParam("app");
        if (!applicationId.isPresent()) {
            return;
        }

        SpewApplication<?> application = SpewApplicationCache.get(applicationId.get());

        if (application != null) {
            responseBuilder.withValueFunction(new ApplicationValueFunction(application));
        }

        //temp
        responseBuilder.withValueFunction((x) -> "t".equals(x) ? "ttt" : null);
    }

    private final class ApplicationValueFunction implements Function<String, Object> {

        private final SpewApplication<?> application;

        public ApplicationValueFunction(SpewApplication<?> application) {
            this.application = application;
        }

        @Override
        public Object apply(String key) {
            // Hmm... in future the key could contain hints how how to render it,
            // e.g. regular table vs div/div/span grid
            if ("app.props".equals(key)) {
                return application.getProperties();
            }
            return null;
        }

    }
}
