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
package uk.co.magictractor.spew.example.twitter.pojo;

import com.google.common.base.MoreObjects;

// https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/user-object
public class TwitterUser {

    private String id_str;
    private String screen_name;
    private String name;
    // TODO! "protected" is a keyword
    //private boolean protected;
    private int followers_count;
    private int friends_count;

    public String getId() {
        return id_str;
    }

    // "@JohnSmith77"
    public String getScreenName() {
        return screen_name;
    }

    // "John Smith"
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("screenName", getScreenName())
                .add("name", getName())
                .toString();
    }

}
