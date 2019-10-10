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

import java.security.NoSuchAlgorithmException;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class AbstractSignatureGenerator implements SignatureGenerator {

    //    private static final byte[] DEFAULT_VERIFY_KEY = "key".getBytes(StandardCharsets.UTF_8);
    //    private static final byte[] DEFAULT_VERIFY_MESSAGE = "message".getBytes(StandardCharsets.UTF_8);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final BiFunction<byte[], byte[], byte[]> signatureFunction(String signatureMethod) {
        String normalisedSignatureMethod = normaliseSignatureMethod(signatureMethod);

        if (!acceptSignatureMethod(normalisedSignatureMethod)) {
            return null;
        }

        try {
            return createSignatureFunction(normalisedSignatureMethod);
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    protected abstract BiFunction<byte[], byte[], byte[]> createSignatureFunction(String signatureMethod)
            throws NoSuchAlgorithmException;

    protected String normaliseSignatureMethod(String signatureMethod) {
        return signatureMethod;
    }

    protected boolean acceptSignatureMethod(String signatureMethod) {
        return true;
    }

    //    private boolean verifyFunction(BiFunction<byte[], byte[], byte[]> signatureMethodFunction) {
    //        boolean verified = false;
    //        signatureMethodFunction.apply(verifyWithKey(), verifyWithMessage());
    //        verified = true;
    //
    //        return verified;
    //    }
    //
    //    protected byte[] verifyWithKey() {
    //        return DEFAULT_VERIFY_KEY;
    //    }
    //
    //    protected byte[] verifyWithMessage() {
    //        return DEFAULT_VERIFY_MESSAGE;
    //    }

    protected Logger getLogger() {
        return logger;
    }

}
