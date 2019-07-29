package uk.co.magictractor.spew.server.netty;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.server.ServerRequest;
import uk.co.magictractor.spew.server.SimpleErrorResponse;
import uk.co.magictractor.spew.server.SimpleResponse;
import uk.co.magictractor.spew.server.StaticPage;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 * Server for receiving OAuth authorization callbacks. Based on example in
 * https://netty.io/wiki/user-guide-for-4.x.html.
 */

// TODO! look at using Undertow instead/
// see https://javachannel.org/posts/netty-is-not-a-web-framework/
public class NettyCallbackServer {

    private final ServerCallback callback;
    private final int port;
    private final List<RequestHandler> requestHandlers;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    private ChannelFuture f;

    public NettyCallbackServer(List<RequestHandler> requestHandlers, ServerCallback callback, int port) {
        this.requestHandlers = requestHandlers;
        this.callback = callback;
        this.port = port;
    }

    public String getUrl() {
        // Note that 127.0.0.1 is used rather than localhost is not used because Twitter does not support localhost.
        // See https://developer.twitter.com/en/docs/basics/apps/guides/callback-urls.html

        // TODO! implement https?
        return "http://127.0.0.1:" + port;
    }

    public void run() {
        ExceptionUtil.call(this::run0);
    }

    private void run0() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // https://netty.io/4.0/api/io/netty/channel/ChannelPipeline.html
                        // http://tutorials.jenkov.com/netty/netty-channelpipeline.html
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            // This is first because it's an outbound handler,
                            // and outbound handlers are processed in reverse order.
                            p.addLast(new HttpResponseEncoder());

                            p.addLast(new HttpRequestDecoder());
                            // Don't want to handle HttpChunks (see HttpSnoopServerInitializer)
                            p.addLast(new HttpObjectAggregator(1048576));

                            p.addLast(new SimpleResponseEncoder());

                            // This adds listeners which will handle exceptions if SimpleResponseEncoder is buggy.
                            // Outbound handlers are processed in reverse order.
                            p.addLast(new OutboundWriteExceptionHandler());

                            // TODO! merge these and maybe move to a distinct class
                            p.addLast(new CallbackServerHandler());
                            p.addLast(new InboundExceptionHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            f = b.bind(port).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            // f.channel().closeFuture().sync();
        }
        finally {
            // workerGroup.shutdownGracefully();
            // bossGroup.shutdownGracefully();
        }
    }

    // Block until the server is stopped.
    public void join() {
        ExceptionUtil.call(this::join0);
    }

    private void join0() throws InterruptedException {
        f.channel().closeFuture().sync();
    }

    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    //    public boolean isRunning() {
    //    	retunr
    //    }

    public class CallbackServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            //		    ByteBuf in = (ByteBuf) msg;
            //		    try {
            //		        while (in.isReadable()) { // (1)
            //		            System.out.print((char) in.readByte());
            //		            System.out.flush();
            //		        }
            //		    } finally {
            //		       // ReferenceCountUtil.release(msg); // (2)
            //		    	in.release();
            //		    }

            FullHttpRequest httpRequest = (FullHttpRequest) msg;

            //System.err.println("msg: " + msg);
            String uri = httpRequest.uri();
            // System.err.println("uri: " + uri);

            // Aaah, OAuth1 jumps straight to the token
            // OAuth2 has the fragment requiring the use of /catchtoken

            switch (uri) {
                case "/":
                    handleRoot(ctx);
                    break;
                case "/catchtoken":
                    handleCatchtoken(httpRequest, ctx);
                    break;
                default:
                    //handleUnknown(ctx);
                    handle(ctx, new FullHttpMessageRequest(httpRequest));
            }
        }

        private void handle(ChannelHandlerContext ctx, ServerRequest request) {
            for (RequestHandler handler : requestHandlers) {
                SimpleResponse simpleResponse = handler.handleRequest(request);
                if (simpleResponse != null) {
                    ctx.writeAndFlush(simpleResponse).addListener(ChannelFutureListener.CLOSE);
                    // TODO! writing immediately is iffy if we can continue handling?
                    if (!simpleResponse.isContinueHandling()) {
                        return;
                    }
                }
            }
            handleUnknown(ctx);
        }

        // See
        // https://github.com/kamatama41/netty-sample-http/blob/master/src/main/java/com/kamatama41/netty/sample/http/HelloServerHandler.java

        // TODO! maybe don't want/need this it gets hit even without a channel read
        // (keep alive maybe)
        //		@Override
        //		public void channelReadComplete(ChannelHandlerContext ctx) {
        //			System.err.println("flush");
        //			ctx.flush();
        //
        //			// without close() browser sticks on "waiting";
        //			// with close() says empty response??
        //			// ctx.close();
        //		}

        //	    @Override
        //	    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        //	        // Discard the received data silently.
        //	        ((ByteBuf) msg).release(); // (3)
        //	    }

        //        @Override
        //        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        //            // DOH!! I didn't realise this exception handler was here!!
        //
        //            // Close the connection when an exception is raised.
        //            cause.printStackTrace();
        //            ctx.close();
        //
        //            shutdown();
        //
        //
        //        }
    }

    private void handleRoot(ChannelHandlerContext ctx) {
        StaticPage page = new StaticPage("fragmentCallback.html");
        ByteBuf content = Unpooled.wrappedBuffer(page.getBytes());
        // ctx.write(content);

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content, false);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, page.getLength());
        // response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/html");
        // See
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types
        // response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/javascript");
        // System.err.println("write js");
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/html");
        System.err.println("write html");

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    // {access_token=[beb2e0b48e52e936363f8f41c7018ff7fcf3800c],
    // expires_in=[315360000], token_type=[bearer],
    // refresh_token=[536af783ea1e1a44db80b603b173fe9e710d0d04],
    // account_username=[GazingAtTrees], account_id=[96937645]}
    private void handleCatchtoken(FullHttpRequest httpRequest, ChannelHandlerContext ctx) {
        //		ByteBuf content = httpRequest.content();
        //		// new QueryStringDecoder()
        //
        //		String s = content.toString(Charsets.UTF_8);
        //		System.err.println("content: " + s);
        //
        //		QueryStringDecoder d = new QueryStringDecoder(s, Charsets.UTF_8, false);
        //		System.err.println(d.parameters());

        callback.callback(httpRequest);

        DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        System.err.println("write xhr response and close");

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

        // TODO! handler should trigger the shutdown?
        shutdown();
    }

    private void handleUnknown(ChannelHandlerContext ctx) {
        SimpleErrorResponse response = new SimpleErrorResponse(404, "Not found");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    // TODO! bin this
    @FunctionalInterface
    public static interface ServerCallback {
        void callback(FullHttpRequest httpRequest);
    }

}
