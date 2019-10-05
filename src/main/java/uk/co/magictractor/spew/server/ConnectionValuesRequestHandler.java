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

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.connection.SpewConnectionCache;

public class ConnectionValuesRequestHandler implements RequestHandler {

    @Override
    public void handleRequest(SpewHttpRequest request, OutgoingResponseBuilder responseBuilder) {
        Optional<String> connectionId = request.getQueryStringParam("connection");
        if (!connectionId.isPresent()) {
            return;
        }

        Optional<SpewConnection> connection = SpewConnectionCache.getConnection(connectionId.get());

        if (connection.isPresent()) {
            responseBuilder.withValueFunction(new ConnectionValueFunction(connection.get()));
        }
    }

    private final class ConnectionValueFunction implements Function<String, String> {

        private final SpewConnection connection;

        public ConnectionValueFunction(SpewConnection connection) {
            this.connection = connection;
        }

        @Override
        public String apply(String key) {
            if ("app.name".equals(key)) {
                return connection.getApplication().getName();
            }
            else if ("sp.name".equals(key)) {
                return connection.getApplication().getServiceProvider().getName();
            }
            return null;
        }

    }
}
