package uk.co.magictractor.spew.api.connection;

import java.util.HashMap;
import java.util.Map;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.util.spi.SPIUtil;

public class SpewConnectionCache {

    /**
     * <p>
     * Keyed by application id.
     * </p>
     * <p>
     * It is intended that there only ever be a single instance of an
     * application implementation,
     * </p>
     */
    private static final Map<String, SpewConnection> connections = new HashMap<>();

    public static SpewConnection getOrCreateConnection(SpewApplication<?> application) {
        String applicationId = application.getId();
        SpewConnection connection = connections.get(applicationId);
        if (connection == null) {
            String lock = applicationId.intern();
            synchronized (lock) {
                connection = connections.get(applicationId);
                if (connection == null) {
                    connection = initConnection(application);
                    connections.put(applicationId, connection);
                }
            }
        }

        return connection;
    }

    private static SpewConnection initConnection(SpewApplication<?> application) {
        return SPIUtil.firstNotNull(SpewConnectionFactory.class, factory -> factory.createConnection(application))
                .get();
    }

}
