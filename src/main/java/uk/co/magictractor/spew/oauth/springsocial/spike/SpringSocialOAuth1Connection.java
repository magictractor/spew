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

import java.io.IOException;
import java.util.Arrays;

import com.jayway.jsonpath.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestOperations;

import uk.co.magictractor.spew.api.OAuth1Application;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.connection.ConnectionRequestFactory;
import uk.co.magictractor.spew.token.UserPreferencesPersister;

public class SpringSocialOAuth1Connection implements SpewConnection {

    private UserPreferencesPersister userToken;
    private UserPreferencesPersister userSecret;

    private Connection<RestOperations> connection;

    // hmm... specific to Jayway, want this to be pluggable too
    private Configuration jsonConfiguration;

    /**
     * Default visibility, applications should obtain instances via
     * {@link SpringSocialConnectionInit#createConnection}, usually indirectly
     * via OAuthConnectionFactory.
     */
    /* default */ SpringSocialOAuth1Connection(OAuth1Application application) {
        SpewOAuth1ConnectionFactory connectionFactory = new SpewOAuth1ConnectionFactory(application);

        this.userToken = new UserPreferencesPersister(application, "user_token");
        this.userSecret = new UserPreferencesPersister(application, "user_secret");

        OAuthToken token = new OAuthToken(userToken.getValue(), userSecret.getValue());
        connection = connectionFactory.createConnection(token);

        jsonConfiguration = application.getServiceProvider().getJsonConfiguration();
    }

    @Override
    public SpewResponse request(SpewRequest apiRequest) {
        //ConnectionData data = connection.createData();

        // TODO! full URL with query string
        String url = apiRequest.getUrl();
        HttpMethod method = HttpMethod.valueOf(apiRequest.getHttpMethod());
        // RequestCallback requestCallback = System.err::println;
        RequestCallback requestCallback = httpRequest -> populateHttpRequest(httpRequest, apiRequest);
        SpewResponseHttpMessageConverter converter = new SpewResponseHttpMessageConverter(jsonConfiguration);
        HttpMessageConverterExtractor<SpewResponse> responseExtractor = new HttpMessageConverterExtractor<SpewResponse>(
            String.class, Arrays.asList(converter));

        //connection.getApi().postForEntity(url, request, responseType)

        SpewResponse response = connection.getApi().execute(url, method, requestCallback, responseExtractor);

        return response;
    }

    private void populateHttpRequest(ClientHttpRequest httpRequest, SpewRequest apiRequest) throws IOException {
        ConnectionRequestFactory.createConnectionRequest(httpRequest).writeParams(apiRequest, jsonConfiguration);
    }

}
