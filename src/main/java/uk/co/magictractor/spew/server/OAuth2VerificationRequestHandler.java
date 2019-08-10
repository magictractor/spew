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
public class OAuth2VerificationRequestHandler implements RequestHandler {

    private final Supplier<VerificationFunction> verificationFunctionSupplier;

    public OAuth2VerificationRequestHandler(Supplier<VerificationFunction> verificationFunctionSupplier) {
        this.verificationFunctionSupplier = verificationFunctionSupplier;
    }

    @Override
    public void handleRequest(ServerRequest request, SimpleResponseBuilder responseBuilder) {
        String baseUrl = request.getBaseUrl();
        if (!baseUrl.contentEquals("/")) {
            return;
        }

        System.err.println("request: " + request);

        String code = request.getQueryStringParam("code");

        if (code == null) {
            throw new IllegalArgumentException("Expected code in the authorization response");
        }
        System.err.println("code: " + code);

        VerificationInfo verificationInfo = new VerificationInfo(code);
        boolean verified = verificationFunctionSupplier.get().apply(verificationInfo);

        // TODO! change these to templates and add some info about the application / service provider
        // TODO! common code with OAuth1VerificationRequestHandler
        if (verified) {
            responseBuilder.withRedirect("/verificationSuccessful.html");
        }
        else {
            responseBuilder.withRedirect("/verificationFailed.html");
        }
    }

}
