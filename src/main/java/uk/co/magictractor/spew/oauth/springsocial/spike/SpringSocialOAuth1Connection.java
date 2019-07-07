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
package uk.co.magictractor.spew.oauth.springsocial.spike;

import org.springframework.social.connect.Connection;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.web.client.RestOperations;

import uk.co.magictractor.spew.api.OAuth1Application;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.token.UserPreferencesPersister;

public class SpringSocialOAuth1Connection implements SpewConnection {

    private UserPreferencesPersister userToken;
    private UserPreferencesPersister userSecret;

    private Connection<RestOperations> connection;

    public SpringSocialOAuth1Connection(OAuth1Application application) {
        SpewOAuth1ConnectionFactory connectionFactory = new SpewOAuth1ConnectionFactory(application);

        this.userToken = new UserPreferencesPersister(application, "user_token");
        this.userSecret = new UserPreferencesPersister(application, "user_secret");

        OAuthToken token = new OAuthToken(userToken.getValue(), userSecret.getValue());
        connection = connectionFactory.createConnection(token);
    }

    @Override
    public SpewResponse request(SpewRequest apiRequest) {
        connection.createData();

        return null;
    }

}
