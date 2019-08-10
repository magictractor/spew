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
    public void handleRequest(ServerRequest request, SimpleResponseBuilder responseBuilder) {
        String baseUrl = request.getBaseUrl();
        if (!baseUrl.contentEquals("/")) {
            return;
        }

        String authToken = request.getQueryStringParam("oauth_token");
        String authVerifier = request.getQueryStringParam("oauth_verifier");

        if (authToken == null || authVerifier == null) {
            throw new IllegalArgumentException("Expected values were missing from authorization response");
        }

        // TODO! check whether the authToken changes or is repeated
        VerificationInfo verificationInfo = new VerificationInfo(authVerifier).withAuthToken(authToken);
        boolean verified = verificationFunctionSupplier.get().apply(verificationInfo);

        // TODO! change these to templates and add some info about the application / service provider
        if (verified) {
            responseBuilder.withRedirect("/verificationSuccessful.html");
        }
        else {
            responseBuilder.withRedirect("/verificationFailed.html");
        }
    }

}
