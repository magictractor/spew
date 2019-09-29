package uk.co.magictractor.spew.api.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.SpewServiceProvider;
import uk.co.magictractor.spew.util.ExceptionUtil;

public abstract class AbstractConnection<APP extends SpewApplication<SP>, SP extends SpewServiceProvider>
        implements SpewConnection {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final APP application;

    /**
     * Callbacks which modify the request before it is send. Authorization
     * headers may be added by these callbacks.
     */
    private final List<Consumer<OutgoingHttpRequest>> beforeSendRequestCallbacks = new ArrayList<>();

    public AbstractConnection(APP application) {
        this.application = application;
    }

    @Override
    public APP getApplication() {
        return application;
    }

    public SP getServiceProvider() {
        return application.getServiceProvider();
    }

    public void addBeforeSendRequestCallback(Consumer<OutgoingHttpRequest> beforeSendRequestCallback) {
        if (beforeSendRequestCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        beforeSendRequestCallbacks.add(beforeSendRequestCallback);
    }

    /** This is expected to be used only for calling service methods. */
    @Override
    public SpewHttpResponse request(OutgoingHttpRequest request) {
        beforeSendRequestCallbacks.forEach(callback -> callback.accept(request));

        return sendRequest(request);
    }

    /**
     * Implementations may use this method to obtain authorization tokens etc as
     * well as calling service methods.
     */
    protected final SpewHttpResponse sendRequest(OutgoingHttpRequest request) {
        return ExceptionUtil.call(() -> sendRequest0(request));
    }

    /**
     * <p>
     * Sends the request without making any modifications to it first.
     * </p>
     * <p>
     * Authorization code may call this directly to get tokens etc, bypassing
     * the callbacks from {@link #request} which would usually ensure
     * authorization had been performed.
     * </p>
     * <p>
     * Implementations generally created a library specific request based on the
     * OutgoingHttpRequest, send it and then wrap the response with a library
     * specific implementation of SpewHttpResponse.
     * </p>
     */
    protected abstract SpewHttpResponse sendRequest0(OutgoingHttpRequest apiRequest) throws Exception;

    protected final Logger getLogger() {
        return logger;
    }

}
