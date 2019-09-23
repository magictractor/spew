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
        return response.getHeader(headerName);
    }

    @Override
    public List<SpewHeader> getHeaders() {
        return response.getHeaders();
    }

    @SuppressWarnings("unchecked")
    protected <T> T convert(Object pojo) {
        if (pojo == null) {
            return null;
        }
        
        return SPIUtil.firstNotNull(ParsedResponsePojoConverter.class, converter -> (T) converter.convert(pojo))
                .orElseThrow(() -> new IllegalArgumentException("Unable to convert " + pojo.getClass().getName()));
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> convertAll(Collection<?> pojos) {
        if (pojos == null) {
            return null;
        }

        return (List<T>) pojos.stream()
                .map(pojo -> this.convert(pojo))
                .collect(Collectors.toList());
    }

}
