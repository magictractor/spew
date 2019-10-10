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

import uk.co.magictractor.spew.store.ApplicationPropertyStore;
import uk.co.magictractor.spew.store.EditableProperty;
import uk.co.magictractor.spew.store.UserPropertyStore;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class SpewOAuth1ConfigurationBuilder {

    private SpewOAuth1ConfigurationImpl configuration = new SpewOAuth1ConfigurationImpl();

    public SpewOAuth1Configuration build() {
        return configuration;
    }

    public SpewOAuth1ConfigurationBuilder withApplication(SpewOAuth1Application<?> application) {
        if (configuration.consumerKey == null) {
            configuration.consumerKey = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
                    .getProperty(application, "consumer_key");
        }
        if (configuration.consumerSecret == null) {
            configuration.consumerSecret = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
                    .getProperty(application, "consumer_secret");
        }
        if (configuration.userTokenProperty == null) {
            configuration.userTokenProperty = SPIUtil.firstAvailable(UserPropertyStore.class)
                    .getProperty(application, "user_token");
        }
        if (configuration.userSecretProperty == null) {
            configuration.userSecretProperty = SPIUtil.firstAvailable(UserPropertyStore.class)
                    .getProperty(application, "user_secret");
        }

        if (configuration.temporaryCredentialRequestUri == null) {
            configuration.temporaryCredentialRequestUri = application.getTemporaryCredentialRequestUri();
        }
        if (configuration.resourceOwnerAuthorizationUri == null) {
            configuration.resourceOwnerAuthorizationUri = application.getResourceOwnerAuthorizationUri();
        }
        if (configuration.tokenRequestUri == null) {
            configuration.tokenRequestUri = application.getTokenRequestUri();
        }
        if (configuration.requestSignatureMethod == null) {
            configuration.requestSignatureMethod = application.getRequestSignatureMethod();
        }
        if (configuration.javaSignatureMethod == null) {
            configuration.javaSignatureMethod = application.getJavaSignatureMethod();
        }

        return this;
    }

    private static final class SpewOAuth1ConfigurationImpl implements SpewOAuth1Configuration {

        private String consumerKey;
        private String consumerSecret;
        private EditableProperty userTokenProperty;
        private EditableProperty userSecretProperty;
        private String temporaryCredentialRequestUri;
        private String resourceOwnerAuthorizationUri;
        private String tokenRequestUri;
        private String requestSignatureMethod;
        private String javaSignatureMethod;

        @Override
        public String getConsumerKey() {
            return consumerKey;
        }

        @Override
        public String getConsumerSecret() {
            return consumerSecret;
        }

        @Override
        public EditableProperty getUserTokenProperty() {
            return userTokenProperty;
        }

        @Override
        public EditableProperty getUserSecretProperty() {
            return userSecretProperty;
        }

        @Override
        public String getTemporaryCredentialRequestUri() {
            return temporaryCredentialRequestUri;
        }

        @Override
        public String getResourceOwnerAuthorizationUri() {
            return resourceOwnerAuthorizationUri;
        }

        @Override
        public String getTokenRequestUri() {
            return tokenRequestUri;
        }

        @Override
        public String getRequestSignatureMethod() {
            return requestSignatureMethod;
        }

        @Override
        public String getJavaSignatureMethod() {
            return javaSignatureMethod;
        }

    }

}
