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

import java.util.Map;

import uk.co.magictractor.spew.store.EditableProperty;

public interface SpewOAuth2Configuration extends SpewVerifiedAuthConnectionConfiguration {

    String getClientId();

    String getClientSecret();

    EditableProperty getAccessTokenProperty();

    EditableProperty getAccessTokenExpiryProperty();

    EditableProperty getRefreshTokenProperty();

    String getAuthorizationUri();

    String getTokenUri();

    String getScope();

    // TODO! maybe not public? could roll in with getAuthorizationUri()? check whether that would work with Spring
    void modifyAuthorizationRequest(OutgoingHttpRequest request);

    // ditto
    void modifyTokenRequest(OutgoingHttpRequest request, Map<String, String> bodyData);

    @FunctionalInterface
    public static interface AuthRequestModifier {
        void modify(OutgoingHttpRequest request, SpewOAuth2Configuration configuration);
    }

    @FunctionalInterface
    public static interface TokenRequestModifier {
        void modify(OutgoingHttpRequest request, Map<String, String> bodyData, SpewOAuth2Configuration configuration);
    }

}
