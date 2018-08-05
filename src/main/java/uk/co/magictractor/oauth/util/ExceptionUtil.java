package uk.co.magictractor.oauth.util;

import java.util.concurrent.Callable;

public final class ExceptionUtil {

	private ExceptionUtil() {
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
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@FunctionalInterface
	public interface RunnableWithException {
		void run() throws Exception;
	}
}
