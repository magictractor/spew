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
 * Optional interface which may be implemented by SPI service implementations
 * loaded via SPIUtil to indicate that the service should not be used.
 * </p>
 * <p>
 * May be used to restrict a service implementation to Windows or Linux
 * platforms.
 * </p>
 * <p>
 * SPIUtil additionally catches NoClassDefFoundError to deal with service
 * implementations which are missing a required dependency, implementing this
 * interface is not required for that case.
 * </p>
 */
public interface SPIAvailability {

    // TODO! return more than a boolean for logging purposes (log reason non-availability)
    boolean isAvailable();

}
