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
package uk.co.magictractor.spew.provider.facebook;

import uk.co.magictractor.spew.api.SpewOAuth2ServiceProvider;

/**
 * Creating a Facebook app requires a developer account. Creating a developer
 * account requires adding a telephone number to the account.
 */
// https://developers.facebook.com/docs/apps/
// https://developers.facebook.com/docs/graph-api/using-graph-api/
// https://developers.facebook.com/docs/graph-api/using-graph-api/#paging
// https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow
public class Facebook implements SpewOAuth2ServiceProvider {

    private Facebook() {
    }

    @Override
    public String getAuthorizationUri() {
        return "https://www.facebook.com/v4.0/dialog/oauth";
    }

    @Override
    public String getTokenUri() {
        return "https://graph.facebook.com/v4.0/oauth/access_token";
    }

}
