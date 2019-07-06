package uk.co.magictractor.spew.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.Callable;

public final class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static RuntimeException notYetImplemented() {
        return new UnsupportedOperationException("Not yet implemented");
    }

    public static void call(RunnableWithException runnable) {
        call(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T call(Callable<T> callable) {
        try {
            return (T) callable.call();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (IOException e) {
            throw new UncheckedIOException((IOException) e);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Exception;
    }

}