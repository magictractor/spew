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
package uk.co.magictractor.spew.core.response.parser.text;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.io.CharStreams;

import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.core.response.parser.AbstractSpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.StringCentricSpewParsedResponse;
import uk.co.magictractor.spew.util.HttpMessageUtil;
import uk.co.magictractor.spew.util.IOUtil;

/**
 * <p>
 * Response containing key and value pairs separated by ampersands.
 * </p>
 * <p>
 * OAuth1 uses a response like this to return oauth_token and oauth_token_secret
 * values.
 * </p>
 */
public class KeyValuePairsResponse
        extends AbstractSpewParsedResponse
        implements StringCentricSpewParsedResponse {

    private final Map<String, String> values;

    public KeyValuePairsResponse(SpewHttpResponse response) {
        super(response);
        values = IOUtil.applyThenClose(HttpMessageUtil.createBodyReader(response), this::parse);
    }

    private Map<String, String> parse(Reader bodyReader) throws IOException {
        String body = CharStreams.toString(bodyReader);
        if (body.isEmpty()) {
            // Spitter chokes getting a Map from an empty String.
            return Collections.emptyMap();
        }
        return Splitter.on("&").withKeyValueSeparator("=").split(body);
    }

    @Override
    public String getString(String key) {
        return values.get(key);
    }

    @Override
    public <T> List<T> getList(String path, Class<?> type) {
        throw new UnsupportedOperationException("This response type does not support conversion to POJOs");
    }

    @Override
    public <T> T getObject(String path, Class<?> type) {
        throw new UnsupportedOperationException("This response type does not support conversion to POJOs");
    }

}
