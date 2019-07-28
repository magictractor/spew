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

    private ResponseNext callback(ServerRequest request) {
        // /?oauth_token=72157709917193752-d6882db0292b43fa&oauth_verifier=1fb13c265dc4d505
        //KeyValuePairsResponse parsedResponse = new KeyValuePairsResponse(response);
        authToken = request.getQueryStringParam("oauth_token");
        authVerifier = request.getQueryStringParam("oauth_verifier");

        if (authToken == null || authVerifier == null) {
            throw new IllegalArgumentException("Expected values were missing from authorization response");
        }

        return ResponseNext.redirect("/hiya");
    }

}
