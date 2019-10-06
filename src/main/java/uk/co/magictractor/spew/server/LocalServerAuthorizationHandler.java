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

import uk.co.magictractor.spew.api.HasCallbackServer;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.verification.AuthorizationHandler;
import uk.co.magictractor.spew.example.flickr.MyFlickrApp;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class LocalServerAuthorizationHandler implements AuthorizationHandler {

    private final HasCallbackServer applicationWithCallbackServer;
    // TODO! server could be static, maybe not here, and could serve other applications/pages while running?
    private CallbackServer server;

    public LocalServerAuthorizationHandler(SpewApplication<?> application) {
        if (!HasCallbackServer.class.isInstance(application)) {
            throw new IllegalArgumentException(
                "Application should implement HasCallbackServer if it can have authorization callbacks");
        }
        this.applicationWithCallbackServer = (HasCallbackServer) application;
    }

    @Override
    public void preOpenAuthorizationInBrowser() {
        server = SPIUtil.firstAvailable(CallbackServer.class);
        server.run(applicationWithCallbackServer.getServerRequestHandlers(), applicationWithCallbackServer.port());
    }

    @Override
    public String getRedirectUri() {
        return applicationWithCallbackServer.uri();
    }

    @Override
    public void postOpenAuthorizationInBrowser() {
        // Wait until the server shuts down, hopefully after it has served a successful verification page.
        // TODO! how to ensure the server gets shutdown... maybe add postValidation() too
        server.join();
    }

    // DO NOT COMMIT
    // temp for testing static pages
    public static void main(String[] args) {
        LocalServerAuthorizationHandler handler = new LocalServerAuthorizationHandler(MyFlickrApp.get());
        handler.preOpenAuthorizationInBrowser();
    }

}
