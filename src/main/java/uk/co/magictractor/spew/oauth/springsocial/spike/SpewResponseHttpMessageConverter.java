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
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import uk.co.magictractor.spew.api.SpewHttpResponse;

// TODO! make consistent with whatever Boa code does
public class SpewResponseHttpMessageConverter implements HttpMessageConverter<SpewHttpResponse> {

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        //return String.class.equals(clazz);

        // Maybe not multipart?
        return true;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    // Not needed because canRead() is overridden
    // TODO! all types
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        //return Arrays.asList("application/json");
        return Arrays.asList(MediaType.APPLICATION_JSON);
    }

    @Override
    public SpewHttpResponse read(Class<? extends SpewHttpResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        IncomingSpringSocialResponse response = new IncomingSpringSocialResponse((ClientHttpResponse) inputMessage);
        // Read body immediately, otherwise the stream will be closed when the body is first accessed.
        response.getBodyBytes();

        return response;
    }

    @Override
    public void write(SpewHttpResponse t, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException();
    }

}
