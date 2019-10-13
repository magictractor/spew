package uk.co.magictractor.spew.api.connection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
     * application implementation, but if multiple instances are created, then
     * they should share a connection (unless {@link SpewApplication#getId()} is
     * overridden).
     * </p>
     */
    private static final Map<Integer, SpewConnection> CONNECTIONS = new HashMap<>();

    private SpewConnectionCache() {
    }

    public static SpewConnection getOrCreateConnection(SpewApplication<?> application) {
        int applicationId = application.getId();
        SpewConnection connection = CONNECTIONS.get(applicationId);
        if (connection == null) {
            String lock = Integer.toString(applicationId).intern();
            synchronized (lock) {
                connection = CONNECTIONS.get(applicationId);
                if (connection == null) {
                    connection = initConnection(application);
                    CONNECTIONS.put(applicationId, connection);
                }
            }
        }

        return connection;
    }

    private static SpewConnection initConnection(SpewApplication<?> application) {
        return SPIUtil.firstNotNull(SpewConnectionFactory.class, factory -> factory.createConnection(application))
                .get();
    }

    public static Optional<SpewConnection> get(String connectionId) {
        // Could have another HashMap keyed by id, but there should be very few, so this is OK for now.
        return CONNECTIONS.values().stream().filter(conn -> conn.getId().equals(connectionId)).findAny();
    }

}
