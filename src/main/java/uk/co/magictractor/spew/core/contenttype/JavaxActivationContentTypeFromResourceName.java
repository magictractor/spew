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
package uk.co.magictractor.spew.core.contenttype;

//import jakarta.activation.MimetypesFileTypeMap;
import javax.activation.MimetypesFileTypeMap;

public class JavaxActivationContentTypeFromResourceName
        implements ContentTypeFromResourceName {

    private MimetypesFileTypeMap map = new MimetypesFileTypeMap();

    @Override
    public String determineContentType(String resourceName) {
        String contentType = map.getContentType(resourceName);
        // Convert fallback application/octet-stream to null.
        //        if ("application/octet-stream".equals(contentType) /*
        //                                                            * &&
        //                                                            * !isReallyOctetStream
        //                                                            * (resourceName)
        //                                                            */) {
        //            contentType = null;
        //        }

        return contentType;
    }

    private boolean isReallyOctetStream(String resourceName) {
        return resourceName.endsWith(".bin") || resourceName.endsWith(".octet-stream");
    }

}
