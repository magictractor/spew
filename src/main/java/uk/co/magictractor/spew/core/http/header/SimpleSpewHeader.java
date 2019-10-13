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

import com.google.common.base.Strings;

/** Instances are created via SpewHeader.of() */
/* default */ class SimpleSpewHeader implements SpewHeader {

    private final String name;
    private final String value;

    public SimpleSpewHeader(String name, String value) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("name must not be null or empty");
        }
        if (Strings.isNullOrEmpty(value)) {
            throw new IllegalArgumentException("value must not be null or empty");
        }
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return SpewHeader.toStringHelper(this).toString();
    }

}
