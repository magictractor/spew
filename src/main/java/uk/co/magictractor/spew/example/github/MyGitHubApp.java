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
package uk.co.magictractor.spew.example.github;

import uk.co.magictractor.spew.api.OAuth2Application;
import uk.co.magictractor.spew.api.OAuth2ServiceProvider;
import uk.co.magictractor.spew.provider.github.GitHub;

/**
 *
 */
public class MyGitHubApp implements OAuth2Application {

    @Override
    public OAuth2ServiceProvider getServiceProvider() {
        return GitHub.getInstance();
    }

    // https://developer.github.com/apps/building-oauth-apps/understanding-scopes-for-oauth-apps/#available-scopes
    @Override
    public String getScope() {
        return null;
    }

}
