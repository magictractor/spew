package uk.co.magictractor.spew.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public final class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static RuntimeException notYetImplemented() {
        return new UnsupportedOperationException("Not yet implemented");
    }

    public static <E extends Exception> void call(RunnableWithException<E> runnable) {
        call(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T call(Callable<T> callable) {
        try {
            return callable.call();
        }
        catch (InvocationTargetException e) {
            throw unwrap(e);
        }
        catch (Exception e) {
            throw asRuntimeException(e);
        }
    }

    private static RuntimeException unwrap(Throwable e) {
        if (e instanceof InvocationTargetException) {
            return (unwrap(e.getCause()));
        }
        return asRuntimeException(e);
    }

    private static RuntimeException asRuntimeException(Throwable e) {
        return asRuntimeException(null, e);
    }

    public static RuntimeException asRuntimeException(String message, Throwable e) {
        if (e instanceof RuntimeException) {
            // hmm, message lost...
            return (RuntimeException) e;
        }
        else if (e instanceof Error) {
            // hmm, message lost...
            throw (Error) e;
        }
        else if (e instanceof IOException) {
            return new UncheckedIOException(message, (IOException) e);
        }
        return new IllegalStateException(message, e);
    }

    @FunctionalInterface
    public interface RunnableWithException<E extends Exception> {
        void run() throws E;
    }

    @FunctionalInterface
    public static interface FunctionWithException<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public static interface BiFunctionWithException<T, U, R, E extends Exception> {
        R apply(T t, U u) throws E;
    }

    @FunctionalInterface
    public static interface ConsumerWithException<T, E extends Exception> {
        void accept(T t) throws E;
    }

    @FunctionalInterface
    public interface SupplierWithException<T, E extends Exception> {
        T get() throws E;
    }

}
