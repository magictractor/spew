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

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.photo.fraction.Fraction;

public class FractionTypeAdapterTest {

    @Test
    public void testFromStringRational_null() {
        Fraction actual = FractionTypeAdapter.RATIONAL.fromString(null);
        assertThat(actual).isNull();
    }

    @Test
    public void testFromStringRational_happyPath() {
        Fraction actual = FractionTypeAdapter.RATIONAL.fromString("1/100");
        assertThat(actual).isEqualTo(Fraction.of("1/100"));
    }

    @Test
    public void testFromStringRational_nonCanonical() {
        Fraction actual = FractionTypeAdapter.RATIONAL.fromString("10/1000");
        assertThat(actual).isEqualTo(Fraction.of("1/100"));
    }

    @Test
    public void testFromStringRational_givenNumeric() {
        Assertions.assertThatThrownBy(() -> FractionTypeAdapter.RATIONAL.fromString("0.01"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not a rational fraction: 0.01");
    }

    @Test
    public void testToStringRational_happyPath() {
        Fraction fraction = Fraction.of("63/10");
        String actual = FractionTypeAdapter.RATIONAL.toString(fraction);
        assertThat(actual).isEqualTo("63/10");
    }

    @Test
    public void testFromStringNumeric_null() {
        Fraction actual = FractionTypeAdapter.NUMERIC.fromString(null);
        assertThat(actual).isNull();
    }

    @Test
    public void testFromStringNumeric_happyPath() {
        Fraction actual = FractionTypeAdapter.NUMERIC.fromString("0.01");
        assertThat(actual).isEqualTo(Fraction.of("1/100"));
    }

    @Test
    public void testFromStringNumeric_nonCanonical() {
        Fraction actual = FractionTypeAdapter.NUMERIC.fromString("0.0100");
        assertThat(actual).isEqualTo(Fraction.of("1/100"));
    }

    @Test
    public void testFromStringNumeric_givenRational() {
        Assertions.assertThatThrownBy(() -> FractionTypeAdapter.NUMERIC.fromString("1/100"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not a numeric fraction: 1/100");
    }

    @Test
    public void testToStringNumeric_happyPath() {
        Fraction fraction = Fraction.of("63/10");
        String actual = FractionTypeAdapter.NUMERIC.toString(fraction);
        assertThat(actual).isEqualTo("6.3");
    }

}
