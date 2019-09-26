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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestApacheTikaContentTypeFromResourceName extends AbstractTestContentTypeFromResourceName {

    private static ApacheTikaContentTypeFromResourceName TESTEE = new ApacheTikaContentTypeFromResourceName();

    @BeforeAll
    public static void setUpUnsupported() {
        // Likely to be supported with a future release of Tika.
        unsupported("cr3");
    }

    @Override
    protected String determineContentType(String resourceName) {
        return TESTEE.determineContentType(resourceName);
    }

    // Tika deviates from the IANA list and https://tools.ietf.org/html/rfc8081#page-9
    // which both say font/ttf
    @Override
    @Test
    public void testTtf() {
        check("font.ttf", "application/x-font-ttf");
    }

}
