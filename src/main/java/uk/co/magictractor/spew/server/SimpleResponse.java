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
package uk.co.magictractor.spew.server;

/**
 * <p>
 * Simple responses are platform agnostic containers of information to be
 * displayed on a web page.
 * </p>
 * <p>
 * These can be used across multiple CallbackServer implementations.
 * </p>
 */
public abstract class SimpleResponse {

    private int httpStatus = 200;

    private boolean continueHandling;
    private boolean terminate;

    public SimpleResponse andTerminate() {
        terminate = true;
        return this;
    }

    public SimpleResponse andContinueHandling() {
        continueHandling = true;
        return this;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public boolean isContinueHandling() {
        return continueHandling;
    }

}
