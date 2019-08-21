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

import java.util.function.Supplier;

import uk.co.magictractor.spew.core.verification.VerificationFunction;
import uk.co.magictractor.spew.core.verification.VerificationInfo;

/**
 *
 */
public class OAuth1VerificationRequestHandler implements RequestHandler {

    private final Supplier<VerificationFunction> verificationFunctionSupplier;

    public OAuth1VerificationRequestHandler(Supplier<VerificationFunction> verificationFunctionSupplier) {
        this.verificationFunctionSupplier = verificationFunctionSupplier;
    }

    @Override
    public void handleRequest(SpewHttpRequest request, OutgoingResponseBuilder responseBuilder) {
        String path = request.getPath();
        if (!path.equals("/")) {
            return;
        }

        String authToken = request.getQueryStringParam("oauth_token").orElse(null);
        String authVerifier = request.getQueryStringParam("oauth_verifier").orElse(null);

        if (authToken == null || authVerifier == null) {
            throw new IllegalArgumentException("Expected values were missing from authorization response");
        }

        // TODO! check whether the authToken changes or is repeated
        VerificationInfo verificationInfo = new VerificationInfo(authVerifier).withAuthToken(authToken);
        VerificationFunction verificationFunction = verificationFunctionSupplier.get();
        boolean verified = verificationFunctionSupplier.get().apply(verificationInfo);

        StringBuilder urlBuilder = new StringBuilder();
        if (verified) {
            urlBuilder.append("/verificationSuccessful.html");
        }
        else {
            urlBuilder.append("/verificationFailed.html");
        }
        urlBuilder.append("?connection=");
        urlBuilder.append(verificationFunction.getConnection().getId());

        responseBuilder.withRedirect(urlBuilder.toString());
    }

}
