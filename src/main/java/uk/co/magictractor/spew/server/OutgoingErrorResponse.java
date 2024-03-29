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
package uk.co.magictractor.spew.server;

/**
 * Simple representation of an error page which can be used across multiple
 * CallbackServer implementations.
 */
public class OutgoingErrorResponse extends OutgoingTemplateResponse {

    private static final OutgoingErrorResponse NOT_FOUND = new OutgoingErrorResponse(404, "Not found");

    private final String message;

    public OutgoingErrorResponse(int httpStatus, String message) {
        super("error.html");
        setHttpStatus(httpStatus);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected String getSubstitutionValue(String key) {

        if ("message".equals(key)) {
            return message;
        }
        else if ("httpStatus".equals(key)) {
            return Integer.toString(getStatus());
        }

        return null;
    }

    public static OutgoingErrorResponse notFound() {
        return NOT_FOUND;
    }

}
