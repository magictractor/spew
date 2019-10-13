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

import java.util.function.BiFunction;
import java.util.function.Function;

import uk.co.magictractor.spew.store.EditableProperty;

/**
 *
 */
// TODO! move the annotation here?
// @SpewAuthType("OAuth1")
public interface SpewOAuth1Configuration extends SpewConnectionConfiguration {

    String getConsumerKey();

    String getConsumerSecret();

    String getTemporaryCredentialRequestUri();

    String getResourceOwnerAuthorizationUri();

    String getTokenRequestUri();

    String getRequestSignatureMethod();

    Function<OutgoingHttpRequest, String> getSignatureBaseStringFunction();

    BiFunction<byte[], byte[], byte[]> getSignatureFunction();

    Function<byte[], String> getSignatureEncodingFunction();

    EditableProperty getUserTokenProperty();

    EditableProperty getUserSecretProperty();

}
