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

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.magictractor.spew.api.SpewHttpResponse;

public abstract class AbstractIncomingOnCloseResponse implements SpewHttpResponse {

    private final OnCloseInputStream bodyStream;

    protected AbstractIncomingOnCloseResponse(InputStream bodyStream) {
        this.bodyStream = new OnCloseInputStream(bodyStream);
    }

    @Override
    public final InputStream getBodyInputStream() {
        return bodyStream;
    }

    public abstract void onClose();

    private final class OnCloseInputStream extends FilterInputStream {

        protected OnCloseInputStream(InputStream inputStream) {
            super(inputStream.markSupported() ? inputStream : new BufferedInputStream(inputStream));
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            }
            finally {
                onClose();
            }
        }
    }

}
