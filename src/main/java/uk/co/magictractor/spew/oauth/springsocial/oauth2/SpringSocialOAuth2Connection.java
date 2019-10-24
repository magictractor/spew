/**
 * Copyright 2015-2019 Ken Dobson
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
package uk.co.magictractor.spew.oauth.springsocial.oauth2;

import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GenericOAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.web.client.RestOperations;

import uk.co.magictractor.spew.api.SpewOAuth2Configuration;
import uk.co.magictractor.spew.oauth.springsocial.AbstractSpringSocialConnection;
import uk.co.magictractor.spew.oauth.springsocial.SpringSocialConnectionFactory;

// https://docs.spring.io/spring-social/docs/current-SNAPSHOT/reference/htmlsingle/
public class SpringSocialOAuth2Connection extends AbstractSpringSocialConnection<SpewOAuth2Configuration> {

    // would be nice if this was final (set in init() rather than constructor)
    private OAuth2Operations authOps;

    /**
     * Applications should obtain instances via
     * {@link SpringSocialConnectionFactory#createConnection}, usually
     * indirectly via OAuthConnectionFactory.
     */
    public SpringSocialOAuth2Connection(SpewOAuth2Configuration configuration) {
        super(configuration);
    }

    @Override
    protected RestOperations init(SpewOAuth2Configuration configuration) {
        GenericOAuth2ConnectionFactory connectionFactory = new GenericOAuth2ConnectionFactory(
            "TODO!",
            configuration.getClientSecret(),
            configuration.getClientSecret(),
            configuration.getAuthorizationUri(),
            configuration.getTokenUri(),
            null);

        authOps = connectionFactory.getOAuthOperations();

        String accessToken = configuration.getAccessTokenProperty().getValue();
        String scope = configuration.getScope();
        String refreshToken = configuration.getRefreshTokenProperty().getValue();
        // TODO! expiry
        Long expiresIn = null;
        // TODO! could create subclass of AccessGrant which always reads from property (so handling updated values)
        AccessGrant accessGrant = new AccessGrant(accessToken, scope, refreshToken, expiresIn);

        return connectionFactory.createConnection(accessGrant).getApi();
    }

}
