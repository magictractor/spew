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
package uk.co.magictractor.spew.core.response.parser;

import java.util.List;

/**
 * <p>
 * Parsed response from the service provider.
 * </p>
 * <p>
 * Most implementations will implement this indirectly via
 * ObjectCentricSpewParsedResponse or StringCentricSpewParsedResponse.
 * </p>
 */
public interface SpewParsedResponse {

    String getString(String key);

    int getInt(String key);

    long getLong(String key);

    boolean getBoolean(String key);

    Object getObject(String key);

    <T> T getObject(String path, Class<T> type);

    <T> List<T> getList(String path, Class<T> type);

}
