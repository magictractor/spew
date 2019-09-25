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
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.core.message.AbstractInputStreamMessage;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.HttpMessageUtil;

/**
 *
 */
public class IncomingHttpUrlConnectionResponse extends AbstractInputStreamMessage implements SpewHttpResponse {

    private final HttpURLConnection connection;
    private List<SpewHeader> headers;

    public IncomingHttpUrlConnectionResponse(HttpURLConnection connection) {
        super(() -> getStream(connection));
        this.connection = connection;
    }

    @Override
    public int getStatus() {
        return ExceptionUtil.call(() -> connection.getResponseCode());
    }

    //    private static InputStream getStream(HttpURLConnection connection) {
    //        return ExceptionUtil.call(() -> getStream0(connection));
    //    }

    private static InputStream getStream(HttpURLConnection connection) throws IOException {
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
                if (headerName == null) {
                    // This is the status line, ignore it. Typical value is "HTTP/1.1 200 OK".
                    continue;
                }
                for (String headerValue : httpUrlHeaderEntry.getValue()) {
                    headers.add(new SpewHeader(headerName, headerValue));
                }
            }
        }
        return headers;
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this).toString();
    }
}
