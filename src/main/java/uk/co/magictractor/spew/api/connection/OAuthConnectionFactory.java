package uk.co.magictractor.spew.api.connection;

import java.util.HashMap;
import java.util.Map;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.boa.BoaConnectionInit;
import uk.co.magictractor.spew.util.ExceptionUtil;

//
public class OAuthConnectionFactory {

    private static final Map<Class<? extends SpewApplication>, SpewConnection> connections = new HashMap<>();

    // TODO! SPI and final
    private static final OAuthConnectionInit CONNECTION_INIT = new BoaConnectionInit();

    public static SpewConnection getConnection(Class<? extends SpewApplication> applicationClass) {
        SpewConnection connection = connections.get(applicationClass);
        if (connection == null) {
            synchronized (connections) {
                if (connection == null) {
                    connection = initConnection(applicationClass);
                }
            }
        }

        return connection;
    }

    private static SpewConnection initConnection(Class<? extends SpewApplication> applicationClass) {
        SpewApplication application = ExceptionUtil.call(() -> applicationClass.newInstance());
        return CONNECTION_INIT.createConnection(application);
    }

}
