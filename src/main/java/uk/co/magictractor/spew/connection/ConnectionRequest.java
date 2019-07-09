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
package uk.co.magictractor.spew.connection;

import java.io.IOException;
import java.io.OutputStream;

import com.jayway.jsonpath.Configuration;

import uk.co.magictractor.spew.api.SpewRequest;

/**
 * Wrapper for various for Http request implements used when building Http
 * requests from a SpewRequest.
 */
public interface ConnectionRequest {

    public default void writeParams(SpewRequest request, Configuration jsonConfiguration) throws IOException {
        //        String json = jsonConfiguration.jsonProvider().toJson(request.getParams());
        //        setRequestBody(json);

        if (request.hasParamsInBody()) {
            setDoOutput(true);

            if (!request.getParams().isEmpty()) {
                setHeader("content-type", "application/json");
                String requestBody = jsonConfiguration.jsonProvider().toJson(request.getParams());
                //logger.trace("request body: " + requestBody);
                // TODO! encoding
                byte[] requestBodyBytes = requestBody.getBytes();
                setFixedLengthStreamingMode(requestBodyBytes.length);
                getOutputStream().write(requestBodyBytes);
            }
            else {
                // Prevent 411 Content Length Required
                setFixedLengthStreamingMode(0);
            }
        }
        else {
            // Write params to the query string
            if (!request.getParams().isEmpty()) {
                throw new UnsupportedOperationException("change code to write params to the query string");
            }
        }

    }

    public OutputStream getOutputStream() throws IOException;

    public void setFixedLengthStreamingMode(int contentLength);

    public void setHeader(String headerName, String headerValue);

    public void setDoOutput(boolean b);

}
