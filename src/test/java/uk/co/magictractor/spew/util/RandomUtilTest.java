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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class RandomUtilTest {

    private static final int MIN_BIG_POSITIVE_INT = 0x40000000;
    private static final long MIN_BIG_POSITIVE_LONG = 0x4000000000000000L;

    @Test
    public void testToBigPositiveInt_fromZero() {
        int big = RandomUtil.toBigPositive(0);
        assertThat(big).isEqualTo(MIN_BIG_POSITIVE_INT);
    }

    @Test
    public void testToBigPositiveInt_fromMaxLong() {
        int big = RandomUtil.toBigPositive(Integer.MAX_VALUE);
        assertThat(big).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testToBigPositiveInt_fromMinusOne() {
        // Minus one has all bits set.
        int big = RandomUtil.toBigPositive(-1);
        assertThat(big).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testToBigPositiveInt_fromMinLong() {
        int big = RandomUtil.toBigPositive(Integer.MIN_VALUE);
        assertThat(big).isEqualTo(MIN_BIG_POSITIVE_INT);
    }

    @Test
    public void textNextBigPositiveInt() {
        for (int i = 1; i < 100; i++) {
            int big = RandomUtil.nextBigPositiveInt();
            assertThat(big).isGreaterThanOrEqualTo(MIN_BIG_POSITIVE_INT);
            assertThat(big).isLessThanOrEqualTo(Integer.MAX_VALUE);
        }
    }

    @Test
    public void textNextHexInt() {
        Map<Integer, Map<Character, Integer>> hexDistibutionMaps = new HashMap<>();

        for (int i = 1; i <= 1000; i++) {
            String hex = RandomUtil.nextHexInt();
            assertThat(hex).hasSize(8);
            assertThat(hex).matches(this::isHex);
            addHexDistribution(hexDistibutionMaps, hex);
        }

        checkHexDistribution(hexDistibutionMaps);
    }

    @Test
    public void testToBigPositiveLong_fromZero() {
        long big = RandomUtil.toBigPositive(0L);
        assertThat(big).isEqualTo(MIN_BIG_POSITIVE_LONG);
    }

    @Test
    public void testToBigPositiveLong_fromMaxLong() {
        long big = RandomUtil.toBigPositive(Long.MAX_VALUE);
        assertThat(big).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testToBigPositiveLong_fromMinusOne() {
        // Minus one has all bits set.
        long big = RandomUtil.toBigPositive(-1L);
        assertThat(big).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testToBigPositiveLong_fromMinLong() {
        long big = RandomUtil.toBigPositive(Long.MIN_VALUE);
        assertThat(big).isEqualTo(MIN_BIG_POSITIVE_LONG);
    }

    @Test
    public void textNextBigPositiveLong() {
        for (int i = 1; i < 1000; i++) {
            long big = RandomUtil.nextBigPositiveLong();
            assertThat(big).isGreaterThanOrEqualTo(MIN_BIG_POSITIVE_LONG);
            assertThat(big).isLessThanOrEqualTo(Long.MAX_VALUE);
        }
    }

    @Test
    public void textNextHexLong() {
        Map<Integer, Map<Character, Integer>> hexDistibutionMaps = new HashMap<>();

        for (int i = 1; i <= 1000; i++) {
            String hex = RandomUtil.nextHexLong();
            assertThat(hex).hasSize(16);
            assertThat(hex).matches(this::isHex);
            addHexDistribution(hexDistibutionMaps, hex);
        }

        checkHexDistribution(hexDistibutionMaps);
    }

    private boolean isHex(String hex) {
        for (char c : hex.toCharArray()) {
            if (!isHex(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean isHex(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f');
    }

    private void addHexDistribution(Map<Integer, Map<Character, Integer>> hexDistibutionMaps, String hex) {
        int i = 0;
        for (char c : hex.toCharArray()) {
            Map<Character, Integer> hexDistibutionMap = hexDistibutionMaps.computeIfAbsent(i++, HashMap::new);
            Integer prevCount = hexDistibutionMap.getOrDefault(c, 0);
            hexDistibutionMap.put(c, prevCount + 1);
        }
    }

    private void checkHexDistribution(Map<Integer, Map<Character, Integer>> hexDistibutionMaps) {
        // Pick any one to get the total.
        Integer sampleSize = hexDistibutionMaps.get(3).values().stream().reduce(0, Integer::sum);
        // Could do something better to calculate expected min/max, since its a uniform distribution with a known sample size.
        // For 1000 average is 62.5 (1000/16).
        float average = sampleSize / 16;
        // For sample size 100, this gives expected min 18, expected max 106.
        float allowVariance = 0.5f + (0.7f * average);
        int expectedMin = Math.round(average - allowVariance);
        int expectedMax = Math.round(average + allowVariance);

        for (Map<Character, Integer> hexDistibutionMap : hexDistibutionMaps.values()) {
            for (int count : hexDistibutionMap.values()) {
                assertThat(count).isBetween(expectedMin, expectedMax);
            }
        }
    }

}
