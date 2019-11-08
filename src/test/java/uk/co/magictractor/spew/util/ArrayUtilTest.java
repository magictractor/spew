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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.google.common.base.Strings;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayUtilTest {

    private static final Function<Integer, String> HEX_FUNCTION = i -> Strings.padStart(Integer.toHexString(i), 2, '0');

    @Test
    public void testEnsureSize_fromEmpty() {
        List<String> list = new ArrayList<>();
        ArrayUtil.ensureSize(list, 10);

        List<String> expected = Collections.nCopies(10, null);
        assertThat(list).isEqualTo(expected);
    }

    @Test
    public void testEnsureSize_remainEmpty() {
        List<String> list = new ArrayList<>();
        ArrayUtil.ensureSize(list, 0);

        List<String> expected = Collections.emptyList();
        assertThat(list).isEqualTo(expected);
    }

    @Test
    public void testEnsureSize_minimumIncreaseFromEmpty() {
        List<String> list = new ArrayList<>();
        ArrayUtil.ensureSize(list, 1);

        List<String> expected = Collections.singletonList(null);
        assertThat(list).isEqualTo(expected);
    }

    @Test
    public void testEnsureSize_noChange() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        ArrayUtil.ensureSize(list, 2);

        List<String> expected = Arrays.asList("a", "b");
        assertThat(list).isEqualTo(expected);
    }

    @Test
    public void testEnsureSize_minimumIncreaseFromNotEmpty() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        ArrayUtil.ensureSize(list, 3);

        List<String> expected = Arrays.asList("a", "b", null);
        assertThat(list).isEqualTo(expected);
    }

    @Test
    public void testEnsureSize_notArrayList() {
        List<String> list = new LinkedList<>();
        ArrayUtil.ensureSize(list, 4);

        List<String> expected = Collections.nCopies(4, null);
        assertThat(list).isEqualTo(expected);
    }

    @Test
    public void testComputeIfAbsent_computePerformed() {
        List<String> list = new ArrayList<>();
        list.add(null);
        list.add(null);
        String actual = ArrayUtil.computeIfAbsent(list, 1, HEX_FUNCTION);

        assertThat(actual).isEqualTo("01");
        List<String> expectedList = Arrays.asList(null, "01");
        assertThat(list).isEqualTo(expectedList);
    }

    @Test
    public void testComputeIfAbsent_computeNotPerformed() {
        List<String> list = new ArrayList<>();
        list.add(null);
        list.add("x");
        String actual = ArrayUtil.computeIfAbsent(list, 1, HEX_FUNCTION);

        assertThat(actual).isEqualTo("x");
        List<String> expectedList = Arrays.asList(null, "x");
        assertThat(list).isEqualTo(expectedList);
    }

    @Test
    public void testComputeIfAbsent_outOfBounds() {
        List<String> list = new ArrayList<>();

        Assertions.assertThatThrownBy(() -> ArrayUtil.computeIfAbsent(list, 0, HEX_FUNCTION))
                .isExactlyInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void testEnsureSizeAndComputeIfAbsent_fromEmpty() {
        List<String> list = new ArrayList<>();
        ArrayUtil.ensureSizeAndComputeIfAbsent(list, 3, HEX_FUNCTION);

        List<String> expected = Arrays.asList(null, null, null, "03");
        assertThat(list).isEqualTo(expected);
    }

    @Test
    public void testEnsureSizeAndComputeIfAbsent_minimumIncreaseFromEmpty() {
        List<String> list = new ArrayList<>();
        ArrayUtil.ensureSizeAndComputeIfAbsent(list, 0, HEX_FUNCTION);

        List<String> expected = Arrays.asList("00");
        assertThat(list).isEqualTo(expected);
    }

    @Test
    public void testEnsureSizeAndComputeIfAbsent_computeNotPerformed() {
        List<String> list = new ArrayList<>();
        list.add(null);
        list.add("x");
        ArrayUtil.ensureSizeAndComputeIfAbsent(list, 1, HEX_FUNCTION);

        List<String> expected = Arrays.asList(null, "x");
        assertThat(list).isEqualTo(expected);
    }

}
