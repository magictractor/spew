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
import java.util.Collections;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;
import uk.co.magictractor.spew.core.contenttype.ApacheTikaContentTypeFromResourceName;
import uk.co.magictractor.spew.core.contenttype.ContentTypeFromResourceName;
import uk.co.magictractor.spew.core.contenttype.FallbackOctetStreamContentTypeFromResourceName;
import uk.co.magictractor.spew.core.contenttype.HardCodedContentTypeFromResourceName;
import uk.co.magictractor.spew.core.response.parser.DefaultParsedResponsePojoConverter;
import uk.co.magictractor.spew.core.response.parser.ParsedResponsePojoConverter;
import uk.co.magictractor.spew.core.response.parser.SpewHttpMessageBodyReaderFactory;
import uk.co.magictractor.spew.core.response.parser.jayway.JaywayResponseFactory;
import uk.co.magictractor.spew.core.response.parser.xpath.JavaXPathResponseFactory;
import uk.co.magictractor.spew.core.signature.MacSignatureGenerator;
import uk.co.magictractor.spew.core.signature.MessageDigestSignatureGenerator;
import uk.co.magictractor.spew.core.signature.SignatureGenerator;
import uk.co.magictractor.spew.http.apache.SpewApacheHttpClientConnectionFactory;
import uk.co.magictractor.spew.http.javaurl.SpewHttpUrlConnectionFactory;
import uk.co.magictractor.spew.oauth.boa.BoaConnectionFactory;
import uk.co.magictractor.spew.oauth.springsocial.spike.SpringSocialConnectionFactory;
import uk.co.magictractor.spew.photo.HardCodedTagLoader;
import uk.co.magictractor.spew.photo.IndentedFileTagLoader;
import uk.co.magictractor.spew.photo.TagLoader;
import uk.co.magictractor.spew.photo.local.DefaultLocalLibraryPathFinder;
import uk.co.magictractor.spew.photo.local.LocalLibraryPathFinder;
import uk.co.magictractor.spew.photo.local.dates.DefaultLocalDirectoryDatesStrategy;
import uk.co.magictractor.spew.photo.local.dates.LocalDirectoryDatesStrategy;
import uk.co.magictractor.spew.server.CallbackServer;
import uk.co.magictractor.spew.server.netty.NettyCallbackServer;
import uk.co.magictractor.spew.server.undertow.UndertowCallbackServer;
import uk.co.magictractor.spew.store.ApplicationPropertyStore;
import uk.co.magictractor.spew.store.ResourceFileApplicationPropertyStore;
import uk.co.magictractor.spew.store.UserPreferencePropertyStore;
import uk.co.magictractor.spew.store.UserPropertyStore;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public final class SPIUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SPIUtil.class);

    @SuppressWarnings("rawtypes")
    private static final Map<Class, List> DEFAULT_IMPLEMENTATION_TYPES = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private static final Map<Class, List> AVAILABLE_IMPLEMENTATIONS = new HashMap<>();

    static {
        addDefault(ApplicationPropertyStore.class, ResourceFileApplicationPropertyStore.class);

        addDefault(UserPropertyStore.class, UserPreferencePropertyStore.class);

        addDefault(SpewConnectionFactory.class, BoaConnectionFactory.class);
        addDefault(SpewConnectionFactory.class, SpringSocialConnectionFactory.class);
        // No auth connections. Boa connections wrap the first available one of these.
        addDefault(SpewConnectionFactory.class, SpewApacheHttpClientConnectionFactory.class);
        addDefault(SpewConnectionFactory.class, SpewHttpUrlConnectionFactory.class);

        addDefault(SpewHttpMessageBodyReaderFactory.class, JaywayResponseFactory.class);
        addDefault(SpewHttpMessageBodyReaderFactory.class, JavaXPathResponseFactory.class);

        addDefault(ParsedResponsePojoConverter.class, DefaultParsedResponsePojoConverter.class);

        // Could have a Wiremock implementation too
        addDefault(CallbackServer.class, UndertowCallbackServer.class);
        addDefault(CallbackServer.class, NettyCallbackServer.class);

        addDefault(ContentTypeFromResourceName.class, HardCodedContentTypeFromResourceName.class);
        addDefault(ContentTypeFromResourceName.class, ApacheTikaContentTypeFromResourceName.class);
        // Fallback to "application/octet-stream", only hit if Tika is not available. Tika has the same fallback behaviour.
        addDefault(ContentTypeFromResourceName.class, FallbackOctetStreamContentTypeFromResourceName.class);

        addDefault(SignatureGenerator.class, MacSignatureGenerator.class);
        addDefault(SignatureGenerator.class, MessageDigestSignatureGenerator.class);

        addDefault(TagLoader.class, IndentedFileTagLoader.class);
        addDefault(TagLoader.class, HardCodedTagLoader.class);

        addDefault(LocalLibraryPathFinder.class, DefaultLocalLibraryPathFinder.class);

        addDefault(LocalDirectoryDatesStrategy.class, DefaultLocalDirectoryDatesStrategy.class);
    }

    private static <T> void addDefault(Class<T> apiClass, Class<? extends T> implementationType) {
        @SuppressWarnings("unchecked")
        List<Class> implementationTypes = DEFAULT_IMPLEMENTATION_TYPES.get(apiClass);
        if (implementationTypes == null) {
            implementationTypes = new ArrayList<>();
            DEFAULT_IMPLEMENTATION_TYPES.put(apiClass, implementationTypes);
        }
        implementationTypes.add(implementationType);
    }

    private SPIUtil() {
    }

    /**
     * Resets the SPIUtil caches. For use with unit tests, which should
     * generally modify available implementations via SPIUtilExtension.
     */
    /* default */ static void reset() {
        AVAILABLE_IMPLEMENTATIONS.clear();
    }

    // TODO! change this to onlyAvailable()? Log warnings (once?) and ignore others if they exist?
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

    public static <T> Stream<T> available(Class<T> apiClass) {
        List<T> available = AVAILABLE_IMPLEMENTATIONS.get(apiClass);
        if (available == null) {
            synchronized (apiClass) {
                available = AVAILABLE_IMPLEMENTATIONS.get(apiClass);
                if (available == null) {
                    available = findAvailable(apiClass);
                    AVAILABLE_IMPLEMENTATIONS.put(apiClass, available);
                }
            }
        }

        return available.stream();
    }

    private static <T> List<T> findAvailable(Class<T> apiClass) {
        String sysImpl = System.getProperty(apiClass.getName());
        if (sysImpl != null) {
            T impl = loadSystemPropertyImplementation(sysImpl);
            return Collections.singletonList(impl);
        }

        ServiceLoader<T> serviceLoader = ServiceLoader.load(apiClass);
        List<T> candidates = serviceLoader.stream()
                .map(Provider::get)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            candidates = defaultImplementations(apiClass);
        }

        return candidates;
    }

    private static <T> T loadSystemPropertyImplementation(String implementationClassName) {
        return ExceptionUtil.call(() -> loadSystemPropertyImplementation0(implementationClassName));
    }

    @SuppressWarnings("unchecked")
    private static <T> T loadSystemPropertyImplementation0(String implementationClassName)
            throws ReflectiveOperationException {
        return (T) Class.forName(implementationClassName).getDeclaredConstructor().newInstance();
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> defaultImplementations(Class<T> apiClass) {
        List<Class<T>> defaultImplementationTypes = DEFAULT_IMPLEMENTATION_TYPES.get(apiClass);
        if (defaultImplementationTypes == null) {
            throw new IllegalStateException(
                "There are no SPI definitions or hard coded defaults for implementations of " + apiClass.getName());
        }

        ArrayList<T> defaultImplementations = new ArrayList<T>();
        for (Class<T> defaultImplementationType : defaultImplementationTypes) {
            T impl = newInstance(defaultImplementationType);
            if (impl != null) {
                defaultImplementations.add(impl);
            }
        }

        return defaultImplementations;
    }

    private static <T> T newInstance(Class<T> implClass) {
        try {
            // This unwraps InvocationTargetException;
            return ExceptionUtil.call(() -> newInstance0(implClass));
        }
        catch (NoClassDefFoundError e) {
            // Some default implementation are optional and only used if a dependency is provided.
            // To avoid seeing these message, provide SPI files stating which implementations are to be used.
            LOGGER.info(
                implClass.getName() + " is not available because it requires optional dependency "
                        + e.getMessage().replace('/', '.'));
            return null;
        }
        catch (RuntimeException e) {
            LOGGER.error(implClass.getName() + " is not available because there was an exception during construction",
                e);
            return null;
        }
    }

    private static <T> T newInstance0(Class<T> implClass) throws ReflectiveOperationException {
        return implClass.getDeclaredConstructor().newInstance();
    }

}
