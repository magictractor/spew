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
package uk.co.magictractor.spew.server;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import uk.co.magictractor.spew.api.SpewHeader;

public interface SpewHttpRequest {

    /**
     * <p>
     * Request path. Not null or empty, and always begins with a slash (an
     * absolute path).
     * </p>
     * <p>
     * For example http://me.com/foo/bar?a=1 has path "/foo/bar".
     * </p>
     */
    public String getPath();

    public Map<String, String> getQueryStringParams();

    default Optional<String> getQueryStringParam(String paramName) {
        return Optional.ofNullable(getQueryStringParams().get(paramName));
    }

    public List<SpewHeader> getHeaders();

    /**
     * <p>
     * Note that header names should be case insensitive, but with some third
     * party libraries header names are case sensitive, in which case the
     * SpewResponse wrapper should work around the case sensitivity.
     * </p>
     *
     * @param headerName case insensitive header name
     * @return
     * @see https://stackoverflow.com/questions/5258977/are-http-headers-case-sensitive
     */
    // TODO! duplicated in SpewRequest
    // TODO! make Optional<> and also check for single header, plus add methods for multiple headers with same name
    default String getHeader(String headerName) {
        for (SpewHeader candidate : getHeaders()) {
            if (candidate.getName().equalsIgnoreCase(headerName)) {
                return candidate.getValue();
            }
        }
        return null;
    }

}
