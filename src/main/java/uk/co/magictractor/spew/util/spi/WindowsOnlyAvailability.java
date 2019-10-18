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

/**
 * <p>
 * Optional interface which may be implemented by SPI service implementations to
 * indicate that the service should only be used if running on a Windows
 * platform.
 */
public interface WindowsOnlyAvailability extends SPIAvailability {

    @Override
    default boolean isAvailable() {
        // This is how java.util.prefs.Preferences checks for Windows, so likely to be robust.
        // Would be happy to use a lib for this, but I didn't find it in Guava.
        return System.getProperty("os.name").startsWith("Windows");
    }

}
