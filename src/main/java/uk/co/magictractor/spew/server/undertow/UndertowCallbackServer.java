package uk.co.magictractor.spew.server.undertow;

import java.util.List;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import uk.co.magictractor.spew.server.CallbackServer;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.util.spi.ClassDependantAvailability;

public class UndertowCallbackServer implements CallbackServer, ClassDependantAvailability {

    private Undertow server;

    @Override
    public String requiresClassName() {
        return "io.undertow.Undertow";
    }

    @Override
    public void run(List<RequestHandler> requestHandlers, int port) {
        server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Hello World");
                    }
                })
                .build();
        server.start();
    }

    @Override
    public void join() {
        // TODO Auto-generated method stub

    }

}
