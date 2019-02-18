package uk.co.magictractor.oauth.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

public final class IOUtil {
	private IOUtil() {
	}

	public static String readAndClose(InputStream in) {
		return ExceptionUtil.call(() -> readAndClose0(in));
	}

	private static String readAndClose0(InputStream in) throws IOException {
		Objects.requireNonNull(in, "InputStream must not be null");
		
		StringBuilder bodyBuilder = new StringBuilder();
		Reader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			char[] buffer = new char[1024];
			int len;
			while ((len = reader.read(buffer)) != -1) {
				bodyBuilder.append(buffer, 0, len);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
			in.close();
		}

		return bodyBuilder.toString();
	}
}
