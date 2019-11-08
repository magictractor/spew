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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 */
public final class ArrayUtil {

    private ArrayUtil() {
    }

    public static void ensureSize(List<?> list, int minimumSize) {
        if (list.size() >= minimumSize) {
            return;
        }

        if (list instanceof ArrayList) {
            ((ArrayList<?>) list).ensureCapacity(minimumSize);
        }

        int expand = minimumSize - list.size();
        while (expand-- > 0) {
            list.add(null);
        }
    }

    public static <T> T ensureSizeAndComputeIfAbsent(List<T> list, int index, Function<Integer, T> newValueFunction) {
        return computeIfAbsent0(list, index, newValueFunction, true);
    }

    /**
     * <p>
     * Behaves like {@link List#get(int))}, but additionally if {@code get()}
     * returns null, the function is applied and the result of the function is
     * set in the list and returned.
     * </p>
     * <p>
     * Similar to {@link Map#computeIfAbsent()}.
     * </p>
     */
    public static <T> T computeIfAbsent(List<T> list, int index, Function<Integer, T> newValueFunction) {
        return computeIfAbsent0(list, index, newValueFunction, false);
    }

    private static <T> T computeIfAbsent0(List<T> list, int index, Function<Integer, T> newValueFunction,
            boolean ensureSize) {
        T value = null;
        if (index < list.size()) {
            value = list.get(index);
        }
        else if (ensureSize) {
            ensureSize(list, index + 1);
        }

        if (value == null) {
            value = newValueFunction.apply(index);
            list.set(index, value);
        }

        return value;
    }

}
