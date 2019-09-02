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
package uk.co.magictractor.spew.core.contenttype;

/**
 * <p>
 * SPI interface for determining content type from a resource name.
 * </p>
 * <p>
 * For example, most implementations will be able to deduce that "picture.jpg"
 * has a content type of "image/jpeg" based on the ".jpg" extension.
 * </p>
 * <p>
 * Implementations are likely to be thin wrappers around third party libraries
 * or Java code. See https://www.baeldung.com/java-file-mime-type.
 * </p>
 * <p>
 * Note that URLConnection and FileNameMap are very limited and did not
 * determine content types for resource name "page.html" and "styles.css" when
 * tested.
 * </p>
 */
public interface ContentTypeFromResourceName {

    String determineContentType(String resourceName);

}
