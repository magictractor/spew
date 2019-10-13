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
package uk.co.magictractor.spew.core.http.header;

import java.util.Map;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 *
 */
public interface SpewHeader {

    String getName();

    String getValue();

    public static SpewHeader of(String name, String value) {
        return new SimpleSpewHeader(name, value);
    }

    public static SpewHeader of(Map.Entry<String, String> entry) {
        return new MapEntrySpewHeader(entry);
    }

    public static ToStringHelper toStringHelper(SpewHeader header) {
        return MoreObjects.toStringHelper(header.getName())
                .add("name", header.getName())
                .add("value", header.getValue());
    }

}
