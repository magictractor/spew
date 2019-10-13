package uk.co.magictractor.spew.server.undertow;

import java.nio.ByteBuffer;
import java.util.List;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.core.http.header.SpewHeader;
import uk.co.magictractor.spew.server.CallbackServer;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.server.SpewHttpRequest;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.HttpMessageUtil;

// Undertow suggested by https://javachannel.org/posts/netty-is-not-a-web-framework
public class UndertowCallbackServer implements CallbackServer {

    private Undertow server;

    @Override
    public void run(List<RequestHandler> requestHandlers, int port) {
        server = Undertow.builder()
                .addHttpListener(port, "localhost")
                //                .setHandler(new HttpHandler() {
                //                    @Override
                //                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                //                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                //                        exchange.getResponseSender().send("Hello World");
                //                    }
                //                })
                .setHandler(new UndertowHttpHandler(requestHandlers))
                .build();
        server.start();
    }

    @Override
    public void join() {
        ExceptionUtil.call(() -> server.getWorker().awaitTermination());
    }

    @Override
    public void shutdown() {
        server.getWorker().shutdown();
        // shutdownAfterSend = true;
        //shutdownNow();
    }

    //    private void shutdownNow() {
    //        server.getWorker().shutdown();
    //    }

    private class UndertowHttpHandler implements HttpHandler {

        private final List<RequestHandler> requestHandlers;

        public UndertowHttpHandler(List<RequestHandler> requestHandlers) {
            this.requestHandlers = requestHandlers;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {

            SpewHttpRequest request = new IncomingUndertowRequest(exchange);

            SpewHttpResponse response = UndertowCallbackServer.this.handleRequest(request, requestHandlers);

            exchange.setStatusCode(response.getStatus());

            HeaderMap undertowHeaders = exchange.getResponseHeaders();
            for (SpewHeader header : response.getHeaders()) {
                undertowHeaders.add(new HttpString(header.getName()), header.getValue());
            }

            ByteBuffer bodyByteBuffer = HttpMessageUtil.createBodyByteBuffer(response);
            // Add IoCallback impl to send()?
            exchange.getResponseSender().send(bodyByteBuffer);
        }
    }

    //    private class UndertowIoCallback extends DefaultIoCallback {
    //
    //        @Override
    //        public void onComplete(final HttpServerExchange exchange, final Sender sender) {
    //            super.onComplete(exchange, sender);
    //            if (UndertowCallbackServer.this.shutdownAfterSend) {
    //                UndertowCallbackServer.this.shutdownNow();
    //            }
    //        }
    //
    //        @Override
    //        public void onException(final HttpServerExchange exchange, final Sender sender, final IOException exception) {
    //            super.onException(exchange, sender, exception);
    //        }
    //
    //    }

}
