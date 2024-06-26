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
package uk.co.magictractor.spew.api.connection;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewConnectionConfiguration;

/**
 *
 */
public interface SpewConnectionFactory {

    /**
     * @param application the application for which a connection is to be
     *        created.
     * @return new connection, or null if connection cannot be created, in which
     *         case other implementations may be available (via SPI) to create
     *         the implementation
     */
    public SpewConnection createConnection(SpewConnectionConfiguration configuration);

    default SpewConnection createConnectionWithoutAuth(SpewConnectionConfiguration configuration) {
        return null;
    }

}
