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

import static uk.co.magictractor.spew.api.HttpHeaderNames.CONTENT_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewHttpMessage;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.core.http.header.HasHttpHeaders;
import uk.co.magictractor.spew.core.http.header.SpewHeader;
import uk.co.magictractor.spew.util.ContentTypeUtil;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public class SpewParsedResponseBuilder implements SpewHttpMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpewParsedResponseBuilder.class);

    private final SpewApplication<?> application;
    private final SpewHttpResponse response;

    private Integer status;
    private List<SpewHeader> headers;
    private SpewHttpMessageBodyReader bodyReader;
    private Collection<Integer> verifiedStatuses = Collections.singleton(200);
    private Consumer<SpewParsedResponse> verification = this::defaultVerification;

    public SpewParsedResponseBuilder(SpewApplication<?> application, SpewHttpResponse response) {
        this.application = application;
        this.response = response;
        this.status = response.getStatus();
        this.headers = new ArrayList<>(response.getHeaders());
    }

    @Override
    public byte[] getBodyBytes() {
        return response.getBodyBytes();
    }

    /**
     * <p>
     * Changes the status of the response.
     * </p>
     * <p>
     * This can be used, for example, with services which return a 200 status,
     * but indicate authentication or server side errors with a status code in
     * the message. Changing the status to a 401 or 5xx status will trigger
     * standard error handling rather than trying to get data which doesn't
     * exist from the response.
     * </p>
     */
    public SpewParsedResponseBuilder withStatus(int status) {
        this.status = status;
        return this;
    }

    @Override
    public List<SpewHeader> getHeaders() {
        return headers;
    }

    public SpewParsedResponseBuilder withHeader(String headerName, String headerValue) {
        HasHttpHeaders.setHeader(headers, headerName, headerValue);
        return this;
    }

    public String getContentType() {
        return ContentTypeUtil.fromHeader(this);
    }

    /**
     * <p>
     * Typically used to fix the content type in the response when the service
     * uses an inappropriate Content-Type.
     * <p>
     * <p>
     * For example, ImageBam reports "text/hmtl" for Json responses.
     * </p>
     */
    public SpewParsedResponseBuilder withContentType(String contentType) {
        String originalHeaderValue = getHeaderValue(CONTENT_TYPE);
        String newHeaderValue;
        int semiColonIndex = originalHeaderValue.indexOf(";");
        if (semiColonIndex == -1) {
            newHeaderValue = contentType;
        }
        else {
            // Preserve charset etc.
            newHeaderValue = contentType + originalHeaderValue.substring(semiColonIndex);
        }
        return withHeader(CONTENT_TYPE, newHeaderValue);
    }

    public SpewParsedResponseBuilder withBodyReader(SpewHttpMessageBodyReader bodyReader) {
        this.bodyReader = bodyReader;
        return this;
    }

    public SpewParsedResponseBuilder withBodyReader(Class<? extends SpewHttpMessageBodyReader> bodyReaderClass) {
        this.bodyReader = ExceptionUtil
                .call(() -> bodyReaderClass.getDeclaredConstructor(SpewHttpResponse.class).newInstance(response));
        return this;
    }

    public SpewParsedResponseBuilder withoutVerification() {
        this.verification = null;
        return this;
    }

    public SpewParsedResponseBuilder withVerification(Consumer<SpewParsedResponse> verification) {
        this.verification = verification;
        return this;
    }

    public SpewParsedResponseBuilder withVerifiedStatuses(Integer... verifiedStatuses) {
        this.verifiedStatuses = Arrays.asList(verifiedStatuses);
        return this;
    }

    private void defaultVerification(SpewParsedResponse parsedResponse) {
        // TODO! only warn once
        LOGGER.warn(
            "There is no verifier for {} responses, either implement one or call withoutVerification() to remove this warning",
            application.getServiceProvider().getClass().getSimpleName());
    }

    public SpewHttpMessageBodyReader getBodyReader() {
        if (bodyReader == null) {
            bodyReader = SpewHttpMessageBodyReader.instanceFor(application, this);
        }
        return bodyReader;
    }

    public SpewParsedResponse build() {
        SpewParsedResponse parsedResponse = new SpewParsedResponseImpl(status, headers, response, getBodyReader());

        if (verification != null && verifiedStatuses.contains(parsedResponse.getStatus())) {
            verification.accept(parsedResponse);
        }

        return parsedResponse;
    }

}
