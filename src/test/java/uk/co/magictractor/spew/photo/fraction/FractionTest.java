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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class FractionTest {

    @Test
    public void testOf_null() {
        Fraction actual = Fraction.of(null);

        assertThat(actual).isNull();
    }

    @Test
    public void testOf_decimal() {
        Fraction actual = Fraction.of("4.5");

        assertThat(actual.getNumerator()).isEqualTo(45);
        assertThat(actual.getDenominator()).isEqualTo(10);
    }

    @Test
    public void testOf_integer() {
        Fraction actual = Fraction.of("18");

        assertThat(actual.getNumerator()).isEqualTo(18);
        assertThat(actual.getDenominator()).isEqualTo(1);
    }

    @Test
    public void testOf_rational() {
        Fraction actual = Fraction.of("1/100");

        assertThat(actual.getNumerator()).isEqualTo(1);
        assertThat(actual.getDenominator()).isEqualTo(100);
    }

    @Test
    public void testOf_illegalArgument() {
        assertThatThrownBy(() -> Fraction.of("xyz"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not a numeric or rational fraction or too big: xyz");
    }

    @Test
    public void testOf_bigNumber() {
        assertThatThrownBy(() -> Fraction.of("1/10000000000"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not a numeric or rational fraction or too big: 1/10000000000");
    }

    @Test
    public void testOfRational_null() {
        Fraction actual = Fraction.ofRational(null);

        assertThat(actual).isNull();
    }

    @Test
    public void testOfRational_rational() {
        Fraction actual = Fraction.ofRational("1/200");

        assertThat(actual.getNumerator()).isEqualTo(1);
        assertThat(actual.getDenominator()).isEqualTo(200);
    }

    @Test
    public void testOfRational_numeric() {
        assertThatThrownBy(() -> Fraction.ofRational("6.3"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not a rational fraction or too big: 6.3");
    }

    @Test
    public void testOfNumeric_null() {
        Fraction actual = Fraction.ofNumeric(null);

        assertThat(actual).isNull();
    }

    @Test
    public void testOfNumeric_decimal() {
        Fraction actual = Fraction.ofNumeric("4.0");

        assertThat(actual.getNumerator()).isEqualTo(40);
        assertThat(actual.getDenominator()).isEqualTo(10);
    }

    @Test
    public void testOfNumeric_integer() {
        Fraction actual = Fraction.ofNumeric("4");

        assertThat(actual.getNumerator()).isEqualTo(4);
        assertThat(actual.getDenominator()).isEqualTo(1);
    }

    @Test
    public void testOfNumeric_rational() {
        assertThatThrownBy(() -> Fraction.ofNumeric("1/500"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not a numeric fraction or too big: 1/500");
    }

    @Test
    public void testOfPair_happyPath() {
        Fraction actual = Fraction.of(1, 80);

        assertThat(actual.getNumerator()).isEqualTo(1);
        assertThat(actual.getDenominator()).isEqualTo(80);
    }

    @Test
    public void testOfPair_zeroDenominator() {
        assertThatThrownBy(() -> Fraction.of(1, 0))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("denominator must be a positive value");
    }

    @Test
    public void testOfPair_negativeDenominator() {
        assertThatThrownBy(() -> Fraction.of(1, -1))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("denominator must be a positive value");
    }

    @Test
    public void testToRationalString() {
        Fraction fraction = Fraction.of("5/3");
        assertThat(fraction.toRationalString()).isEqualTo("5/3");
    }

    @Test
    public void testToRationalString_large() {
        Fraction fraction = Fraction.of("50000000/3");
        assertThat(fraction.toRationalString()).isEqualTo("50000000/3");
    }

    @Test
    public void testToNumericString() {
        Fraction fraction = Fraction.of("5/3");
        assertThat(fraction.toNumericString()).isEqualTo("1.667");
    }

    @Test
    public void testToNumericString_withDecimalPlaces() {
        Fraction fraction = Fraction.of("5/3");
        assertThat(fraction.toNumericString(5)).isEqualTo("1.66667");
    }

    @Test
    public void testToNumericString_large() {
        Fraction fraction = Fraction.of("50000000/3");
        assertThat(fraction.toNumericString()).isEqualTo("16666666.667");
    }

    @Test
    public void testToString() {
        Fraction fraction = Fraction.of("1/50");
        assertThat(fraction.toString()).isEqualTo("Fraction{numerator=1, denominator=50}");
    }

    @Test
    public void testCanonicalForm_reduced() {
        Fraction fraction = Fraction.of("4.5");
        Fraction actual = fraction.getCanonicalForm();

        assertThat(actual.getNumerator()).isEqualTo(9);
        assertThat(actual.getDenominator()).isEqualTo(2);
    }

    @Test
    public void testCanonicalForm_notReduced() {
        Fraction fraction = Fraction.of("0.01");
        Fraction actual = fraction.getCanonicalForm();

        assertThat(actual).isSameAs(fraction);
    }

    @Test
    public void testHashCode_OneOverTwo() {
        Fraction fraction = Fraction.of("1/2");

        assertThat(fraction.hashCode()).isEqualTo(0x0102);
    }

    @Test
    public void testHashCode_reduced() {
        Fraction fraction = Fraction.of("3/6");
        Fraction canonical = Fraction.of("1/2");

        assertThat(fraction.hashCode()).isEqualTo(canonical.hashCode());
    }

    @Test
    public void testEquals_self() {
        Fraction fraction = Fraction.of("0.01");

        assertThat(fraction.equals(fraction)).isTrue();
    }

    @Test
    public void testEquals_null() {
        Fraction fraction = Fraction.of("0.01");

        assertThat(fraction.equals(null)).isFalse();
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals_otherType() {
        Fraction fraction = Fraction.of("0.01");

        assertThat(fraction.equals("xyz")).isFalse();
    }

    @Test
    public void testEquals_otherValue() {
        Fraction fraction = Fraction.of("0.01");
        Fraction other = Fraction.of("0.02");

        assertThat(fraction.equals(other)).isFalse();
    }

    @Test
    public void testEquals_sameNumeratorAndDenominator() {
        Fraction fraction = Fraction.of("0.01");
        Fraction.clearCache();
        Fraction other = Fraction.of(1, 100);
        assertThat(fraction.getNumerator()).isEqualTo(other.getNumerator());
        assertThat(fraction.getDenominator()).isEqualTo(other.getDenominator());
        // The cache was cleared for this same-as check.
        assertThat(fraction).isNotSameAs(other);

        assertThat(fraction.equals(other)).isTrue();
    }

    @Test
    public void testEquals_multipliedNumeratorAndDenominator() {
        Fraction fraction = Fraction.of("4.0");
        Fraction other = Fraction.of("4");
        // preconditions
        assertThat(fraction.getNumerator()).isNotEqualTo(other.getNumerator());
        assertThat(fraction.getDenominator()).isNotEqualTo(other.getDenominator());

        assertThat(fraction.equals(other)).isTrue();
    }

    // TODO! roll hashCode() checks into testEquals() methods

}
