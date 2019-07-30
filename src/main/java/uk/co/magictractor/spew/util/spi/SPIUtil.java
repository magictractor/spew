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
package uk.co.magictractor.spew.util.spi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

/**
 *
 */
public final class SPIUtil {

    private static final Map<Class, List> DEFAULT_IMPLEMENTATIONS = new HashMap<>();

    static {

    }

    private static <T> void addDefault(Class<T> apiClass, T implementation) {

    }

    private SPIUtil() {
    }

    public static <T> T loadFirstAvailable(Class<T> apiClass) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(apiClass);
        List<T> candidates = serviceLoader.stream()
                .map(Provider::get)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            candidates = defaultImplementations(apiClass);
        }

        for (T candidate : candidates) {
            if (candidate instanceof Availability) {
                if (!((Availability) candidate).isAvailable()) {
                    // Not available, perhaps due to a dependency on a third party library.
                    continue;
                }
            }

            return candidate;
        }

        throw new IllegalArgumentException("There are no available implementations of " + apiClass.getName());
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> defaultImplementations(Class<T> apiClass) {
        if (!DEFAULT_IMPLEMENTATIONS.containsKey(apiClass)) {
            throw new IllegalStateException(
                "There are no SPI definitions or hard coded defaults for implementations of " + apiClass.getName());
        }

        return DEFAULT_IMPLEMENTATIONS.get(apiClass);
    }

}
