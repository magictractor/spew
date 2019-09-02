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
package uk.co.magictractor.spew.core.typeadapter;

/**
 *
 */
public class BooleanTypeAdapter implements SpewTypeAdapter<Boolean> {

    public static final BooleanTypeAdapter ZERO_AND_ONE = new BooleanTypeAdapter("0", "1");

    private final String falseString;
    private final String trueString;

    public BooleanTypeAdapter(String falseString, String trueString) {
        this.falseString = falseString;
        this.trueString = trueString;
    }

    @Override
    public Class<Boolean> getType() {
        // TODO! Make it configurable whether this is Boolean or boolean?
        return boolean.class;
    }

    @Override
    public Boolean fromString(String valueAsString) {
        if (falseString.equals(valueAsString)) {
            return Boolean.FALSE;
        }
        else if (trueString.equals(valueAsString)) {
            return Boolean.TRUE;
        }
        throw new IllegalArgumentException();
        // ("value '" + valueAsString is not one of the expected boolean");
    }

    @Override
    public String toString(Boolean value) {
        if (Boolean.FALSE.equals(value)) {
            return falseString;
        }
        else if (Boolean.TRUE.equals(value)) {
            return trueString;
        }
        throw new IllegalArgumentException("value must not be null");
    }

}
