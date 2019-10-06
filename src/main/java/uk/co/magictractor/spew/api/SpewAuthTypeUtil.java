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

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Iterables;

/**
 *
 */
public final class SpewAuthTypeUtil {

    private SpewAuthTypeUtil() {
    }

    public static String getAuthType(Class<? extends SpewApplication> applicationClass) {
        Set<SpewAuthType> authTypes = new HashSet<>();
        Class<?>[] ifaces = applicationClass.getInterfaces();
        // TODO! check super interfaces too.
        // TODO! and should check the class
        // TODO! generally smells bad... replace with something simpler...
        for (Class<?> iface : ifaces) {
            SpewAuthType annotation = iface.getAnnotation(SpewAuthType.class);
            if (annotation != null) {
                authTypes.add(annotation);
            }
        }

        if (authTypes.isEmpty()) {
            throw new IllegalArgumentException(
                applicationClass.getName() + " does not implement any interfaces indicating the auth type");
        }

        if (authTypes.size() > 1) {
            throw new IllegalArgumentException(applicationClass.getName()
                    + " should implement a single interfaces indicating the auth type, but implements " + authTypes);
        }

        return Iterables.getOnlyElement(authTypes).value();
    }

}
