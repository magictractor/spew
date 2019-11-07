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

import com.google.common.base.MoreObjects;

/**
 * <p>
 * Used for fractional media propery values, such as exposure and aperture which
 * are represented in different, but equivalent, ways by different tools.
 * </p>
 * <p>
 * For example, exposure may be stored as 1/100, 10/1000 and 0.01 which all
 * represent the same value.
 * </p>
 */
public class Fraction {

    private static final int[] POWER_OF_TEN = new int[] { 1, 10, 100, 1000, 10000, 100000 };

    public static Fraction of(String string) {
        if (string == null) {
            return null;
        }
        else if (string.contains("/")) {
            return ofRational(string);
        }
        else if (string.contains(".")) {
            return ofDecimal(string);
        }
        else {
            return ofInteger(string);
        }
    }

    private static Fraction ofRational(String string) {
        int slashIndex = string.indexOf('/');
        String lhs = string.substring(0, slashIndex);
        String rhs = string.substring(slashIndex + 1);
        int numerator = Integer.parseInt(lhs);
        int denominator = Integer.parseInt(rhs);

        return of(numerator, denominator);
    }

    // 0.01 -> (0 * 100 + 1)  / 100
    // 1.25 -> (1 * 100 + 25) / 100
    // 6.3  -> (6 * 10  + 3)  / 10
    // 18   -> (18 * 1  + 0)  / 1  (via ofInteger)
    private static Fraction ofDecimal(String string) {
        int dotIndex = string.indexOf('.');
        String lhs = string.substring(0, dotIndex);
        String rhs = string.substring(dotIndex + 1);
        int decimalPlaces = rhs.length();

        int lhsValue = Integer.parseInt(lhs);
        int rhsValue = Integer.parseInt(rhs);

        int denominator = POWER_OF_TEN[decimalPlaces];
        int numerator = lhsValue * denominator + rhsValue;

        return of(numerator, denominator);
    }

    private static Fraction ofInteger(String string) {
        int numerator = Integer.parseInt(string);

        return of(numerator, 1);
    }

    public static Fraction of(int numerator, int denominator) {
        // TODO! cache?
        // TODO! canonicalise (use GCD)
        return new Fraction(numerator, denominator);
    }

    private final int numerator;
    private final int denominator;

    private Fraction(int numerator, int denominator) {
        if (denominator <= 0) {
            throw new IllegalArgumentException("denominator must be a positive value");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!Fraction.class.isInstance(other)) {
            return false;
        }

        Fraction otherFraction = (Fraction) other;
        // Tolerates non-canonical forms.
        return this.denominator * otherFraction.numerator == otherFraction.denominator * this.numerator;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("numerator", numerator)
                .add("denominator", denominator)
                .toString();
    }

}
