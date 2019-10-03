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
package uk.co.magictractor.spew.api.connection;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.SpewServiceProvider;

/**
 *
 */
public abstract class AbstractAuthorizationConnection<APP extends SpewApplication<SP>, SP extends SpewServiceProvider>
        extends AbstractConnection<APP, SP> {

    public AbstractAuthorizationConnection(APP application) {
        super(application);
        addBeforeSendRequestCallback(this::ensureAuthorized);
    }

    private void ensureAuthorized(OutgoingHttpRequest request) {
        if (!hasExistingAuthorization()) {
            obtainAuthorization();
        }
        addAuthorization(request);
    }

    protected abstract boolean hasExistingAuthorization();

    protected abstract void obtainAuthorization();

    protected abstract void addAuthorization(OutgoingHttpRequest request);

    @Override
    public SpewHttpResponse request(OutgoingHttpRequest request) {
        // TODO! add retry logic
        SpewHttpResponse response = super.request(request);

        // aah... parse first to let Flickr etc modify status codes... !!!!
        if (response.getStatus() == 401) {
            getLogger().info("Existing authorization failed, obtaining fresh authorization");
            obtainAuthorization();
            response = super.request(request);
        }

        // TODO! reauthorize (for 401? and retry for 5xx after a pause)
        // and do so for all connections...
        // Flickr has 200 status with error code 98 in body (could treat like 401 status)
        // and Flickr error code 105 could be treated like 5xx
        if (response.getStatus() != 200) {
            String message = "request returned status " + response.getStatus();
            System.err.println(message + ": " + request);
            System.err.println(response);
            throw new IllegalStateException(message);
        }

        return response;
    }

}
