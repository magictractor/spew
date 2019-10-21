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
package uk.co.magictractor.spew.core.response.parser;

import static uk.co.magictractor.spew.api.HttpHeaderNames.CONTENT_TYPE;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import uk.co.magictractor.spew.api.SpewConnectionConfiguration;
import uk.co.magictractor.spew.api.SpewHttpMessage;
import uk.co.magictractor.spew.util.HttpMessageUtil;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 * <p>
 * Parsed response from the service provider.
 * </p>
 * <p>
 * Most implementations will implement this indirectly via
 * ObjectCentricSpewParsedResponse or StringCentricSpewParsedResponse.
 * </p>
 */
public interface SpewHttpMessageBodyReader {

    String getString(String key);

    int getInt(String key);

    long getLong(String key);

    boolean getBoolean(String key);

    Object getObject(String key);

    <T> T getObject(String path, Class<T> type);

    <T> List<T> getList(String path, Class<T> type);

    default <T> T subType(T element) {
        if (element == null) {
            return null;
        }

        return SPIUtil.firstNotNull(ParsedResponsePojoConverter.class, converter -> converter.subType(element))
                .orElseThrow(() -> new IllegalArgumentException("Unable to convert " + element.getClass().getName()));
    }

    default <T> List<T> subTypes(Collection<T> elements) {
        if (elements == null) {
            return null;
        }

        return elements.stream()
                .map(element -> this.subType(element))
                .collect(Collectors.toList());
    }

    static SpewHttpMessageBodyReader instanceFor(SpewConnectionConfiguration connectionConfiguration,
            SpewHttpMessage response) {

        Optional<SpewHttpMessageBodyReader> instance = SPIUtil.firstNotNull(SpewHttpMessageBodyReaderFactory.class,
            factory -> factory.instanceFor(connectionConfiguration, response));
        if (instance.isPresent()) {
            return instance.get();
        }

        String headerContentType = response.getHeaderValue(CONTENT_TYPE);
        BufferedReader bodyReader = HttpMessageUtil.createBodyReader(response);
        StringBuilder messageBuilder = new StringBuilder()
                // TODO! link to a help page
                .append("There are no available parsers for \nContent-Type: ")
                .append(headerContentType);
        bodyReader.lines().forEach(line -> {
            messageBuilder.append('\n').append(line);
        });

        throw new IllegalStateException(messageBuilder.toString());
    }

}
