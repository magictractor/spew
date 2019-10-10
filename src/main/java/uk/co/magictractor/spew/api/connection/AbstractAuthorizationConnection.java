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

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewAuthorizationVerifiedConnection;
import uk.co.magictractor.spew.api.SpewServiceProvider;

/**
 *
 */
public abstract class AbstractAuthorizationConnection<APP extends SpewApplication<SP>, SP extends SpewServiceProvider>
        extends AbstractConnection<APP, SP> implements SpewAuthorizationVerifiedConnection {

    @Override
    public void prepareApplicationRequest(OutgoingHttpRequest request) {
        ensureAuthorized(request);
    }

    private void ensureAuthorized(OutgoingHttpRequest request) {
        if (!hasExistingAuthorization()) {
            obtainAuthorization();
        }
        else {
            Instant expiry = authorizationExpiry();
            if (expiry != null) {
                if (Instant.now().isAfter(expiry)) {
                    getLogger().info("Authentication expired at {}", expiry);
                    boolean refreshed = refreshAuthorization();
                    if (!refreshed) {
                        obtainAuthorization();
                    }
                }
                else {
                    getLogger().debug("Authentication expires at {}", expiry);
                }
            }
            else {
                getLogger().debug("Authentication does not expire");
            }
        }
        addAuthorization(request);
    }

    protected abstract boolean hasExistingAuthorization();

    protected abstract Instant authorizationExpiry();

    protected abstract void obtainAuthorization();

    protected abstract boolean refreshAuthorization();

    protected abstract void addAuthorization(OutgoingHttpRequest request);

    // TODO! add resetUserAuthorization() and call it after 401.

    @Override
    public Map<String, Object> getProperties() {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        Instant expiry = authorizationExpiry();
        properties.put("Authorization expires", expiry != null);
        if (expiry != null) {
            properties.put("Authorization expiry", expiry);
        }
        // TODO! and "Has refresh token"

        return properties;
    }

}
