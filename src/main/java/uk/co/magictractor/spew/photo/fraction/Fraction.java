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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.Function;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

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

    // TODO! grow Array like FORMATTERS
    private static final int[] POWER_OF_TEN = new int[] { 1, 10, 100, 1000, 10000, 100000 };
    private static final int DEFAULT_DECIMAL_PLACES = 3;
    private static final ArrayList<DecimalFormat> FORMATTERS = new ArrayList<>();

    public static Fraction of(String string) {
        return safe(string, str -> of0(str), "Not a numeric or rational fraction");
    }

    public static Fraction ofRational(String string) {
        return safe(string, str -> ofRational0(str), "Not a rational fraction");
    }

    public static Fraction ofNumeric(String string) {
        return safe(string, str -> ofNumeric0(str), "Not a numeric fraction");
    }

    private static Fraction safe(String string, Function<String, Fraction> mapping, String errorMessage) {
        if (string == null) {
            return null;
        }

        try {
            return mapping.apply(string);
        }
        catch (RuntimeException e) {
            throw new IllegalArgumentException(errorMessage + ": " + string);
        }
    }

    private static Fraction of0(String string) {
        if (string.contains("/")) {
            return ofRational0(string);
        }
        else {
            return ofNumeric0(string);
        }
    }

    private static Fraction ofRational0(String string) {
        int slashIndex = string.indexOf('/');
        String lhs = string.substring(0, slashIndex);
        String rhs = string.substring(slashIndex + 1);
        int numerator = Integer.parseInt(lhs);
        int denominator = Integer.parseInt(rhs);

        return of(numerator, denominator);
    }

    private static Fraction ofNumeric0(String string) {
        if (string.contains(".")) {
            return ofDecimal0(string);
        }
        else {
            return ofInteger0(string);
        }
    }

    private static Fraction ofDecimal0(String string) {
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

    private static Fraction ofInteger0(String string) {
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

    // TODO! hashCode

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

    public String toRationalString() {
        return numerator + "/" + denominator;
    }

    public String toNumericString() {
        return toNumericString(DEFAULT_DECIMAL_PLACES);
    }

    public String toNumericString(int decimalPlaces) {
        DecimalFormat formatter = null;

        if (FORMATTERS.size() <= decimalPlaces) {
            FORMATTERS.ensureCapacity(decimalPlaces);
            int expand = decimalPlaces - FORMATTERS.size();
            while (expand-- >= 0) {
                FORMATTERS.add(null);
            }
        }
        else {
            formatter = FORMATTERS.get(decimalPlaces);
        }

        if (formatter == null) {
            String pattern = "#." + Strings.repeat("#", decimalPlaces);
            formatter = new DecimalFormat(pattern);
            FORMATTERS.set(decimalPlaces, formatter);
        }

        double quotient = (double) numerator / denominator;
        return formatter.format(quotient);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("numerator", numerator)
                .add("denominator", denominator)
                .toString();
    }

}
