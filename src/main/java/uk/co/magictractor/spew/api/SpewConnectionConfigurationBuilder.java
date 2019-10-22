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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import uk.co.magictractor.spew.api.SpewConnectionConfigurationBuilder.SpewConnectionConfigurationImpl;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;

/**
 *
 */
public abstract class SpewConnectionConfigurationBuilder<CONFIG extends SpewConnectionConfiguration, IMPL extends SpewConnectionConfigurationImpl, APP extends SpewApplication<SP>, SP extends SpewServiceProvider, BUILDER extends SpewConnectionConfigurationBuilder<CONFIG, IMPL, APP, SP, BUILDER>> {

    private IMPL configuration;
    private List<CONFIG> next;

    protected abstract IMPL newInstance();

    protected SpewConnectionConfigurationBuilder() {
        configuration = newInstance();
    }

    public final CONFIG build() {
        CONFIG result = (CONFIG) configuration;

        if (next != null) {
            next.add(result);
            next = null;
        }

        return result;
    }

    protected IMPL configuration() {
        return configuration;
    }

    public final Supplier<CONFIG> nextBuild() {
        if (next == null) {
            next = new ArrayList<>();
        }
        List<CONFIG> holder = next;
        return () -> {
            return holder.get(0);
        };
    }

    @SuppressWarnings("unchecked")
    public BUILDER withApplication(APP application) {
        if (application.getServiceProvider() != null) {
            withServiceProvider(application.getServiceProvider());
        }

        withProperties(application.getProperties());

        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withServiceProvider(SP serviceProvider) {

        SpewConnectionConfigurationImpl configuration = this.configuration;

        if (configuration.typeAdapters == null) {
            configuration.typeAdapters = serviceProvider.getTypeAdapters();
        }

        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withProperties(Map<String, Object> properties) {
        SpewConnectionConfigurationImpl configuration = this.configuration;
        configuration.properties.putAll(properties);
        return (BUILDER) this;
    }

    public abstract static class SpewConnectionConfigurationImpl implements SpewConnectionConfiguration {

        private List<SpewTypeAdapter<?>> typeAdapters;
        private final Map<String, Object> properties = new LinkedHashMap<>();

        @Override
        public List<SpewTypeAdapter<?>> getTypeAdapters() {
            return typeAdapters;
        }

        @Override
        public void addProperties(Map<String, Object> properties) {
            properties.putAll(this.properties);
        }
    }

}
