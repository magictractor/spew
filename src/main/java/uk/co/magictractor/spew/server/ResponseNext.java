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

import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.core.response.ResourceResponse;

public class ResponseNext {

    private Type type;
    private String redirect;
    private SpewResponse response;
    private boolean continueHandling;
    private boolean terminate;

    /** Use static methods to get an instance. */
    private ResponseNext() {
    }

    public static ResponseNext redirect(String redirect) {
        ResponseNext next = new ResponseNext();
        next.setType(Type.REDIRECT);
        next.redirect = redirect;
        return next;
    }

    public static ResponseNext response(SpewResponse response) {
        ResponseNext next = new ResponseNext();
        next.setType(Type.RESPONSE);
        next.response = response;
        return next;
    }

    public static ResponseNext response(String resourceName) {
        return response(new ResourceResponse(resourceName));
    }

    private void setType(Type type) {
        if (this.type != null) {
            throw new IllegalStateException("Already has type " + this.type);
        }
        this.type = type;
    }

    /**
     * Not static. Typically used directly after one of the static instance
     * creation methods. For example
     *
     * <pre>
     * ResponseNext.respond("happy.html").andTerminate()
     * </pre>
     */
    public ResponseNext andTerminate() {
        terminate = true;
        return this;
    }

    public ResponseNext andContinueHandling() {
        continueHandling = true;
        return this;
    }

    public Type getType() {
        return type;
    }

    public String getRedirect() {
        return redirect;
    }

    public SpewResponse getResponse() {
        return response;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public boolean isContinueHandling() {
        return continueHandling;
    }

    public static enum Type {
        NONE, REDIRECT, RESPONSE;
    }

}
