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
package uk.co.magictractor.spew.core.response;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 *
 */
public class HttpUrlConnectionResponse extends AbstractOnCloseResponse {

    private final HttpURLConnection connection;

    public HttpUrlConnectionResponse(HttpURLConnection connection) throws IOException {
        // Check for the error stream first, since getInputStream() throws an exception for some status codes.
        // meh... both streams are null...
        super(connection.getErrorStream() != null ? connection.getErrorStream() : connection.getInputStream());
        this.connection = connection;
    }

    @Override
    public String getHeader(String headerName) {
        return connection.getHeaderField(headerName);
    }

    @Override
    public void onClose() {
        connection.disconnect();
    }

}
