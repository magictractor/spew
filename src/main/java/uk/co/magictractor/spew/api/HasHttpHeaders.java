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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public interface HasHttpHeaders {

    /**
     * TODO! update this comment - the code here deals with this
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
    List<SpewHeader> getHeaders();

    default List<String> getHeaderValues(String headerName) {
        return getHeaders(headerName).stream()
                .map(SpewHeader::getValue)
                .collect(Collectors.toList());
    }

    default String getHeaderValue(String headerName) {
        SpewHeader header = getHeader(headerName);
        return header == null ? null : header.getValue();
    }

    default List<SpewHeader> getHeaders(String headerName) {
        List<SpewHeader> headers = new ArrayList<>();
        for (SpewHeader candidate : getHeaders()) {
            if (candidate.getName().equalsIgnoreCase(headerName)) {
                headers.add(candidate);
            }
        }
        return headers;
    }

    default SpewHeader getHeader(String headerName) {
        List<SpewHeader> headers = getHeaders(headerName);
        if (headers.isEmpty()) {
            return null;
        }
        else if (headers.size() == 1) {
            return headers.get(0);
        }
        throw new IllegalStateException("There are multiple headers with name " + headerName);
    }

    static void setHeader(List<SpewHeader> headers, String headerName, String headerValue) {
        int existingIndex = -1;
        int i = -1;
        for (SpewHeader header : headers) {
            i++;
            if (header.getName().equalsIgnoreCase(headerName)) {
                if (existingIndex == -1) {
                    existingIndex = i;
                }
                else {
                    throw new IllegalStateException("There are multiple existing headers with name " + headerName);
                }
            }
        }

        SpewHeader header = new SpewHeader(headerName, headerValue);
        if (existingIndex == -1) {
            headers.add(header);
        }
        else {
            headers.set(existingIndex, header);
        }
    }

}
