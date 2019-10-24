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
package uk.co.magictractor.spew.oauth.springsocial.oauth1;

import org.springframework.social.oauth1.GenericOAuth1ConnectionFactory;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.web.client.RestOperations;

import uk.co.magictractor.spew.api.SpewOAuth1Configuration;
import uk.co.magictractor.spew.oauth.springsocial.AbstractSpringSocialConnection;
import uk.co.magictractor.spew.oauth.springsocial.SpringSocialConnectionFactory;

// https://docs.spring.io/spring-social/docs/current-SNAPSHOT/reference/htmlsingle/
public class SpringSocialOAuth1Connection extends AbstractSpringSocialConnection<SpewOAuth1Configuration> {

    /**
     * Applications should obtain instances via
     * {@link SpringSocialConnectionFactory#createConnection}, usually
     * indirectly via OAuthConnectionFactory.
     */
    public SpringSocialOAuth1Connection(SpewOAuth1Configuration configuration) {
        super(configuration);
    }

    @Override
    protected RestOperations init(SpewOAuth1Configuration configuration) {
        GenericOAuth1ConnectionFactory connectionFactory = new GenericOAuth1ConnectionFactory(
            "TODO!", /*
                      * application.getClass().getSimpleName().toLowerCase(),
                      */
            configuration.getConsumerKey(),
            configuration.getConsumerSecret(),
            configuration.getTemporaryCredentialRequestUri(),
            configuration.getResourceOwnerAuthorizationUri(),
            //"http://localhost/spring-social-callback",
            configuration.getTokenRequestUri(),
            OAuth1Version.CORE_10_REVISION_A,
            null);

        OAuthToken token = new OAuthToken(
            configuration.getUserTokenProperty().getValue(), configuration.getUserSecretProperty().getValue());

        return connectionFactory.createConnection(token).getApi();
    }

}
