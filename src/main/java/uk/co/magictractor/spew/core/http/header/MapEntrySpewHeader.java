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

/** Instances are created via SpewHeader.of() */
/* default */ class MapEntrySpewHeader implements SpewHeader {

    private final Map.Entry<String, String> entry;

    public MapEntrySpewHeader(Map.Entry<String, String> entry) {
        this.entry = entry;
    }

    @Override
    public String getName() {
        return entry.getKey();
    }

    @Override
    public String getValue() {
        return entry.getValue();
    }

    @Override
    public String toString() {
        return SpewHeader.toStringHelper(this).toString();
    }

}
