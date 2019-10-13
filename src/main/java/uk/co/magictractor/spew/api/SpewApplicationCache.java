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
package uk.co.magictractor.spew.api;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public final class SpewApplicationCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpewApplicationCache.class);

    /** Keyed by application id. */
    // hmm... maybe key by class...
    private static final Map<Integer, SpewApplication<?>> CACHE = new HashMap<>();

    private SpewApplicationCache() {
    }

    public static Optional<SpewApplication<?>> get(String applicationId) {
        return get(Integer.parseInt(applicationId));
    }

    public static Optional<SpewApplication<?>> get(Integer applicationId) {
        return Optional.ofNullable(CACHE.get(applicationId));
    }

    public static Collection<SpewApplication<?>> all() {
        return CACHE.values();
    }

    public static <APP extends SpewApplication<?>> APP add(Class<APP> applicationClass) {
        SpewApplication<?> application;

        synchronized (CACHE) {
            application = createApplication(applicationClass);
            if (CACHE.containsKey(application.getId())) {
                // TODO! link to a troubleshooting page?
                // Another instance of the same class, or dodgy overrides of getId()?
                // Could do more checks here (compare classes).
                throw new IllegalArgumentException(
                    "There is already an existing application instance with the same id");
            }

            CACHE.put(application.getId(), application);
        }

        return (APP) application;
    }

    private static SpewApplication<?> createApplication(Class<? extends SpewApplication<?>> applicationClass) {
        // Goes boom if there is no auth type
        SpewAuthTypeUtil.getAuthType(applicationClass);
        return ExceptionUtil.call(() -> newInstance(applicationClass));
    }

    private static SpewApplication<?> newInstance(Class<? extends SpewApplication<?>> applicationClass)
            throws ReflectiveOperationException {
        Constructor<? extends SpewApplication<?>> constructor = applicationClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        SpewApplication<?> newInstance = constructor.newInstance();
        return newInstance;
    }

}
