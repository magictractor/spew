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
package uk.co.magictractor.spew.extra.registry;

import uk.co.magictractor.spew.store.user.AbstractUserPropertyStore;
import uk.co.magictractor.spew.util.spi.WindowsOnlyAvailability;

/**
 * <p>
 * User property store based on JNA access to the Windows registry using
 * {@link com.sun.jna.platform.win32.Advapi32Util}.
 * </p>
 * <p>
 * TODO! explain differences between this and java.util.prefs.Preferences.
 * </p>
 */
public class JnaWindowsRegistryUserPropertyStore extends AbstractUserPropertyStore<JnaWindowsRegistryProperty>
        implements WindowsOnlyAvailability {

    public JnaWindowsRegistryUserPropertyStore() {
        super((app, key) -> new JnaWindowsRegistryProperty(app, key));
    }

}
