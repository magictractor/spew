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

import org.springframework.social.oauth1.GenericOAuth1ConnectionFactory;
import org.springframework.social.oauth1.OAuth1Version;

import uk.co.magictractor.spew.api.OAuth1Application;

public class SpewOAuth1ConnectionFactory extends GenericOAuth1ConnectionFactory {

    /**
     * @param providerId
     * @param serviceProvider
     * @param apiAdapter
     */
    public SpewOAuth1ConnectionFactory(OAuth1Application application) {
        //super(application.getClass().getSimpleName().toLowerCase(), new SpewOAuth1ServiceProvider(application), null);
        // TODO Auto-generated constructor stub

        super(application.getClass().getSimpleName().toLowerCase(),
            application.getConsumerKey(),
            application.getConsumerSecret(),
            application.getServiceProvider().getTemporaryCredentialRequestUri(),
            application.getServiceProvider().getResourceOwnerAuthorizationUri(),
            //"http://localhost/spring-social-callback",
            application.getServiceProvider().getTokenRequestUri(),
            OAuth1Version.CORE_10_REVISION_A,
            null);
    }

    //    public GenericOAuth1ConnectionFactory(
    //            String providerId,
    //            String consumerKey,
    //            String consumerSecret,
    //            String requestTokenUrl,
    //            String authorizeUrl,
    //            String accessTokenUrl,
    //            OAuth1Version oauth1Version,
    //            ApiAdapter<RestOperations> apiAdapter) {

}
