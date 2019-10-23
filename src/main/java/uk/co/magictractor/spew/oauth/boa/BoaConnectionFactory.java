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
package uk.co.magictractor.spew.oauth.boa;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.api.SpewOAuth2Application;
import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;

/**
 *
 */
public class BoaConnectionFactory implements SpewConnectionFactory {

    @Override
    public SpewConnection createConnection(SpewApplication<?> application) {
        if (application instanceof SpewOAuth1Application) {
            SpewOAuth1Application<?> oauth1Application = (SpewOAuth1Application<?>) application;
            return new BoaOAuth1Connection(oauth1Application.getConfiguration());
        }
        else if (application instanceof SpewOAuth2Application) {
            SpewOAuth2Application<?> oauth2Application = (SpewOAuth2Application<?>) application;
            return new BoaOAuth2Connection(oauth2Application.getConfiguration());
        }
        else {
            return null;
        }
    }

}
