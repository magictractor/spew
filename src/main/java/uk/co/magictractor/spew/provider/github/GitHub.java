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
package uk.co.magictractor.spew.provider.github;

import java.util.Collections;
import java.util.List;

import uk.co.magictractor.spew.api.SpewOAuth2ServiceProvider;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;

/**
 *
 */
// https://github.com/settings/apps
// https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/
// https://developer.github.com/apps/quickstart-guides/using-the-github-api-in-your-app/
// https://developer.github.com/v3/
// TODO! GitHub also supports BasicAuth
public class GitHub implements SpewOAuth2ServiceProvider {

    private GitHub() {
    }

    @Override
    public String oauth2AuthorizationUri() {
        return "https://github.com/login/oauth/authorize";
    }

    @Override
    public String oauth2TokenUri() {
        return "https://github.com/login/oauth/access_token";
    }

    @Override
    public List<SpewTypeAdapter<?>> getTypeAdapters() {
        return Collections.emptyList();
    }

}
