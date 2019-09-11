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

import uk.co.magictractor.spew.api.SpewApplication;

public interface UserPropertyStore {

    /**
     * <p>
     * User properties are editable because user access tokens are stored once
     * access to the application has been verified.
     * </p>
     * <p>
     * An implementation could have a no-op for
     * {@link EditableProperty#setValue}, which would prevent the access token
     * being persisted and require the user to authorise access to the
     * application every time.
     * </p>
     */
    EditableProperty getProperty(SpewApplication<?> application, String propertyName);

}
