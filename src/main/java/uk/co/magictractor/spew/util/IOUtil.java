package uk.co.magictractor.spew.util;

import java.io.Closeable;
import java.io.IOException;

import uk.co.magictractor.spew.util.ExceptionUtil.ConsumerWithException;
import uk.co.magictractor.spew.util.ExceptionUtil.FunctionWithException;

public final class IOUtil {

    private IOUtil() {
    }

    public static <T extends Closeable, E extends Exception> void acceptThenClose(T closeable,
            ConsumerWithException<T, E> consumer) {
        applyThenClose(closeable, (t) -> {
            consumer.accept(t);
            return null;
        });
    }

    public static <T extends Closeable, R, E extends Exception> R applyThenClose(T closeable,
            FunctionWithException<T, R, E> consumer) {
        return ExceptionUtil.call(() -> applyThenClose0(closeable, consumer));
    }

    private static <T extends Closeable, R, E extends Exception> R applyThenClose0(T closeable,
            FunctionWithException<T, R, E> consumer) throws E, IOException {
        try (T c = closeable) {
            return consumer.apply(closeable);
        }
    }

}
