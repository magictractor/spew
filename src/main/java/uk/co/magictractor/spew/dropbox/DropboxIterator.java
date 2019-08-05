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
package uk.co.magictractor.spew.dropbox;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.PageTokenServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.dropbox.pojo.DropboxFileOrFolder;

/**
 *
 */
public class DropboxIterator<E> extends PageTokenServiceIterator<E> {

    private DropboxIterator() {
    }

    @Override
    protected PageAndNextToken<E> fetchPage(String pageToken) {
        String url = "https://api.dropboxapi.com/2/files/list_folder";
        if (pageToken != null) {
            url = url + "/continue";
        }

        // https://www.dropbox.com/developers/documentation/http/documentation#sharing-list_folders
        SpewRequest request = getApplication()
                .createPostRequest(url);

        if (pageToken != null) {
            request.setBodyParam("cursor", pageToken);
        }
        else {

            // TODO! move into a subclass
            // These params should only be set if pageToken is null.
            // The root folder must be specified as an empty string rather than slash.
            request.setBodyParam("path", "/");

            // page sizes may be restricted. More values than this may be returned.
            //request.setBodyParam("limit", 2);
        }

        SpewParsedResponse response = request.sendRequest();

        List<E> page = response.getList("$.entries", getElementType());

        String nextToken = null;
        boolean hasMore = response.getBoolean("$.has_more");
        if (hasMore) {
            // The response contains a cursor even if has_more is false.
            nextToken = response.getString("$.cursor");
        }

        return new PageAndNextToken<>(page, nextToken);
    }

    public static class DropboxIteratorBuilder<E>
            extends PageTokenServiceIteratorBuilder<E, DropboxIterator<E>, DropboxIteratorBuilder<E>> {

        public DropboxIteratorBuilder(SpewApplication application, Class<E> elementType) {
            super(application, elementType, new DropboxIterator<>());
        }

    }

    public static void main(String[] args) {
        Iterator<DropboxFileOrFolder> iterator = new DropboxIteratorBuilder<>(new MyDropboxApp(),
            DropboxFileOrFolder.class)
                    .build();
        while (iterator.hasNext()) {
            DropboxFileOrFolder folder = iterator.next();
            System.err.println(folder.getName());
        }
    }

}
