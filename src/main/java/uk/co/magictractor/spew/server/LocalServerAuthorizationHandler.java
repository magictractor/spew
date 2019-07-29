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

import uk.co.magictractor.spew.access.AuthorizationHandler;
import uk.co.magictractor.spew.access.AuthorizationResult;
import uk.co.magictractor.spew.server.netty.NettyCallbackServer;

/**
 *
 */
public class LocalServerAuthorizationHandler implements AuthorizationHandler {

    private NettyCallbackServer server;
    private String authToken;
    private String authVerifier;

    @Override
    public void preAuthorizationRequest() {
        // TODO! could allow other implementations of callback server
        server = new NettyCallbackServer(null);
        server.addResponseHandler(new ResourceResponseHandler(this.getClass()));
        server.addResponseHandler(this::callback);
        server.run();
    }

    @Override
    public String getCallbackValue() {
        return server.getUrl();
    }

    // TODO! no - should fetch the token immediately so we know whether there's a problem before redirecting
    // hmm, that's on the connection...
    @Override
    public AuthorizationResult getResult() {
        server.join();
        return new AuthorizationResult(authToken, authVerifier);
    }

    private SimpleResponse callback(ServerRequest request) {
        String baseUrl = request.getBaseUrl();
        if (!baseUrl.contentEquals("/")) {
            return null;
        }

        authToken = request.getQueryStringParam("oauth_token");
        authVerifier = request.getQueryStringParam("oauth_verifier");

        if (authToken == null || authVerifier == null) {
            throw new IllegalArgumentException("Expected values were missing from authorization response");
        }

        return new SimpleRedirectResponse("/verificationSuccessful.html");
    }

    // DO NOT COMMIT
    // temp for testing static pages
    public static void main(String[] args) {
        LocalServerAuthorizationHandler handler = new LocalServerAuthorizationHandler();
        handler.preAuthorizationRequest();
    }

}
