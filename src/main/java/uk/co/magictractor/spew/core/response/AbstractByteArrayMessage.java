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
package uk.co.magictractor.spew.core.response;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteStreams;

import uk.co.magictractor.spew.api.SpewHttpMessage;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 * Base class for response implementations which need to read and cache their
 * input stream because the third party library used for the connection will
 * automatically close the input stream. (TODO! update comment - could be other
 * reasons for using this)
 */
public abstract class AbstractByteArrayMessage implements SpewHttpMessage {

    private final byte[] bodyBytes;

    protected AbstractByteArrayMessage(Path bodyPath) {
        // Content-Length likely to be in header, scope for optimisation here.
        this(ExceptionUtil.call(() -> Files.newInputStream(bodyPath)));
    }

    protected AbstractByteArrayMessage(InputStream bodyInputStream) {
        // Content-Length likely to be in header, scope for optimisation here.
        this(ExceptionUtil.call(() -> ByteStreams.toByteArray(bodyInputStream)));
        ExceptionUtil.call(() -> bodyInputStream.close());
    }

    protected AbstractByteArrayMessage(byte[] bytes) {
        this.bodyBytes = bytes;
    }

    @Override
    public byte[] getBodyBytes() {
        return bodyBytes;
    }

}
