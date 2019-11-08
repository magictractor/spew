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
package uk.co.magictractor.spew.photo.fraction;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class FractionTest {

    @Test
    public void testToRationalString() {
        Fraction fraction = Fraction.of("5/3");
        Assertions.assertThat(fraction.toRationalString()).isEqualTo("5/3");
    }

    @Test
    public void testToRationalString_large() {
        Fraction fraction = Fraction.of("50000000/3");
        Assertions.assertThat(fraction.toRationalString()).isEqualTo("50000000/3");
    }

    @Test
    public void testToNumericString() {
        Fraction fraction = Fraction.of("5/3");
        Assertions.assertThat(fraction.toNumericString()).isEqualTo("1.667");
    }

    @Test
    public void testToNumericString_withDecimalPlaces() {
        Fraction fraction = Fraction.of("5/3");
        Assertions.assertThat(fraction.toNumericString(5)).isEqualTo("1.66667");
    }

    @Test
    public void testToNumericString_large() {
        Fraction fraction = Fraction.of("50000000/3");
        Assertions.assertThat(fraction.toNumericString()).isEqualTo("16666666.667");
    }

}
