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
package uk.co.magictractor.spew.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import uk.co.magictractor.spew.api.SpewVerifiedAuthConnectionConfigurationBuilder.SpewVerifiedAuthConnectionConfigurationImpl;
import uk.co.magictractor.spew.core.verification.AuthVerificationHandler;

/**
 *
 */
public abstract class SpewVerifiedAuthConnectionConfigurationBuilder<CONFIG extends SpewVerifiedAuthConnectionConfiguration, IMPL extends SpewVerifiedAuthConnectionConfigurationImpl, APP extends SpewApplication<SP>, SP extends SpewServiceProvider, BUILDER extends SpewVerifiedAuthConnectionConfigurationBuilder<CONFIG, IMPL, APP, SP, BUILDER>>
        extends SpewConnectionConfigurationBuilder<CONFIG, IMPL, APP, SP, BUILDER> {

    private boolean isDefaultVerificationHandlerTypes = true;

    public BUILDER withOutOfBandUri(String outOfBandUri) {
        SpewVerifiedAuthConnectionConfigurationImpl configuration = configuration();
        configuration.outOfBandUri = outOfBandUri;
        return (BUILDER) this;
    }

    public BUILDER withoutVerificationHandlerType(String verificationHandlerType) {
        SpewVerifiedAuthConnectionConfigurationImpl configuration = configuration();
        ensureModifiableVerificationHandlerTypes();
        configuration.verificationHandlerTypes.remove(verificationHandlerType);
        return (BUILDER) this;
    }

    /**
     * Verification handler types are generally left unchanged, so the list of
     * types is initially the unmodifiable constant.
     */
    private void ensureModifiableVerificationHandlerTypes() {
        if (isDefaultVerificationHandlerTypes) {
            SpewVerifiedAuthConnectionConfigurationImpl configuration = configuration();
            configuration.verificationHandlerTypes = new ArrayList<>(configuration.verificationHandlerTypes);
            isDefaultVerificationHandlerTypes = false;
        }
    }

    public static abstract class SpewVerifiedAuthConnectionConfigurationImpl
            extends SpewConnectionConfigurationBuilder.SpewConnectionConfigurationImpl
            implements SpewVerifiedAuthConnectionConfiguration {

        private List<String> verificationHandlerTypes = AuthVerificationHandler.DEFAULT_AUTH_VERIFICATION_TYPES;
        private Function<SpewVerifiedAuthConnectionConfiguration, AuthVerificationHandler> verificationHandlerFunction = AuthVerificationHandler::instanceFor;
        private String outOfBandUri;

        @Override
        public List<String> getVerificationHandlerTypes() {
            return verificationHandlerTypes;
        }

        @Override
        public AuthVerificationHandler createAuthVerificationHandler() {
            return verificationHandlerFunction.apply(this);
        }

        @Override
        public String getOutOfBandUri() {
            return outOfBandUri;
        }

    }

}
