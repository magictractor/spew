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

import uk.co.magictractor.spew.api.SpewOAuth2Configuration;

public class GoogleClientOAuth2Connection extends AbstractGoogleClientConnection<SpewOAuth2Configuration> {

    /**
     * Applications should obtain instances via
     * {@link GoogleClientConnectionFactory#createConnection}, usually
     * indirectly via OAuthConnectionFactory.
     */
    public GoogleClientOAuth2Connection(SpewOAuth2Configuration configuration) {
        super(configuration);
    }

}
