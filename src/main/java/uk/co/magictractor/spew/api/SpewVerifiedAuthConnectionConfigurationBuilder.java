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

import uk.co.magictractor.spew.api.SpewVerifiedAuthConnectionConfigurationBuilder.SpewVerifiedAuthConnectionConfigurationImpl;

/**
 *
 */
public abstract class SpewVerifiedAuthConnectionConfigurationBuilder<CONFIG extends SpewVerifiedAuthConnectionConfiguration, IMPL extends SpewVerifiedAuthConnectionConfigurationImpl, APP extends SpewApplication<?>, BUILDER extends SpewVerifiedAuthConnectionConfigurationBuilder<CONFIG, IMPL, APP, BUILDER>>
        extends SpewConnectionConfigurationBuilder<CONFIG, IMPL, APP, BUILDER> {

    @SuppressWarnings("unchecked")
    @Override
    public BUILDER withServiceProvider(SpewServiceProvider serviceProvider) {
        super.withServiceProvider(serviceProvider);
        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BUILDER withApplication(APP application) {
        super.withApplication(application);
        return (BUILDER) this;
    }

    public BUILDER withOutOfBandUri(String outOfBandUri) {
        SpewVerifiedAuthConnectionConfigurationImpl configuration = configuration();
        configuration.outOfBandUri = outOfBandUri;
        return (BUILDER) this;
    }

    public static abstract class SpewVerifiedAuthConnectionConfigurationImpl
            extends SpewConnectionConfigurationBuilder.SpewConnectionConfigurationImpl
            implements SpewVerifiedAuthConnectionConfiguration {

        private String outOfBandUri;

        @Override
        public String getOutOfBandUri() {
            return outOfBandUri;
        }

    }

}
