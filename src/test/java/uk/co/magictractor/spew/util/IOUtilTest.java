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
package uk.co.magictractor.spew.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.Closeable;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IOUtilTest {

    private Reader reader;

    @AfterEach
    public void afterEach() {
        if (reader == null) {
            return;
        }

        assertThatThrownBy(() -> reader.read())
                .isExactlyInstanceOf(IOException.class)
                .hasMessageContaining("closed");
    }

    @Test
    public void testConsumeThenClose_happyPath() {
        setUpReader();
        char[] charBuf = new char[1];
        IOUtil.applyThenClose(reader, r -> r.read(charBuf));

        assertThat(charBuf).isEqualTo(new char[] { 'h' });
    }

    @Test
    public void testConsumeThenClose_nullCloseable() {
        assertThatCode(
            () -> IOUtil.acceptThenClose(null, r -> {
            })).doesNotThrowAnyException();
    }

    @Test
    public void testConsumeThenClose_nullCloseableWithConsumerException() {
        // No additional exception from closing null Closeable.
        assertThatThrownBy(
            () -> IOUtil.acceptThenClose(null, r -> consumerThrowsExceptionThenClose(r, false)))
                    .isExactlyInstanceOf(UnsupportedOperationException.class)
                    .hasMessage("consumer problem")
                    .hasNoSuppressedExceptions();
    }

    @Test
    public void testConsumeThenClose_exceptionFromConsumer() throws IOException {
        setUpReader();
        reader.close();
        Exception e = Assertions.assertThrows(Exception.class, () -> consumerThrowsExceptionThenClose(reader, false));

        assertThat(e).isExactlyInstanceOf(UnsupportedOperationException.class)
                .hasMessage("consumer problem")
                .hasNoSuppressedExceptions();
    }

    @Test
    public void testConsumeThenClose_exceptionFromClose() throws IOException {
        setUpCloseOnceReader();
        assertThatThrownBy(
            () -> IOUtil.acceptThenClose(reader, r -> reader.close()))
                    .isExactlyInstanceOf(UncheckedIOException.class)
                    .hasCauseExactlyInstanceOf(IOException.class)
                    .satisfies(e -> e.getCause().getMessage().equals("Already closed"));
    }

    @Test
    public void testConsumeThenClose_exceptionsFromConsumerAndClose() {
        setUpCloseOnceReader();
        Exception e = Assertions.assertThrows(Exception.class, () -> consumerThrowsExceptionThenClose(reader, true));

        assertThat(e).isExactlyInstanceOf(UnsupportedOperationException.class)
                .hasMessage("consumer problem")
                .hasSuppressedException(new IOException("Already closed"));
    }

    private void consumerThrowsExceptionThenClose(Closeable closeable, boolean close) throws IOException {
        IOUtil.acceptThenClose(closeable, (c) -> consumerThrowsException0(c, close));
    }

    private void consumerThrowsException0(Closeable closeable, boolean close) throws IOException {
        if (close) {
            closeable.close();
        }
        throw new UnsupportedOperationException("consumer problem");
    }

    private void setUpReader() {
        reader = new StringReader("hello");
    }

    private void setUpCloseOnceReader() {
        reader = new CloseOnceReader(new StringReader("goodbye"));
    }

    /**
     * Reader used to force an IOException when the Reader is closed.
     */
    private final static class CloseOnceReader extends FilterReader {

        private boolean open = true;

        protected CloseOnceReader(Reader in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            if (!open) {
                throw new IOException("Already closed");
            }
            super.close();
            open = false;
        }
    }

}
