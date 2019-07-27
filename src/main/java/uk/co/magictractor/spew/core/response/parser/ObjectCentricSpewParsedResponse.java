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

public interface ObjectCentricSpewParsedResponse extends SpewParsedResponse {

    @Override
    public default String getString(String key) {
        return getObject(key, String.class);
    }

    @Override
    public default int getInt(String key) {
        return getObject(key, Integer.class);
    }

    @Override
    public default long getLong(String key) {
        return getObject(key, Long.class);
    }

    @Override
    public default Object getObject(String key) {
        return getObject(key, Object.class);
    }

}
