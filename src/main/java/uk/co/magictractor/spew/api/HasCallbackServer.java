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
package uk.co.magictractor.spew.api;

import java.util.List;
import java.util.function.BiFunction;

import uk.co.magictractor.spew.server.CallbackServer;
import uk.co.magictractor.spew.server.RequestHandler;

/**
 * Additional interface implemented by Applications which may use a callback
 * server to capture authorizations from service providers.
 */
public interface HasCallbackServer {

    /**
     * Request handlers which determine how callbacks from the server provider
     * are handled, plus perhaps static pages for redirecting to success or
     * failure messages after authorization plus supporting CSS files etc.
     */
    List<RequestHandler> getServerRequestHandlers(BiFunction<String, String, Boolean> verificationFunction);

    /**
     * <p>
     * The class relative to which resources for static web pages and templates
     * are found.
     * </p>
     * <p>
     * Applications may choose to override this and copy and modify the
     * resources in the new location. They may preserve their names to work with
     * the existing list of RequestHandlers, or getServerRequestHandlers() may
     * be overridden too.
     * </p>
     */
    default Class<?> serverResourcesRelativeToClass() {
        return CallbackServer.class;
    }

}
