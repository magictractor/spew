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
package uk.co.magictractor.spew.oauth.googleclient;

import uk.co.magictractor.spew.api.SpewOAuth1Configuration;

/**
 * Google Client supports HMAC-SHA1 and RSA-SHA1 signature methods. See
 * https://developers.google.com/api-client-library/java/google-oauth-java-client/oauth1.
 */
// https://googleapis.dev/java/google-oauth-client/1.25.0/index.html
public class GoogleClientOAuth1Connection extends AbstractGoogleClientConnection<SpewOAuth1Configuration> {

    /**
     * Applications should obtain instances via
     * {@link GoogleClientConnectionFactory#createConnection}, usually
     * indirectly via OAuthConnectionFactory.
     */
    public GoogleClientOAuth1Connection(SpewOAuth1Configuration configuration) {
        super(configuration);
        
        // See https://googleapis.dev/java/google-oauth-client/1.30.4/index.html
    }

}
