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
package uk.co.magictractor.spew.example.dropbox;

import java.util.function.Supplier;

import uk.co.magictractor.spew.api.SpewOAuth2Application;
import uk.co.magictractor.spew.core.verification.AuthorizationHandler;
import uk.co.magictractor.spew.core.verification.VerificationFunction;
import uk.co.magictractor.spew.provider.dropbox.Dropbox;
import uk.co.magictractor.spew.server.LocalServerAuthorizationHandler;

public class MyDropboxApp implements SpewOAuth2Application<Dropbox> {

    @Override
    public Dropbox getServiceProvider() {
        return Dropbox.getInstance();
    }

    @Override
    public AuthorizationHandler getAuthorizationHandler(Supplier<VerificationFunction> verificationFunctionSupplier) {
        //return new PasteVerificationCodeHandler(verificationFunction);
        return new LocalServerAuthorizationHandler(verificationFunctionSupplier);
    }

}
