package uk.co.magictractor.spew.api.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewAuthType;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.spi.SPIUtil;

public class SpewConnectionCache {

    private static final Map<Class<? extends SpewApplication>, SpewConnection> connections = new HashMap<>();

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
        checkAuthType(applicationClass);
        SpewApplication application = ExceptionUtil.call(() -> applicationClass.newInstance());
        return SPIUtil.firstNotNull(SpewConnectionFactory.class, factory -> factory.createConnection(application))
                .get();
        // TODO! wire in transport here??
    }

    // hmm... another method for the transport...?

    /*
     * @param applicationClass
     */
    private static void checkAuthType(Class<? extends SpewApplication> applicationClass) {
        List<String> authTypes = new ArrayList<>();
        Class<?>[] ifaces = applicationClass.getInterfaces();
        for (Class<?> iface : ifaces) {
            if (iface.isAnnotationPresent(SpewAuthType.class)) {
                authTypes.add(iface.getSimpleName());
            }
        }

        if (authTypes.isEmpty()) {
            throw new IllegalArgumentException(
                applicationClass.getName() + " does not implement any interfaces indicating the auth type");
        }

        if (authTypes.size() > 1) {
            throw new IllegalArgumentException(applicationClass.getName()
                    + " should implement a single interfaces indicating the auth type, but implements " + authTypes);
        }
    }

}
