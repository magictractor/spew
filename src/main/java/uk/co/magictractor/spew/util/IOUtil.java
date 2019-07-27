package uk.co.magictractor.spew.util;

import java.io.Closeable;
import java.io.IOException;

import uk.co.magictractor.spew.util.ExceptionUtil.ConsumerWithException;

public final class IOUtil {

    private IOUtil() {
    }

    public static <T extends Closeable, E extends Exception> void consumeThenClose(T closeable,
            ConsumerWithException<T, E> consumer) {
        ExceptionUtil.call(() -> consumeThenClose0(closeable, consumer));
    }

    public static <T extends Closeable, E extends Exception> void consumeThenClose0(T closeable,
            ConsumerWithException<T, E> consumer) throws E, IOException {
        try (T c = closeable) {
            consumer.accept(closeable);
        }
    }

}
