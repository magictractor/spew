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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.SpewAuthorizationVerifiedConnection;
import uk.co.magictractor.spew.api.connection.SpewConnectionVerificationPendingCache;

/**
 *
 */
public class AuthVerificationRequestHandler implements RequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthVerificationRequestHandler.class);

    @Override
    public void handleRequest(SpewHttpRequest request, OutgoingResponseBuilder responseBuilder) {
        String path = request.getPath();
        if (!path.equals("/")) {
            return;
        }

        Optional<SpewAuthorizationVerifiedConnection> connectionOpt = SpewConnectionVerificationPendingCache
                .removeVerificationPending(request);

        if (connectionOpt.isEmpty()) {
            LOGGER.warn("Missing application pending verification for request {}", request);
            return;
        }

        SpewAuthorizationVerifiedConnection connection = connectionOpt.get();
        boolean verified = connection.verifyAuthorization(request);

        StringBuilder urlBuilder = new StringBuilder();
        if (verified) {
            urlBuilder.append("/verificationSuccessful.html");
        }
        else {
            urlBuilder.append("/verificationFailed.html");
        }
        urlBuilder.append("?conn=");
        urlBuilder.append(connection.getId());

        responseBuilder.withRedirect(urlBuilder.toString());
    }

}
