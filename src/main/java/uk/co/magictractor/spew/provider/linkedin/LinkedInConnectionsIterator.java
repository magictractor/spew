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
package uk.co.magictractor.spew.provider.linkedin;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.ApplicationRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.linkedin.MyLinkedInApp;
import uk.co.magictractor.spew.example.linkedin.pojo.LinkedInConnection;

// nope! cannot do connections
public class LinkedInConnectionsIterator<E> extends AbstractLinkedInIterator<E> {

    @Override
    protected final List<E> fetchPage(int startAt) {
        // ApplicationRequest request = createGetRequest("https://api.linkedin.com/v2/connections");
        // https://api.linkedin.com/v2/socialActions/{shareUrn|ugcPostUrn|commentUrn}
        // aargh... "Not enough permissions to access ..."
        ApplicationRequest request = createGetRequest("https://api.linkedin.com/v2/socialActions/shareUrn");

        SpewParsedResponse response = request.sendRequest();

        return response.getList("$.elements", getElementType());
    }

    public static class LinkedInConnectionsIteratorBuilder<E>
            extends
            AbstractLinkedInIteratorBuilder<E, LinkedInConnectionsIterator<E>, LinkedInConnectionsIteratorBuilder<E>> {

        protected LinkedInConnectionsIteratorBuilder(SpewApplication<?> application, Class<E> elementType) {
            super(application, elementType, new LinkedInConnectionsIterator<>());
        }
    }

    public static void main(String[] args) {
        Iterator<LinkedInConnection> iter = new LinkedInConnectionsIteratorBuilder<>(MyLinkedInApp.get(),
            LinkedInConnection.class)
                    .build();
        while (iter.hasNext()) {
            LinkedInConnection connection = iter.next();
            System.err.println(connection);
        }
    }

}
