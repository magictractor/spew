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
package uk.co.magictractor.spew.access;

public class AuthorizationResult {

    //    private final AuthorizationType type;
    private final String authToken;
    private final String verificationCode;

    //    public static AuthorizationResult verification(String verificationCode) {
    //        return new AuthorizationResult(AuthorizationType.VERIFICATION_CODE, verificationCode);
    //    }
    //
    //    public static AuthorizationResult authToken(String authToken) {
    //        return new AuthorizationResult(AuthorizationType.AUTH_TOKEN, authToken);
    //    }

    /** Use static methods to create an instance. */
    //private AuthorizationResult(AuthorizationType type, String value) {
    //    this.type = type;
    //        this.value = value;
    //    }

    public AuthorizationResult(String authToken, String verificationCode) {
        if (authToken == null) {
            throw new IllegalArgumentException("authToken must not be null");
        }
        if (verificationCode == null) {
            throw new IllegalArgumentException("verificationCode must not be null");
        }
        this.authToken = authToken;
        this.verificationCode = verificationCode;
    }

    public AuthorizationResult(String verificationCode) {
        if (verificationCode == null) {
            throw new IllegalArgumentException("verificationCode must not be null");
        }
        this.authToken = null;
        this.verificationCode = verificationCode;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

}
