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

import uk.co.magictractor.spew.photo.fraction.Fraction;

/**
 *
 */
public abstract class FractionTypeAdapter implements SpewTypeAdapter<Fraction> {

    /** For example aperture 6.3, may be whole number such as 18 */
    public static final FractionTypeAdapter NUMERIC = new NumericFractionTypeAdapter();
    /** For example aperture 63/100, shutter speed 1/500 or 10/5000 */
    public static final FractionTypeAdapter RATIONAL = new RationalFractionTypeAdapter();

    @Override
    public Class<Fraction> getType() {
        return Fraction.class;
    }

    public static final class NumericFractionTypeAdapter extends FractionTypeAdapter {
        @Override
        public Fraction fromString(String valueAsString) {
            return Fraction.ofNumeric(valueAsString);
        }

        @Override
        public String toString(Fraction fraction) {
            return fraction.toNumericString();
        }
    }

    public static final class RationalFractionTypeAdapter extends FractionTypeAdapter {
        @Override
        public Fraction fromString(String valueAsString) {
            return Fraction.ofRational(valueAsString);
        }

        @Override
        public String toString(Fraction fraction) {
            return fraction.toRationalString();
        }
    }

}
