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

public interface SpewOAuth2Configuration extends SpewVerifiedAuthConnectionConfiguration {

    String getClientId();

    String getClientSecret();

    String getAuthorizationUri();

    String getTokenUri();

    String getScope();

    @Override
    default String getOutOfBandUri() {
        // out-of-band isn't in the spec, but is supported by Google and other
        // https://mailarchive.ietf.org/arch/msg/oauth/OCeJLZCEtNb170Xy-C3uTVDIYjM
        return "urn:ietf:wg:oauth:2.0:oob";
    }

}
