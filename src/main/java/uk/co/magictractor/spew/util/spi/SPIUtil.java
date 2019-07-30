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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import uk.co.magictractor.spew.api.boa.BoaConnectionInit;
import uk.co.magictractor.spew.api.connection.SpewConnectionInit;
import uk.co.magictractor.spew.oauth.springsocial.spike.SpringSocialConnectionInit;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public final class SPIUtil {

    @SuppressWarnings("rawtypes")
    private static final Map<Class, List> DEFAULT_IMPLEMENTATIONS = new HashMap<>();

    static {
        addDefault(SpewConnectionInit.class, new BoaConnectionInit());
        addDefault(SpewConnectionInit.class, new SpringSocialConnectionInit());
    }

    private static <T> void addDefault(Class<T> apiClass, T implementation) {
        @SuppressWarnings("unchecked")
        List<T> implementations = DEFAULT_IMPLEMENTATIONS.get(apiClass);
        if (implementations == null) {
            implementations = new ArrayList<>();
            DEFAULT_IMPLEMENTATIONS.put(apiClass, implementations);
        }
        implementations.add(implementation);
    }

    private SPIUtil() {
    }

    public static <T> T loadFirstAvailable(Class<T> apiClass) {
        String sysImpl = System.getProperty(apiClass.getName());
        if (sysImpl != null) {
            return loadAvailableSystemPropertyImplementation(sysImpl);
        }

        ServiceLoader<T> serviceLoader = ServiceLoader.load(apiClass);
        List<T> candidates = serviceLoader.stream()
                .map(Provider::get)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            candidates = defaultImplementations(apiClass);
        }

        for (T candidate : candidates) {
            if (!isImplementationAvailable(candidate)) {
                // Not available, perhaps due to a dependency on a third party library.
                continue;
            }

            return candidate;
        }

        throw new IllegalArgumentException("There are no available implementations of " + apiClass.getName());
    }

    private static boolean isImplementationAvailable(Object implementation) {
        if (implementation instanceof Availability) {
            if (!((Availability) implementation).isAvailable()) {
                // Not available, perhaps due to a dependency on a third party library.
                return false;
            }
        }
        return true;
    }

    private static <T> T loadAvailableSystemPropertyImplementation(String implementationClassName) {
        T implementation = loadSystemPropertyImplementation(implementationClassName);
        if (!isImplementationAvailable(implementation)) {
            throw new IllegalArgumentException(implementationClassName + ".isAvailable() returns false");
        }

        return implementation;
    }

    private static <T> T loadSystemPropertyImplementation(String implementationClassName) {
        return ExceptionUtil.call(() -> loadSystemPropertyImplementation0(implementationClassName));
    }

    private static <T> T loadSystemPropertyImplementation0(String implementationClassName)
            throws ReflectiveOperationException {
        return (T) Class.forName(implementationClassName).newInstance();
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
