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

import uk.co.magictractor.spew.access.AbstractAuthorizationHandler;
import uk.co.magictractor.spew.api.HasCallbackServer;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.VerificationFunction;
import uk.co.magictractor.spew.flickr.MyFlickrApp;
import uk.co.magictractor.spew.server.netty.NettyCallbackServer;

/**
 *
 */
public class LocalServerAuthorizationHandler extends AbstractAuthorizationHandler {

    private NettyCallbackServer server;

    public LocalServerAuthorizationHandler(VerificationFunction verificationFunction) {
        super(verificationFunction);
    }

    @Override
    public void preOpenAuthorizationInBrowser(SpewApplication application) {
        if (!HasCallbackServer.class.isInstance(application)) {
            throw new IllegalArgumentException(
                "Application should implement HasCallbackServer if it can have authorization callbacks");
        }
        HasCallbackServer hasCallbackServer = (HasCallbackServer) application;

        // TODO! could allow other implementations of callback server
        server = new NettyCallbackServer(hasCallbackServer.getServerRequestHandlers(verificationFunction()), null,
            8080);
        server.run();
    }

    @Override
    public String getCallbackValue() {
        return server.getUrl();
    }

    @Override
    public void postOpenAuthorizationInBrowser(SpewApplication application) {
        // Wait until the server shuts down, hopefully after it has served a successful verification page.
        server.join();
    }

    // DO NOT COMMIT
    // temp for testing static pages
    public static void main(String[] args) {
        LocalServerAuthorizationHandler handler = new LocalServerAuthorizationHandler((a, v) -> true);
        handler.preOpenAuthorizationInBrowser(new MyFlickrApp());
    }

}
