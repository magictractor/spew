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

import java.io.InputStream;

/**
 * <p>
 * Interface for SimpleResponses which are backed by a SpewResponse containing
 * content and a small number of headers.
 * </p>
 * <p>
 * Used with static and template responses.
 * </p>
 */
public interface SimpleResourceResponse {

    public int getHttpStatus();

    InputStream getBodyInputStream();

    String getHeader(String headerName);

}
