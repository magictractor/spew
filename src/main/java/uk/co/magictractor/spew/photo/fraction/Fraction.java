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
import com.google.common.math.IntMath;

import uk.co.magictractor.spew.util.ArrayUtil;

/**
 * <p>
 * Used for fractional media property values, such as exposure and aperture
 * which are represented in different, but equivalent, ways by different tools.
 * </p>
 * <p>
 * For example, exposure may be stored as 1/100, 10/1000 and 0.01 which all
 * represent the same value.
 * </p>
 * <p>
 * The numerator and denominator are ints, so are not suitable for large values.
 * Additionally there are no arithmetic operations. This class is used to check
 * the equivalence of values in image files and sidecars, it is not suitable for
 * arithmetic with rational numbers.
 */
public class Fraction {

    private static final int[] POWER_OF_TEN = new int[] { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000,
            10000000, 100000000, 1000000000 };
    private static final int DEFAULT_DECIMAL_PLACES = 3;
    private static final ArrayList<DecimalFormat> FORMATTERS = new ArrayList<>();

    public static Fraction of(String string) {
        return safe(string, str -> of0(str), "Not a numeric or rational fraction or too big");
    }

    public static Fraction ofRational(String string) {
        return safe(string, str -> ofRational0(str), "Not a rational fraction or too big");
    }

    public static Fraction ofNumeric(String string) {
        return safe(string, str -> ofNumeric0(str), "Not a numeric fraction or too big");
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
        return new Fraction(numerator, denominator);
    }

    private final int numerator;
    private final int denominator;
    private Fraction canonicalForm;

    private Fraction(int numerator, int denominator) {
        if (denominator <= 0) {
            throw new IllegalArgumentException("denominator must be a positive value");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public Fraction getCanonicalForm() {
        if (canonicalForm == null) {
            int gcd = IntMath.gcd(numerator, denominator);
            if (gcd == 1) {
                canonicalForm = this;
            }
            else {
                canonicalForm = Fraction.of(numerator / gcd, denominator / gcd);
            }
        }

        return canonicalForm;
    }

    @Override
    public int hashCode() {
        // Use canonicalForm to ensure that hashCode() is equal when equals() is true.
        Fraction canonical = getCanonicalForm();

        // TODO! better implementation here?
        return canonical.numerator << 8 ^ canonical.denominator;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!Fraction.class.isInstance(other)) {
            return false;
        }

        Fraction otherFraction = (Fraction) other;
        return this.denominator * otherFraction.numerator == otherFraction.denominator * this.numerator;
    }

    public String toRationalString() {
        return numerator + "/" + denominator;
    }

    public String toNumericString() {
        return toNumericString(DEFAULT_DECIMAL_PLACES);
    }

    public String toNumericString(int decimalPlaces) {
        DecimalFormat formatter = ArrayUtil.ensureSizeAndComputeIfAbsent(FORMATTERS, decimalPlaces,
            this::createDecimalFormatter);

        double quotient = (double) numerator / denominator;
        return formatter.format(quotient);
    }

    private DecimalFormat createDecimalFormatter(int decimalPlaces) {
        String pattern = "#." + Strings.repeat("#", decimalPlaces);
        return new DecimalFormat(pattern);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("numerator", numerator)
                .add("denominator", denominator)
                .toString();
    }

}
