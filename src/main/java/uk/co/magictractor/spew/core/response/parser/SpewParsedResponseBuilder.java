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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public class SpewParsedResponseBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpewParsedResponseBuilder.class);

    private final SpewApplication<?> application;
    private final SpewHttpResponse response;

    private Integer status;
    private SpewHttpMessageBodyReader bodyReader;
    private Collection<Integer> verifiedStatuses = Collections.singleton(200);
    private Consumer<SpewParsedResponse> verification = this::defaultVerification;

    public SpewParsedResponseBuilder(SpewApplication<?> application, SpewHttpResponse response) {
        this.application = application;
        this.response = response;
        this.status = response.getStatus();
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

    public SpewParsedResponseBuilder withBodyReader(SpewHttpMessageBodyReader bodyReader) {
        this.bodyReader = bodyReader;
        return this;
    }

    public SpewParsedResponseBuilder withBodyReader(Class<? extends SpewHttpMessageBodyReader> bodyReaderClass) {
        this.bodyReader = ExceptionUtil
                .call(() -> bodyReaderClass.getDeclaredConstructor(SpewHttpResponse.class).newInstance(response));
        return this;
    }

    public void withoutVerification() {
        this.verification = null;
    }

    public void withVerification(Consumer<SpewParsedResponse> verification) {
        this.verification = verification;
    }

    public void withVerifiedStatuses(Integer... verifiedStatuses) {
        this.verifiedStatuses = Arrays.asList(verifiedStatuses);
    }

    private void defaultVerification(SpewParsedResponse parsedResponse) {
        LOGGER.warn(
            "There is no verifier for {} responses. Either implement one or call withoutVerification() to remove this warning",
            application.getServiceProvider().getClass().getSimpleName());
    }

    public SpewHttpMessageBodyReader getBodyReader() {
        if (bodyReader == null) {
            bodyReader = SpewHttpMessageBodyReader.instanceFor(application, response);
        }
        return bodyReader;
    }

    public SpewParsedResponse build() {
        SpewParsedResponse parsedResponse = new SpewParsedResponseImpl(status, response, getBodyReader());

        if (verification != null && verifiedStatuses.contains(parsedResponse.getStatus())) {
            verification.accept(parsedResponse);
        }

        return parsedResponse;
    }

}