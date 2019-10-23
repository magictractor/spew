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

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewConnectionConfiguration;
import uk.co.magictractor.spew.api.SpewOAuth1Configuration;
import uk.co.magictractor.spew.api.SpewOAuth2Configuration;
import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;

/**
 *
 */
public class BoaConnectionFactory implements SpewConnectionFactory {

    @Override
    public SpewConnection createConnection(SpewConnectionConfiguration configuration) {
        if (configuration instanceof SpewOAuth1Configuration) {
            return new BoaOAuth1Connection((SpewOAuth1Configuration) configuration);
        }
        else if (configuration instanceof SpewOAuth2Configuration) {
            return new BoaOAuth2Connection((SpewOAuth2Configuration) configuration);
        }
        else {
            return null;
        }
    }

}
