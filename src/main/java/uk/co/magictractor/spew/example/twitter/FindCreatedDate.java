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
package uk.co.magictractor.spew.example.twitter;

import uk.co.magictractor.spew.api.ApplicationRequest;

/**
 * <p>
 * Find the created date for a Twitter account.
 * </p>
 *
 * @see https://developer.twitter.com/en/docs/accounts-and-users/follow-search-get-users/api-reference/get-users-show
 * @see https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/user-object
 */
public class FindCreatedDate {

    public void go() {
        MyTwitterApp app = MyTwitterApp.get();
        ApplicationRequest request = app.createGetRequest("https://api.twitter.com/1.1/users/show.json");

        request.setQueryStringParam("screen_name", "Edinbirder");

        request.sendRequest();
    }

    public static void main(String[] args) {
        // convert this to a processor? but there's no iterator?
        // could use user/lookup and create an iterator
        new FindCreatedDate().go();
    }

}
