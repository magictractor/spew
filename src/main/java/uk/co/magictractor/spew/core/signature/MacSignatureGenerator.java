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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.function.BiFunction;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 * https://docs.oracle.com/javase/9/docs/specs/security/standard-names.html#mac-algorithms
 */
public class MacSignatureGenerator extends AbstractSignatureGenerator {

    @Override
    protected BiFunction<byte[], byte[], byte[]> createSignatureFunction(String signatureMethod)
            throws NoSuchAlgorithmException {

        Mac mac = Mac.getInstance(signatureMethod);

        return (key, message) -> ExceptionUtil.call(() -> createSignature(mac, signatureMethod, key, message));
    }

    private byte[] createSignature(Mac mac, String signatureMethod, byte[] key, byte[] message)
            throws InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key, signatureMethod);
        mac.init(signingKey);
        return mac.doFinal(message);
    }

    @Override
    protected String normaliseSignatureMethod(String signatureMethod) {
        if (signatureMethod.toUpperCase().startsWith("HMAC")) {
            if (signatureMethod.length() > 4 && signatureMethod.charAt(4) == '-') {
                // Remove hyphen and change case.
                return "Hmac" + signatureMethod.substring(5).toUpperCase();
            }
            else {
                // Just change case.
                return "Hmac" + signatureMethod.substring(4).toUpperCase();
            }
        }
        return signatureMethod;
    }

    /*
     * PBEWithXxx methods are not accepted: PBEParameterSpec required for salt
     * and iteration count.
     */
    @Override
    protected boolean acceptSignatureMethod(String normalisedSignatureMethod) {
        return normalisedSignatureMethod.startsWith("Hmac");
    }

}
