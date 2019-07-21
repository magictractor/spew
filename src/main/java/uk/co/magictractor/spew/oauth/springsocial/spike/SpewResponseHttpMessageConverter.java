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

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseFactory;

// TODO! make consistent with whatever Boa code does
public class SpewResponseHttpMessageConverter implements HttpMessageConverter<SpewParsedResponse> {

    private final SpewApplication application;

    public SpewResponseHttpMessageConverter(SpewApplication application) {
        this.application = application;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return String.class.equals(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    // TODO! all types
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        //return Arrays.asList("application/json");
        return Arrays.asList(MediaType.APPLICATION_JSON);
    }

    @Override
    public SpewParsedResponse read(Class<? extends SpewParsedResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        HttpInputMessageResponse response = new HttpInputMessageResponse(inputMessage);

        return SpewParsedResponseFactory.parse(application, response);
    }

    @Override
    public void write(SpewParsedResponse t, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException();
    }

}
