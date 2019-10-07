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
package uk.co.magictractor.spew.util;

import java.util.Random;

import com.google.common.base.Strings;

/**
 *
 */
public final class RandomUtil {

    private static final int INT_HIGH_POSITIVE_BIT = 0x40000000;
    private static final long LONG_HIGH_POSITIVE_BIT = 0x4000000000000000L;

    private static final Random RNG = new Random();

    private RandomUtil() {
    }

    public static String nextHexInt() {
        String hex = Integer.toHexString(RNG.nextInt());
        return Strings.padStart(hex, 8, '0');
    }

    public static String nextHexLong() {
        String hex = Long.toHexString(RNG.nextLong());
        return Strings.padStart(hex, 16, '0');
    }

    public static int nextBigPositiveInt() {
        return toBigPositive(RNG.nextInt());
    }

    public static long nextBigPositiveLong() {
        return toBigPositive(RNG.nextLong());
    }

    public static int toBigPositive(int value) {
        return (value & Integer.MAX_VALUE) | INT_HIGH_POSITIVE_BIT;
    }

    public static long toBigPositive(long value) {
        return (value & Long.MAX_VALUE) | LONG_HIGH_POSITIVE_BIT;
    }

}
