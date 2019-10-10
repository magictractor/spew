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
package uk.co.magictractor.spew.core.signature;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MacSignatureGeneratorTest extends AbstractSignatureGeneratorTest {

    private static final byte[] EXPECTED_HMAC_MD5 = new byte[] { -3, 50, -32, 65, 93, 28, -48, -91, 14, 79, -9, 86, 87,
            -95, -60, 127 };
    private static final byte[] EXPECTED_HMAC_SHA1 = new byte[] { 100, 63, 50, 103, 10, 62, 73, 93, 90, 68, -20, -31,
            -59, 36, -120, -96, 44, 51, -117, 122 };
    private static final byte[] EXPECTED_HMAC_SHA1_ALT_KEY = new byte[] { 49, 23, -128, -18, -88, -80, -117, 4, 45,
            -121, -120, 13, 108, 102, -106, -32, -24, -92, -125, 11 };

    private MacSignatureGenerator testee = new MacSignatureGenerator();

    @Override
    SignatureGenerator testee() {
        return testee;
    }

    @Test
    public void testMD5() {
        assertThat(createSignature("MD5")).isNull();
    }

    @Test
    public void testHmacMD5() {
        assertThat(createSignature("HmacMD5")).isEqualTo(EXPECTED_HMAC_MD5);
    }

    @Test
    public void testHmacSHA1() {
        assertThat(createSignature("HmacSHA1")).isEqualTo(EXPECTED_HMAC_SHA1);
    }

    @Test
    public void testLowercase() {
        assertThat(createSignature("hmacsha1")).isEqualTo(EXPECTED_HMAC_SHA1);
    }

    @Test
    public void testUppercase() {
        assertThat(createSignature("HMACSHA1")).isEqualTo(EXPECTED_HMAC_SHA1);
    }

    // Flickr and Twitter use "HMAC-SHA1"
    @Test
    public void testHyphenated() {
        assertThat(createSignature("HMAC-SHA1")).isEqualTo(EXPECTED_HMAC_SHA1);
    }

    @Test
    public void testNormaliseBoundary() {
        assertThat(createSignature("HMAC")).isNull();
    }

    @Test
    public void testPBEWithHmacSHA1() {
        assertThat(createSignature("PBEWithHmacSHA1")).isNull();
    }

    @Test
    public void testHmacishDoesNotExist() {
        // Passes the "does this look like Hmac" test, but does not exist
        assertThat(createSignature("HmacGibberish")).isNull();
    }

    @Test
    public void testUsesKey() {
        changeKey();
        // Different result because Hmac uses a key. Hmac is a better choice for auth.
        assertThat(createSignature("HmacSHA1")).isEqualTo(EXPECTED_HMAC_SHA1_ALT_KEY);
    }

}
