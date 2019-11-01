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

public interface ObjectCentricHttpMessageBodyReader extends SpewHttpMessageBodyReader {

    @Override
    public default String getString(String expr) {
        return getObject(expr, String.class);
    }

    @Override
    public default Integer getInt(String expr) {
        return getObject(expr, Integer.class);
    }

    @Override
    public default Long getLong(String expr) {
        return getObject(expr, Long.class);
    }

    @Override
    public default Boolean getBoolean(String expr) {
        return getObject(expr, Boolean.class);
    }

    @Override
    public default Object getObject(String expr) {
        return getObject(expr, Object.class);
    }

}
