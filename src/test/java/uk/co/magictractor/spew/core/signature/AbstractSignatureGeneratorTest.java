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

import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSignatureGeneratorTest {

    private static final byte[] DEFAULT_KEY = "key".getBytes(StandardCharsets.UTF_8);
    private static final byte[] ALTERNATE_KEY = "another key".getBytes(StandardCharsets.UTF_8);
    private static final byte[] MESSAGE = "blah blah blah blah blah".getBytes(StandardCharsets.UTF_8);

    private Logger logger = LoggerFactory.getLogger(getClass());

    private byte[] key = DEFAULT_KEY;

    protected byte[] createSignature(String signatureMethod) {
        BiFunction<byte[], byte[], byte[]> signatureFunction = testee().signatureFunction(signatureMethod);
        if (signatureFunction == null) {
            return null;
        }

        byte[] signature = signatureFunction.apply(key, MESSAGE);
        logger.debug("signature with {}: {}", signatureMethod, signature);

        return signature;
    }

    abstract SignatureGenerator testee();

    @Test
    public void testNull() {
        Assertions.assertThatThrownBy(() -> createSignature(null)).isExactlyInstanceOf(NullPointerException.class);
    }

    protected void changeKey() {
        key = ALTERNATE_KEY;
    }

}
