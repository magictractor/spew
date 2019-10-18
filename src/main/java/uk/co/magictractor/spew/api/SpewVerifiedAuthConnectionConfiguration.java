/**
 * Copyright 2019 Ken Dobson
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
package uk.co.magictractor.spew.api;

/**
 *
 */
public interface SpewVerifiedAuthConnectionConfiguration extends SpewConnectionConfiguration {

    /**
     * The out-of-band URI (sometimes referred to as "oob") is a special value
     * used in the redirect_url to tell the server to display a verification
     * code rather than perform a callback.
     */
    String getOutOfBandUri();

}
