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
package uk.co.magictractor.spew.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class HttpMessageUtilTest {

    @Test
    public void testAsHeaderString() {
        long milli = 1567020003111L;
        Instant instant = Instant.ofEpochMilli(milli);
        assertThat(HttpMessageUtil.asHeaderString(instant)).isEqualTo("Wed, 28 Aug 2019 19:20:03 UTC");
    }

}
