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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.core.response.parser.jayway.JaywayResponseInit;
import uk.co.magictractor.spew.util.ContentTypeUtil;

/**
 *
 */
// TODO! SPI
public final class SpewParsedResponseFactory {

    private static final List<SpewParsedResponseInit> INITS = Arrays.asList(
        new JaywayResponseInit());

    private SpewParsedResponseFactory() {
    }

    public static SpewParsedResponse parse(SpewApplication application, SpewResponse response) {

        for (SpewParsedResponseInit init : INITS) {
            SpewParsedResponse instance = (init.instanceFor(application, response));
            if (instance != null) {
                return instance;
            }
        }

        String headerContentType = response.getHeader(ContentTypeUtil.CONTENT_TYPE_HEADER_NAME);
        // TODO! perhaps get encoding from header
        InputStreamReader bodyReader = new InputStreamReader(response.getBodyInputStream(), StandardCharsets.UTF_8);
        BufferedReader bufferedBodyReader = new BufferedReader(bodyReader);
        StringBuilder messageBuilder = new StringBuilder()
                .append("Unable to parse response\nContent-Type: ")
                .append(headerContentType);
        bufferedBodyReader.lines().forEach(line -> {
            messageBuilder.append('\n').append(line);
        });

        throw new IllegalStateException(messageBuilder.toString());
    }

}
