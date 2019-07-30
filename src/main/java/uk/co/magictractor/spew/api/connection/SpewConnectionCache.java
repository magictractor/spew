package uk.co.magictractor.spew.api.connection;

import java.util.HashMap;
import java.util.Map;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.spi.SPIUtil;

public class SpewConnectionCache {

    private static final Map<Class<? extends SpewApplication>, SpewConnection> connections = new HashMap<>();

    public static final SpewConnectionFactory CONNECTION_FACTORY = SPIUtil.loadFirstAvailable(SpewConnectionFactory.class);

    public static SpewConnection getConnection(Class<? extends SpewApplication> applicationClass) {
        SpewConnection connection = connections.get(applicationClass);
        if (connection == null) {
            synchronized (applicationClass) {
                connection = connections.get(applicationClass);
                if (connection == null) {
                    connection = initConnection(applicationClass);
                    connections.put(applicationClass, connection);
                }
            }
        }

        return connection;
    }

    private static SpewConnection initConnection(Class<? extends SpewApplication> applicationClass) {
        SpewApplication application = ExceptionUtil.call(() -> applicationClass.newInstance());
        return CONNECTION_FACTORY.createConnection(application);
    }

}
