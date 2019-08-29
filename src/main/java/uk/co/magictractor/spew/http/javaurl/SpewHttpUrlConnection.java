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
package uk.co.magictractor.spew.http.javaurl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public class SpewHttpUrlConnection implements SpewConnection {

    @Override
    public SpewHttpResponse request(OutgoingHttpRequest request) {
        return ExceptionUtil.call(() -> request0(request));
    }

    public SpewHttpResponse request0(OutgoingHttpRequest request) throws IOException {
        // URL has no knowledge of escaping, the application must do that. See URL Javadoc.
        URL url = new URL(request.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(request.getHttpMethod());

        for (SpewHeader header : request.getHeaders()) {
            connection.addRequestProperty(header.getName(), header.getValue());
        }

        byte[] body = request.getBodyBytes();
        if (body != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(body);
        }

        return new IncomingHttpUrlConnectionResponse(connection);
    }

    @Override
    public SpewApplication<?> getApplication() {
        throw new UnsupportedOperationException("getApplication() not yet supported for transport connections");
    }

}
