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
package uk.co.magictractor.spew.dropbox;

import java.util.function.Supplier;

import uk.co.magictractor.spew.access.AuthorizationHandler;
import uk.co.magictractor.spew.api.OAuth2Application;
import uk.co.magictractor.spew.api.OAuth2ServiceProvider;
import uk.co.magictractor.spew.api.VerificationFunction;
import uk.co.magictractor.spew.processor.properties.ResourceFileProperties;
import uk.co.magictractor.spew.server.LocalServerAuthorizationHandler;

public class MyDropboxApp implements OAuth2Application {

    private final ResourceFileProperties properties = new ResourceFileProperties(getClass());

    @Override
    public OAuth2ServiceProvider getServiceProvider() {
        return Dropbox.getInstance();
    }

    @Override
    public String getClientId() {
        return properties.getProperty("client_id");
    }

    @Override
    public String getClientSecret() {
        return properties.getProperty("client_secret");
    }

    @Override
    public AuthorizationHandler getAuthorizationHandler(Supplier<VerificationFunction> verificationFunctionSupplier) {
        //return new PasteVerificationCodeHandler(verificationFunction);
        return new LocalServerAuthorizationHandler(verificationFunctionSupplier);
    }

}
