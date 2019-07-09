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
import java.net.HttpURLConnection;

public class HttpURLConnectionConnectionRequest implements ConnectionRequest {

    private final HttpURLConnection httpUrlConnection;

    public HttpURLConnectionConnectionRequest(HttpURLConnection httpUrlConnection) {
        this.httpUrlConnection = httpUrlConnection;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return httpUrlConnection.getOutputStream();
    }

    @Override
    public void setFixedLengthStreamingMode(int contentLength) {
        httpUrlConnection.setFixedLengthStreamingMode(contentLength);
    }

    @Override
    public void setHeader(String headerName, String headerValue) {
        httpUrlConnection.setRequestProperty(headerName, headerValue);
    }

    @Override
    public void setDoOutput(boolean doOutput) {
        httpUrlConnection.setDoOutput(doOutput);
    }

}
