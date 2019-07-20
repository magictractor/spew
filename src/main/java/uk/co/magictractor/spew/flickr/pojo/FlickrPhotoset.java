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
package uk.co.magictractor.spew.flickr.pojo;

import java.time.Instant;

import com.google.common.base.MoreObjects;

import uk.co.magictractor.spew.photo.Album;

/**
 * Album or collection
 */
//{ "id": "72157706928593651", "primary": "40806373843", "secret": "9f5650cffd", "server": "65535", "farm": 66, "photos": 7, "videos": 0,
//    "title": { "_content": "RBGE 201904 Unidentified" },
//    "description": { "_content": "Images which I'm trying to identify." }, "needs_interstitial": 0, "visibility_can_see_set": 1, "count_views": 4, "count_comments": 0, "can_comment": 1, "date_create": "1557050307", "date_update": "1557310045" }
public class FlickrPhotoset implements Album {

    private String id;
    private Content title;
    private int photos;
    private int videos;
    private Instant date_create;
    private Instant date_update;

    @Override
    public String getTitle() {
        return title == null ? null : title._content;
    }

    private class Content {
        private String _content;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("title", getTitle())
                .add("date_create", date_create)
                .add("date_update", date_update)
                .toString();
    }

    @Override
    public String getServiceProviderId() {
        return id;
    }

    @Override
    public String getAlbumUrl() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public String getCoverPhotoBaseUrl() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public int getPhotoCount() {
        return photos;
    }

}
