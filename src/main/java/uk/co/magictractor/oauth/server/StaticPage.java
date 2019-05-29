package uk.co.magictractor.oauth.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

// A static page displayed by CallbackServer.
public class StaticPage {

    private byte[] bytes;

    // TODO! cache?

    public StaticPage(String resourceName) {
        this(StaticPage.class.getResourceAsStream(resourceName));
    }

    // TODO! date in response and if-not-modified-since
    public StaticPage(InputStream inputStream) {
        try {
            bytes = ByteStreams.toByteArray(inputStream);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        finally {
            Closeables.closeQuietly(inputStream);
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getLength() {
        return bytes.length;
    }

}
