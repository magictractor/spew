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

import uk.co.magictractor.spew.api.HasCallbackServer;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.verification.AbstractAuthorizationHandler;
import uk.co.magictractor.spew.core.verification.VerificationFunction;
import uk.co.magictractor.spew.example.flickr.MyFlickrApp;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class LocalServerAuthorizationHandler extends AbstractAuthorizationHandler {

    private CallbackServer server;
    private String callback;

    public LocalServerAuthorizationHandler(Supplier<VerificationFunction> verificationFunctionSupplier) {
        super(verificationFunctionSupplier);
    }

    @Override
    public void preOpenAuthorizationInBrowser(SpewApplication application) {
        if (!HasCallbackServer.class.isInstance(application)) {
            throw new IllegalArgumentException(
                "Application should implement HasCallbackServer if it can have authorization callbacks");
        }
        HasCallbackServer hasCallbackServer = (HasCallbackServer) application;
        callback = hasCallbackServer.protocol() + "://" + hasCallbackServer.host() + ":" + hasCallbackServer.port();

        server = SPIUtil.firstAvailable(CallbackServer.class);
        server.run(hasCallbackServer.getServerRequestHandlers(verificationFunctionSupplier()),
            hasCallbackServer.port());
    }

    @Override
    public String getCallbackValue() {
        if (callback == null) {
            throw new IllegalStateException(
                "preOpenAuthorizationInBrowser() should be called before getCallbackValue()");
        }
        return callback;
    }

    @Override
    public void postOpenAuthorizationInBrowser(SpewApplication application) {
        // Wait until the server shuts down, hopefully after it has served a successful verification page.
        // TODO! how to ensure the server gets shutdown... maybe add postValidation() too
        server.join();
    }

    // DO NOT COMMIT
    // temp for testing static pages
    public static void main(String[] args) {
        LocalServerAuthorizationHandler handler = new LocalServerAuthorizationHandler(() -> (info -> true));
        handler.preOpenAuthorizationInBrowser(new MyFlickrApp());
    }

}
