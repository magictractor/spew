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
package uk.co.magictractor.spew.core.verification;

import java.util.Scanner;

import uk.co.magictractor.spew.api.SpewAuthorizationVerifiedConnection;
import uk.co.magictractor.spew.api.SpewVerifiedAuthConnectionConfiguration;
import uk.co.magictractor.spew.api.connection.SpewConnectionVerificationPendingCache;
import uk.co.magictractor.spew.util.IOUtil;

/**
 *
 */
public class ConsolePasteAuthVerificationHandler implements AuthVerificationHandler {

    private final SpewVerifiedAuthConnectionConfiguration connectionConfiguration;

    public ConsolePasteAuthVerificationHandler(SpewVerifiedAuthConnectionConfiguration connectionConfiguration) {
        this.connectionConfiguration = connectionConfiguration;
    }

    @Override
    public void preOpenAuthorizationInBrowser() {
        // Do nothing.
    }

    @Override
    public void postOpenAuthorizationInBrowser() {
        IOUtil.acceptThenClose(new Scanner(System.in), this::verify);
    }

    private void verify(Scanner scanner) {
        SpewAuthorizationVerifiedConnection connection = SpewConnectionVerificationPendingCache.removeSingleton();

        System.out.println("Enter verification code:");

        while (true) {
            String verificationCode = scanner.nextLine().trim();
            boolean verified = connection.verifyAuthorization(verificationCode);
            if (verified) {
                System.out.println("Verified successfully");
                return;
            }

            System.out.println("Verification failed, check the value which was copied and try again:");
        }
    }

    @Override
    public String getRedirectUri() {
        return connectionConfiguration.getOutOfBandUri();
    }

}
