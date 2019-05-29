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

    // TODO! replace with Guava Closeables
    public static void closeQuietly(InputStream in) {
        ExceptionUtil.call(() -> in.close());
    }

    public static String readStringAndClose(InputStream in) {
        return ExceptionUtil.call(() -> readStringAndClose0(in));
    }

    // TODO! now using Guava, which probably provides something very similar
    // https://stackoverflow.com/questions/4185665/guava-equivalent-for-ioutils-tostringinputstream
    // see CharStreams and StaticPage
    private static String readStringAndClose0(InputStream in) throws IOException {
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
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            in.close();
        }

        return bodyBuilder.toString();
    }
}
