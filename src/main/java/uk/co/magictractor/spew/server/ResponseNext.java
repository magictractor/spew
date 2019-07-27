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

public class ResponseNext {

    private SpewResponse redirect;
    private boolean terminate;

    public static ResponseNext redirect(SpewResponse redirect) {
        return new ResponseNext(redirect, false);
    }

    public static ResponseNext redirectAndShutdown(SpewResponse redirect) {
        return new ResponseNext(redirect, true);
    }

    public static ResponseNext shutdown() {
        return new ResponseNext(null, true);
    }

    /** Use static methods to get an instance. */
    private ResponseNext(SpewResponse redirect, boolean shutdown) {
        this.redirect = redirect;
        this.terminate = shutdown;

    }

    public SpewResponse redirect() {
        return redirect;
    }

    public boolean terminate() {
        return terminate;
    }

}
