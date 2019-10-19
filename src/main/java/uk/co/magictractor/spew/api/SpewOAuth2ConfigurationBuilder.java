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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import uk.co.magictractor.spew.store.application.ApplicationPropertyStore;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class SpewOAuth2ConfigurationBuilder {

    private SpewOAuth2ConfigurationImpl configuration = new SpewOAuth2ConfigurationImpl();
    private boolean done;

    public SpewOAuth2Configuration build() {
        done = true;

        return configuration;
    }

    public Supplier<SpewOAuth2Configuration> nextBuild() {
        return () -> {
            if (!done) {
                throw new IllegalStateException("Not built yet");
            }
            return configuration;
        };
    }

    public SpewOAuth2ConfigurationBuilder withServiceProvider(SpewOAuth2ServiceProvider serviceProvider) {
        if (configuration.authorizationUri == null) {
            configuration.authorizationUri = serviceProvider.oauth2AuthorizationUri();
        }
        if (configuration.tokenUri == null) {
            configuration.tokenUri = serviceProvider.oauth2TokenUri();
        }

        return this;
    }

    public SpewOAuth2ConfigurationBuilder withApplication(SpewOAuth2Application<?> application) {
        if (configuration.clientId == null) {
            // TODO! firstNonNull would be better
            configuration.clientId = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
                    .getProperty(application, "client_id");
        }
        if (configuration.clientSecret == null) {
            configuration.clientSecret = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
                    .getProperty(application, "client_secret");
        }
        if (configuration.scope == null) {
            configuration.scope = application.getScope();
        }

        withProperties(application.getProperties());

        return this;
    }

    public SpewOAuth2ConfigurationBuilder withProperties(Map<String, Object> properties) {
        configuration.properties.putAll(properties);
        return this;
    }

    private static final class SpewOAuth2ConfigurationImpl implements SpewOAuth2Configuration {

        private String clientId;
        private String clientSecret;
        private String authorizationUri;
        private String tokenUri;
        private String scope;
        private Map<String, Object> properties = new LinkedHashMap<>();

        @Override
        public String getClientId() {
            return clientId;
        }

        @Override
        public String getClientSecret() {
            return clientSecret;
        }

        @Override
        public String getAuthorizationUri() {
            return authorizationUri;
        }

        @Override
        public String getTokenUri() {
            return tokenUri;
        }

        @Override
        public String getScope() {
            return scope;
        }

        @Override
        public void addProperties(Map<String, Object> properties) {
            properties.putAll(this.properties);
        }

    }

}
