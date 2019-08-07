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
package uk.co.magictractor.spew.store;

import java.util.HashMap;
import java.util.Map;

import uk.co.magictractor.spew.api.SpewApplication;

/**
 *
 */
public class UserPreferencePropertyStore implements UserPropertyStore {

    private final Map<String, PreferencesProperty> propertyMap = new HashMap<>();

    @Override
    public EditableProperty getProperty(SpewApplication application, String propertyName) {
        String key = application.getClass().getName() + ":" + propertyName;
        return propertyMap.computeIfAbsent(key, k -> new PreferencesProperty(application, propertyName));
    }

}
