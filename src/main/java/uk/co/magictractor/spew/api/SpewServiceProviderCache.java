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
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public final class SpewServiceProviderCache {

    private static final Map<Class<?>, SpewServiceProvider> SERVICE_PROVIDERS = new HashMap<>();

    private SpewServiceProviderCache() {
    }

    @SuppressWarnings("unchecked")
    public static <SP extends SpewServiceProvider> SP getOrCreateForClass(Class<SP> serviceProviderClass) {
        SP serviceProvider = (SP) SERVICE_PROVIDERS.get(serviceProviderClass);
        if (serviceProvider == null) {
            synchronized (SERVICE_PROVIDERS) {
                serviceProvider = (SP) SERVICE_PROVIDERS.get(serviceProviderClass);
                if (serviceProvider == null) {
                    serviceProvider = ExceptionUtil.call(() -> createForClass(serviceProviderClass));
                }
            }
        }

        return serviceProvider;
    }

    private static <SP extends SpewServiceProvider> SP createForClass(Class<SP> serviceProviderClass)
            throws ReflectiveOperationException {
        Constructor<SP> constructor = serviceProviderClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    @SuppressWarnings("unchecked")
    public static <SP extends SpewServiceProvider> SP getOrCreateForApplication(SpewApplication<SP> application) {
        ParameterizedType type = (ParameterizedType) application.getClass().getGenericInterfaces()[0];
        Class<SP> serviceProviderClass = (Class<SP>) type.getActualTypeArguments()[0];

        return getOrCreateForClass(serviceProviderClass);
    }

}
