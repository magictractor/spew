package uk.co.magictractor.spew.api.connection;

import java.util.HashMap;
import java.util.Map;

import uk.co.magictractor.spew.api.OAuthApplication;
import uk.co.magictractor.spew.api.OAuthConnection;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class OAuthConnectionFactory {

    private static Map<Class<? extends OAuthApplication>, OAuthConnection> connections = new HashMap<>();

    public static OAuthConnection getConnection(Class<? extends OAuthApplication> applicationClass) {
        OAuthConnection connection = connections.get(applicationClass);
        if (connection == null) {
            synchronized (connections) {
                if (connection == null) {
                    connection = initConnection(applicationClass);
                }
            }
        }

        return connection;
    }

    private static OAuthConnection initConnection(Class<? extends OAuthApplication> applicationClass) {
        OAuthApplication application = ExceptionUtil.call(() -> applicationClass.newInstance());
        return application.getNewConnectionSupplier().get();
    }

}
