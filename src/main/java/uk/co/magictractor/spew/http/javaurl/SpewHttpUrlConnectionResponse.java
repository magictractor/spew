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
package uk.co.magictractor.spew.http.javaurl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.core.response.AbstractOnCloseResponse;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public class SpewHttpUrlConnectionResponse extends AbstractOnCloseResponse {

    private final HttpURLConnection connection;
    private List<SpewHeader> headers;

    public SpewHttpUrlConnectionResponse(HttpURLConnection connection) {
        super(getStream(connection));
        this.connection = connection;
    }

    private static InputStream getStream(HttpURLConnection connection) {
        return ExceptionUtil.call(() -> getStream0(connection));
    }

    private static InputStream getStream0(HttpURLConnection connection) throws IOException {
        /*
         * getInputStream() throws an exception for some status codes and
         * getErrorStream() is null until getInputStream() has been called.
         * Workaround by calling getResponseCode() which calls getInputStream()
         * with exception handling.
         */
        connection.getResponseCode();
        return connection.getErrorStream() != null ? connection.getErrorStream() : connection.getInputStream();
    }

    @Override
    public List<SpewHeader> getHeaders() {
        if (headers == null) {
            Map<String, List<String>> httpUrlHeaders = connection.getHeaderFields();
            headers = new ArrayList<>(httpUrlHeaders.size());
            for (Map.Entry<String, List<String>> httpUrlHeaderEntry : httpUrlHeaders.entrySet()) {
                String headerName = httpUrlHeaderEntry.getKey();
                for (String headerValue : httpUrlHeaderEntry.getValue()) {
                    headers.add(new SpewHeader(headerName, headerValue));
                }
            }
        }
        return headers;
    }

    @Override
    public void onClose() {
        connection.disconnect();
    }

}
