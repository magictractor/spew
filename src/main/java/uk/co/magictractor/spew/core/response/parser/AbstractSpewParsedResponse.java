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
package uk.co.magictractor.spew.core.response.parser;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public abstract class AbstractSpewParsedResponse implements SpewParsedResponse {

    private final SpewHttpResponse response;

    protected AbstractSpewParsedResponse(SpewHttpResponse response) {
        this.response = response;
    }

    @Override
    public String getHeader(String headerName) {
        return response.getHeaderValue(headerName);
    }

    @Override
    public List<SpewHeader> getHeaders() {
        return response.getHeaders();
    }

    protected <T> T subType(T element) {
        if (element == null) {
            return null;
        }

        return SPIUtil.firstNotNull(ParsedResponsePojoConverter.class, converter -> converter.subType(element))
                .orElseThrow(() -> new IllegalArgumentException("Unable to convert " + element.getClass().getName()));
    }

    protected <T> List<T> subTypes(Collection<T> elements) {
        if (elements == null) {
            return null;
        }

        return elements.stream()
                .map(element -> this.subType(element))
                .collect(Collectors.toList());
    }

}
