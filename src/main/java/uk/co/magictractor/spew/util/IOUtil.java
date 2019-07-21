package uk.co.magictractor.spew.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;

import com.google.common.io.Closeables;

public final class IOUtil {

    private IOUtil() {
    }

    /**
     * @param bodyStream
     * @param object
     */
    public static <T extends Closeable> void consumeThenClose(T closeable, ConsumerWithIOException<T> consumer) {
        boolean swallowCloseException = true;
        try {
            consumer.accept(closeable);
            swallowCloseException = false;
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        finally {
            try {
                Closeables.close(closeable, swallowCloseException);
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @FunctionalInterface
    public static interface ConsumerWithIOException<T> {
        void accept(T t) throws IOException;
    }

}
