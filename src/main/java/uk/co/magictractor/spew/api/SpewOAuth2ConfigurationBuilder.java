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
import java.util.Map;

import uk.co.magictractor.spew.api.SpewOAuth2Configuration.AuthRequestModifier;
import uk.co.magictractor.spew.api.SpewOAuth2Configuration.TokenRequestModifier;
import uk.co.magictractor.spew.api.SpewOAuth2ConfigurationBuilder.SpewOAuth2ConfigurationImpl;
import uk.co.magictractor.spew.store.EditableProperty;
import uk.co.magictractor.spew.store.application.ApplicationPropertyStore;
import uk.co.magictractor.spew.store.user.UserPropertyStore;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class SpewOAuth2ConfigurationBuilder
        extends
        SpewVerifiedAuthConnectionConfigurationBuilder<SpewOAuth2Configuration, SpewOAuth2ConfigurationImpl, SpewOAuth2Application<SpewOAuth2ServiceProvider>, SpewOAuth2ServiceProvider, SpewOAuth2ConfigurationBuilder> {

    public SpewOAuth2ConfigurationBuilder() {
        // out-of-band isn't in the spec, but is supported by Google and other
        // https://mailarchive.ietf.org/arch/msg/oauth/OCeJLZCEtNb170Xy-C3uTVDIYjM
        withOutOfBandUri("urn:ietf:wg:oauth:2.0:oob");
    }

    @Override
    public SpewOAuth2ConfigurationImpl newInstance() {
        return new SpewOAuth2ConfigurationImpl();
    }

    @Override
    public SpewOAuth2ConfigurationBuilder withServiceProvider(SpewOAuth2ServiceProvider serviceProvider) {
        super.withServiceProvider(serviceProvider);

        serviceProvider.initConnectionConfigurationBuilder(this);

        SpewOAuth2ConfigurationImpl configuration = configuration();

        if (configuration.authorizationUri == null) {
            configuration.authorizationUri = serviceProvider.oauth2AuthorizationUri();
        }
        if (configuration.tokenUri == null) {
            configuration.tokenUri = serviceProvider.oauth2TokenUri();
        }

        return this;
    }

    @Override
    public SpewOAuth2ConfigurationBuilder withApplication(SpewOAuth2Application application) {
        super.withApplication(application);

        application.initConnectionConfigurationBuilder(this);

        SpewOAuth2ConfigurationImpl configuration = configuration();

        if (configuration.clientId == null) {
            // TODO! firstNonNull would be better
            configuration.clientId = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
                    .getProperty(application, "client_id");
        }
        if (configuration.clientSecret == null) {
            configuration.clientSecret = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
                    .getProperty(application, "client_secret");
        }

        if (configuration.accessTokenProperty == null) {
            configuration.accessTokenProperty = SPIUtil.firstAvailable(UserPropertyStore.class)
                    .getProperty(application, "access_token");
        }
        if (configuration.accessTokenExpiryProperty == null) {
            configuration.accessTokenExpiryProperty = SPIUtil.firstAvailable(UserPropertyStore.class)
                    .getProperty(application, "access_token_expiry");
        }
        if (configuration.refreshTokenProperty == null) {
            configuration.refreshTokenProperty = SPIUtil.firstAvailable(UserPropertyStore.class)
                    .getProperty(application, "refresh_token");
        }

        if (configuration.scope == null) {
            configuration.scope = application.getScope();
        }

        withProperties(application.getProperties());

        return this;
    }

    public void withModifyAuthRequest(AuthRequestModifier modifier) {
        SpewOAuth2ConfigurationImpl configuration = configuration();

        if (configuration.authRequestModifiers == null) {
            configuration.authRequestModifiers = new ArrayList<>();
        }
        configuration.authRequestModifiers.add(modifier);
    }

    public void withModifyTokenRequest(TokenRequestModifier modifier) {
        SpewOAuth2ConfigurationImpl configuration = configuration();

        if (configuration.tokenRequestModifiers == null) {
            configuration.tokenRequestModifiers = new ArrayList<>();
        }
        configuration.tokenRequestModifiers.add(modifier);
    }

    public static final class SpewOAuth2ConfigurationImpl
            extends SpewVerifiedAuthConnectionConfigurationBuilder.SpewVerifiedAuthConnectionConfigurationImpl
            implements SpewOAuth2Configuration {

        private String clientId;
        private String clientSecret;
        private EditableProperty refreshTokenProperty;
        private EditableProperty accessTokenExpiryProperty;
        private EditableProperty accessTokenProperty;
        private String authorizationUri;
        private String tokenUri;
        private String scope;
        // TODO! Spring code won't use these
        private List<AuthRequestModifier> authRequestModifiers;
        private List<TokenRequestModifier> tokenRequestModifiers;

        @Override
        public String getClientId() {
            return clientId;
        }

        @Override
        public String getClientSecret() {
            return clientSecret;
        }

        @Override
        public EditableProperty getAccessTokenProperty() {
            return accessTokenProperty;
        }

        @Override
        public EditableProperty getAccessTokenExpiryProperty() {
            return accessTokenExpiryProperty;
        }

        @Override
        public EditableProperty getRefreshTokenProperty() {
            return refreshTokenProperty;
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
        public void modifyAuthorizationRequest(OutgoingHttpRequest request) {
            if (authRequestModifiers != null) {
                for (AuthRequestModifier authRequestModifier : authRequestModifiers) {
                    authRequestModifier.modify(request, this);
                }
            }
        }

        @Override
        public void modifyTokenRequest(OutgoingHttpRequest request, Map<String, String> bodyData) {
            if (tokenRequestModifiers != null) {
                for (TokenRequestModifier tokenRequestModifier : tokenRequestModifiers) {
                    tokenRequestModifier.modify(request, bodyData, this);
                }
            }
        }

    }

}
