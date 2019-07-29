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

import java.util.Scanner;

/**
 *
 */
public class PasteVerificationCodeHandler implements AuthorizationHandler {

    // TODO! "oob" is not correct for OAuth2 ("pin" or "code")
    @Override
    public String getCallbackValue() {
        return "oob";
    }

    @Override
    public AuthorizationResult getResult() {
        System.err.println("Enter verification code: ");
        try (Scanner scanner = new Scanner(System.in)) {
            return new AuthorizationResult(scanner.nextLine().trim());
        }
    }

}