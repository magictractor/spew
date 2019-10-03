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

import java.util.List;

import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.util.HttpMessageUtil;

/**
 *
 */
public class SpewParsedResponseImpl implements SpewParsedResponse {

    private final int status;
    private final SpewHttpResponse response;
    private final SpewHttpMessageBodyReader bodyReader;

    public SpewParsedResponseImpl(int status, SpewHttpResponse response, SpewHttpMessageBodyReader bodyReader) {
        this.status = status;
        this.response = response;
        this.bodyReader = bodyReader;
    }

    @Override
    public int getStatus() {
        return status;
    }

    // TODO! HttpHeaders interface for other getHeader() methods

    @Override
    public String getHeaderValue(String headerName) {
        return response.getHeaderValue(headerName);
    }

    @Override
    public List<SpewHeader> getHeaders() {
        return response.getHeaders();
    }

    @Override
    public String getString(String key) {
        return this.bodyReader.getString(key);
    }

    @Override
    public byte[] getBodyBytes() {
        return response.getBodyBytes();
    }

    @Override
    public int getInt(String expr) {
        return this.bodyReader.getInt(expr);
    }

    @Override
    public long getLong(String expr) {
        return this.bodyReader.getLong(expr);
    }

    @Override
    public boolean getBoolean(String expr) {
        return this.bodyReader.getBoolean(expr);
    }

    @Override
    public Object getObject(String expr) {
        return this.bodyReader.getObject(expr);
    }

    @Override
    public <T> T getObject(String expr, Class<T> type) {
        return this.bodyReader.getObject(expr, type);
    }

    @Override
    public <T> List<T> getList(String expr, Class<T> type) {
        return this.bodyReader.getList(expr, type);
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this).toString();
    }

}
