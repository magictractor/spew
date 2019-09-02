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
package uk.co.magictractor.spew.core.response.parser.jayway;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;
import uk.co.magictractor.spew.json.GsonTypeAdapter;

/**
 *
 */
public class JaywayConfigurationCache {

    private static final Map<SpewApplication<?>, Configuration> CONFIGURATION_MAP = new HashMap<>();

    public static Configuration getConfiguration(SpewApplication<?> application) {
        Configuration configuration = CONFIGURATION_MAP.get(application);
        if (configuration == null) {
            synchronized (CONFIGURATION_MAP) {
                configuration = CONFIGURATION_MAP.get(application);
                if (configuration == null) {
                    configuration = createConfiguration(application);
                    CONFIGURATION_MAP.put(application, configuration);
                }
            }
        }

        return configuration;
    }

    // TODO! move this to SPI? allow for advanced config...
    private static Configuration createConfiguration(SpewApplication<?> application) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        // TODO! allow application to add type adapters
        for (SpewTypeAdapter<?> spewTypeAdapter : application.getServiceProvider().getTypeAdapters()) {
            GsonTypeAdapter<?> gsonTypeAdapter = new GsonTypeAdapter<>(spewTypeAdapter);
            gsonBuilder.registerTypeAdapter(spewTypeAdapter.getType(), gsonTypeAdapter);
        }

        Gson gson = gsonBuilder.setPrettyPrinting().create();
        JsonProvider jsonProvider = new GsonJsonProvider(gson);
        MappingProvider mappingProvider = new GsonMappingProvider(gson);

        // Option.DEFAULT_PATH_LEAF_TO_NULL required for nextPageToken used with Google paged services
        return new Configuration.ConfigurationBuilder().jsonProvider(jsonProvider)
                .mappingProvider(mappingProvider)
                .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
                .build();
    }

}
