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
package uk.co.magictractor.spew.core.message;

import java.nio.file.Path;

import uk.co.magictractor.spew.api.SpewHttpMessage;
import uk.co.magictractor.spew.util.PathUtil;

/**
 * <p>
 * Base class for messages which have the whole body available without blocking,
 * such as outgoing responses and dummy requests for unit tests based on local
 * files.
 * </p>
 * <p>
 * This class should generally not be used for incoming requests and responses,
 * as it could result in the input stream being read from the server's main
 * thread rather than a worker thread (this behaviour seen with Undertow).
 * AbstractInputStreamMessage is a better choice for those.
 * </p>
 */
public abstract class AbstractByteArrayMessage implements SpewHttpMessage {

    private final byte[] bodyBytes;

    protected AbstractByteArrayMessage(byte[] bytes) {
        this.bodyBytes = bytes;
    }

    protected AbstractByteArrayMessage(Path bodyPath) {
        bodyBytes = PathUtil.read(bodyPath);
    }

    @Override
    public byte[] getBodyBytes() {
        return bodyBytes;
    }

}
