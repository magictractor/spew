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
package uk.co.magictractor.spew.oauth.springsocial.spike;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.jayway.jsonpath.Configuration;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import uk.co.magictractor.spew.api.OAuthJsonResponse;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.util.IOUtil;

public class SpewResponseHttpMessageConverter implements HttpMessageConverter<SpewResponse> {

    private final Configuration jsonConfiguration;

    public SpewResponseHttpMessageConverter(Configuration jsonConfiguration) {
        this.jsonConfiguration = jsonConfiguration;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return String.class.equals(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        //return Arrays.asList("application/json");
        return Arrays.asList(MediaType.APPLICATION_JSON);
    }

    @Override
    public SpewResponse read(Class<? extends SpewResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        //        DocumentContext ctx;
        //        try (InputStream body = inputMessage.getBody()) {
        //            // TODO! encoding
        //            ctx = JsonPath.parse(body, jsonConfiguration);
        //        }

        String body = IOUtil.readStringAndClose(inputMessage.getBody());
        System.out.println(body);

        return new OAuthJsonResponse(body, jsonConfiguration);
    }

    @Override
    public void write(SpewResponse t, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException();
    }

}
