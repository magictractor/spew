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

/**
 *
 */
public class MessageDigestSignatureGeneratorTest extends AbstractSignatureGeneratorTest {

    private static final byte[] EXPECTED_MD5 = new byte[] { 51, -75, -126, -61, -6, -8, -96, -92, 31, -127, 37, 108,
            107, 86, 36, 87 };

    private MessageDigestSignatureGenerator testee = new MessageDigestSignatureGenerator();

    @Override
    SignatureGenerator testee() {
        return testee;
    }

    @Test
    public void testMD5() {
        assertThat(createSignature("MD5")).isEqualTo(EXPECTED_MD5);
    }

    @Test
    public void testHmacMD5() {
        assertThat(createSignature("HmacMD5")).isNull();
    }

    @Test
    public void testLowercase() {
        assertThat(createSignature("md5")).isEqualTo(EXPECTED_MD5);
    }

    @Test
    public void testMixedcase() {
        assertThat(createSignature("Md5")).isEqualTo(EXPECTED_MD5);
    }

    @Test
    public void testDoesNotUseKey() {
        changeKey();
        // Same result because MD5 does not use a key. Hmac is a better choice for auth.
        assertThat(createSignature("MD5")).isEqualTo(EXPECTED_MD5);
    }

}
