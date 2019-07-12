/**
 * Copyright 2015-2019 Ken Dobson
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
package uk.co.magictractor.spew.oauth.springsocial.spike;

import java.util.Iterator;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.twitter.MyTwitterApp;
import uk.co.magictractor.spew.twitter.TweetIterator.TweetIteratorBuilder;
import uk.co.magictractor.spew.twitter.pojo.Tweet;

/**
 * <p>
 * Spike using Spring Social's OAuth to connect to Twitter.
 * </p>
 * <p>
 * https://docs.spring.io/spring-social/docs/2.0.0.M4/reference/htmlsingle/
 * </p>
 */
public class SpringSocialSpike {

    // TODO! broken because Twitter needs args to be set in query params of GET
    public static void main(String[] args) {
        // Same as else, but with different connection
        SpewConnection connection = new SpringSocialOAuth1Connection(new MyTwitterApp());
        Iterator<Tweet> iter = new TweetIteratorBuilder<>(connection, Tweet.class).build();
        while (iter.hasNext()) {
            Tweet tweet = iter.next();
            if (tweet.getLikes() >= 100) {
                System.out.println(tweet);
            }
        }
    }

}
