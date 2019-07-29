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

import uk.co.magictractor.spew.api.VerificationFunction;

/**
 *
 */
public class OAuth1VerificationRequestHandler implements RequestHandler {

    private final VerificationFunction verificationFunction;

    public OAuth1VerificationRequestHandler(VerificationFunction verificationFunction) {
        this.verificationFunction = verificationFunction;
    }

    @Override
    public SimpleResponse handleRequest(ServerRequest request) {
        String baseUrl = request.getBaseUrl();
        if (!baseUrl.contentEquals("/")) {
            return null;
        }

        String authToken = request.getQueryStringParam("oauth_token");
        String authVerifier = request.getQueryStringParam("oauth_verifier");

        if (authToken == null || authVerifier == null) {
            throw new IllegalArgumentException("Expected values were missing from authorization response");
        }

        boolean verified = verificationFunction.apply(authToken, authVerifier);

        // TODO! change these to templates and add some info about the application / service provider
        if (verified) {
            return new SimpleRedirectResponse("/verificationSuccessful.html");
        }
        else {
            return new SimpleRedirectResponse("/verificationFailed.html");
        }
    }

}
