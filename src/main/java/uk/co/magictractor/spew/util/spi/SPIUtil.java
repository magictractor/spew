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
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseFactory;
import uk.co.magictractor.spew.core.response.parser.jayway.JaywayResponseFactory;
import uk.co.magictractor.spew.http.apache.SpewApacheHttpClientConnectionFactory;
import uk.co.magictractor.spew.http.javaurl.SpewHttpUrlConnectionFactory;
import uk.co.magictractor.spew.oauth.boa.BoaConnectionFactory;
import uk.co.magictractor.spew.oauth.springsocial.spike.SpringSocialConnectionFactory;
import uk.co.magictractor.spew.store.ApplicationPropertyStore;
import uk.co.magictractor.spew.store.ResourceFileApplicationPropertyStore;
import uk.co.magictractor.spew.store.UserPreferencePropertyStore;
import uk.co.magictractor.spew.store.UserPropertyStore;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public final class SPIUtil {

    @SuppressWarnings("rawtypes")
    private static final Map<Class, List> DEFAULT_IMPLEMENTATIONS = new HashMap<>();

    static {
        addDefault(ApplicationPropertyStore.class, new ResourceFileApplicationPropertyStore());

        addDefault(UserPropertyStore.class, new UserPreferencePropertyStore());

        addDefault(SpewConnectionFactory.class, new BoaConnectionFactory());
        addDefault(SpewConnectionFactory.class, new SpringSocialConnectionFactory());
        // No auth connections. Boa connections wrap the first available one of these.
        addDefault(SpewConnectionFactory.class, new SpewApacheHttpClientConnectionFactory());
        addDefault(SpewConnectionFactory.class, new SpewHttpUrlConnectionFactory());

        addDefault(SpewParsedResponseFactory.class, new JaywayResponseFactory());
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

    public static <T> T firstAvailable(Class<T> apiClass) {
        return available(apiClass)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "There are no available implementations of " + apiClass.getName()));
    }

    public static <V, T> Optional<V> firstNotNull(Class<T> apiClass, Function<T, V> function) {
        return firstMatching(apiClass, function, v -> v != null);
    }

    public static <V, T> Optional<V> firstMatching(Class<T> apiClass, Function<T, V> function, Predicate<V> matcher) {
        return available(apiClass)
                .map(function)
                .filter(v -> matcher.test(v))
                .findFirst();
    }

    private static <T> Stream<T> available(Class<T> apiClass) {
        String sysImpl = System.getProperty(apiClass.getName());
        if (sysImpl != null) {
            T impl = loadAvailableSystemPropertyImplementation(sysImpl);
            return Stream.of(impl);
        }

        ServiceLoader<T> serviceLoader = ServiceLoader.load(apiClass);
        List<T> candidates = serviceLoader.stream()
                .map(Provider::get)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            candidates = defaultImplementations(apiClass);
        }

        return candidates.stream().filter(SPIUtil::isImplementationAvailable);
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
