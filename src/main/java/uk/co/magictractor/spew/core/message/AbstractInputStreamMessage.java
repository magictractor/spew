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

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

import uk.co.magictractor.spew.api.SpewHttpMessage;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.ExceptionUtil.SupplierWithException;

/**
 * Base class for response implementations which need to read and cache their
 * input stream because the third party library used for the connection will
 * automatically close the input stream. (TODO! update comment - could be other
 * reasons for using this)
 * <p>
 * This class should generally not be used for incoming requests and responses,
 * as it could result in the input stream being read from the server's main
 * thread rather than a worker thread (this behaviour seen with Undertow).
 * AbstractInputStreamMessage is a better choice for those.
 * </p>
 */
public abstract class AbstractInputStreamMessage implements SpewHttpMessage {

    // TODO! close() hooks
    private SupplierWithException<InputStream, IOException> bodySupplier;
    private byte[] bodyBytes;

    protected AbstractInputStreamMessage(SupplierWithException<InputStream, IOException> bodySupplier) {
        this.bodySupplier = bodySupplier;
    }

    @Override
    public byte[] getBodyBytes() {
        if (bodyBytes == null) {
            ExceptionUtil.call(() -> initBodyBytes0());
        }
        return bodyBytes;
    }

    private void initBodyBytes0() throws IOException {
        InputStream bodyInputStream = null;
        try {
            bodyInputStream = bodySupplier.get();
            bodyBytes = ByteStreams.toByteArray(bodyInputStream);
        }
        finally {
            if (bodyInputStream != null) {
                bodyInputStream.close();
            }
        }
        bodySupplier = null;
    }

}
