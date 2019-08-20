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

import org.springframework.social.oauth1.GenericOAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuth1Version;

import uk.co.magictractor.spew.api.SpewOAuth1Application;

public class SpewOAuth1ServiceProvider extends GenericOAuth1ServiceProvider {
    //    extends AbstractOAuth1ServiceProvider<String> {

    /* default */ SpewOAuth1ServiceProvider(SpewOAuth1Application<?> application) {

        super(application.getConsumerKey(),
            application.getConsumerSecret(),
            application.getServiceProvider().getTokenRequestUri(),
            application.getServiceProvider().getResourceOwnerAuthorizationUri(),
            "http://localhost/spring-social-callback",
            application.getServiceProvider().getTemporaryCredentialRequestUri(),
            OAuth1Version.CORE_10_REVISION_A);

        //        super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, authenticateUrl, accessTokenUrl,
        //            oauth1Version);
    }

    //    @Override
    //    public String getApi(String accessToken, String secret) {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }

    /**
     * @param consumerKey
     * @param consumerSecret
     * @param requestTokenUrl
     * @param authorizeUrl
     * @param authenticateUrl
     * @param accessTokenUrl
     * @param oauth1Version
     */
    //    public SpewOAuth1ServiceProvider(String consumerKey, String consumerSecret, String requestTokenUrl,
    //            String authorizeUrl, String authenticateUrl, String accessTokenUrl, OAuth1Version oauth1Version) {
    //        super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, authenticateUrl, accessTokenUrl, oauth1Version);
    //        // TODO Auto-generated constructor stub
    //    }

}
