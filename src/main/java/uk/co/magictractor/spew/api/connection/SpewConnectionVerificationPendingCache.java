package uk.co.magictractor.spew.api.connection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.SpewAuthorizationVerifiedConnection;
import uk.co.magictractor.spew.server.SpewHttpRequest;

public class SpewConnectionVerificationPendingCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpewConnectionVerificationPendingCache.class);

    // Predicate checks oauth_token (OAuth1) or random state (OAuth2)
    private static final Set<PredicateAndApplication> verificationPending = new HashSet<>();

    private SpewConnectionVerificationPendingCache() {
    }

    public static void addVerificationPending(Predicate<SpewHttpRequest> predicate,
            SpewAuthorizationVerifiedConnection connection) {
        LOGGER.debug("added {}", connection);
        verificationPending.add(new PredicateAndApplication(predicate, connection));
    }

    public static Optional<SpewAuthorizationVerifiedConnection> removeVerificationPending(
            SpewHttpRequest verificationRequest) {
        Iterator<PredicateAndApplication> iterator = verificationPending.iterator();
        while (iterator.hasNext()) {
            PredicateAndApplication candidate = iterator.next();
            if (candidate.predicate.test(verificationRequest)) {
                iterator.remove();
                return Optional.of(candidate.connection);
            }
        }
        return Optional.empty();
    }

    public static SpewAuthorizationVerifiedConnection removeSingleton() {
        if (verificationPending.size() != 1) {
            throw new IllegalStateException();
        }
        SpewAuthorizationVerifiedConnection connection = Iterables.getOnlyElement(verificationPending).connection;
        verificationPending.clear();

        return connection;
    }

    /**
     * Using a Predicate allows different types of authentication to use the
     * same code. The Boa OAuth1 implementation checks the "oauth_token" query
     * string parameter in the request, and the Boa OAuth2 implementation checks
     * a randomly generated "state" query string parameter.
     */
    private static final class PredicateAndApplication {
        private final Predicate<SpewHttpRequest> predicate;
        private final SpewAuthorizationVerifiedConnection connection;

        PredicateAndApplication(Predicate<SpewHttpRequest> predicate, SpewAuthorizationVerifiedConnection connection) {
            this.predicate = predicate;
            this.connection = connection;
        }
    }

}
