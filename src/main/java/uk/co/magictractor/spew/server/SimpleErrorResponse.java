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
public class SimpleErrorResponse extends SimpleResponse {

    private final String message;

    public SimpleErrorResponse(int httpStatus, String message) {
        setHttpStatus(httpStatus);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getValue(String key) {

        if ("message".equals(key)) {
            return message;
        }
        else if ("httpStatus".equals(key)) {
            return Integer.toString(getHttpStatus());
        }

        return null;
    }

    public static SimpleErrorResponse notFound() {
        return new SimpleErrorResponse(404, "Not found");
    }

}
