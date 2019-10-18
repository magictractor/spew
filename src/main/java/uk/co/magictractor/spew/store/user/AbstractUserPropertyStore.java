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
package uk.co.magictractor.spew.store.user;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.store.EditableProperty;

/**
 *
 */
public class AbstractUserPropertyStore<PROP extends EditableProperty> implements UserPropertyStore {

    private final Map<String, PROP> propertyMap = new HashMap<>();
    private final BiFunction<SpewApplication<?>, String, PROP> mappingFunction;

    protected AbstractUserPropertyStore(BiFunction<SpewApplication<?>, String, PROP> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @Override
    public final EditableProperty getProperty(SpewApplication<?> application, String propertyName) {
        String key = application.getClass().getName() + ":" + propertyName;
        return propertyMap.computeIfAbsent(key, ignored -> mappingFunction.apply(application, propertyName));
    }

}
