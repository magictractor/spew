package uk.co.magictractor.spew.server.netty;

import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import uk.co.magictractor.spew.server.CallbackServer;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.server.SpewHttpRequest;
import uk.co.magictractor.spew.server.OutgoingErrorResponse;
import uk.co.magictractor.spew.server.OutgoingResponse;
import uk.co.magictractor.spew.server.OutgoingResponseBuilder;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.spi.ClassDependentAvailability;

/**
 * Server for receiving OAuth authorization callbacks. Based on example in
 * https://netty.io/wiki/user-guide-for-4.x.html.
 */

// TODO! look at using Undertow instead/
// see https://javachannel.org/posts/netty-is-not-a-web-framework/
public class NettyCallbackServer implements CallbackServer, ClassDependentAvailability {

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    private ChannelFuture f;

    @Override
    public String requiresClassName() {
        return "io.netty.bootstrap.ServerBootstrap";
    }

    @Override
    public void run(List<RequestHandler> requestHandlers, int port) {
        ExceptionUtil.call(() -> run0(requestHandlers, port));
    }

    private void run0(List<RequestHandler> requestHandlers, int port) throws InterruptedException {
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

                            p.addLast(new OutgoingResponseEncoder());

                            // This adds listeners which will handle exceptions if SimpleResponseEncoder is buggy.
                            // Outbound handlers are processed in reverse order.
                            p.addLast(new OutboundWriteExceptionHandler());

                            // TODO! merge these and maybe move to a distinct class
                            p.addLast(new CallbackServerHandler(requestHandlers));
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

    @Override
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

    public class CallbackServerHandler extends ChannelInboundHandlerAdapter {

        private final List<RequestHandler> requestHandlers;

        public CallbackServerHandler(List<RequestHandler> requestHandlers) {
            this.requestHandlers = requestHandlers;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            handle(ctx, new IncomingNettyRequest(httpRequest));
        }

        private void handle(ChannelHandlerContext ctx, SpewHttpRequest request) {

            OutgoingResponseBuilder responseBuilder = new OutgoingResponseBuilder();

            for (RequestHandler handler : requestHandlers) {
                handler.handleRequest(request, responseBuilder);
                if (responseBuilder.isDone()) {
                    break;
                }
            }

            if (responseBuilder.isShutdown()) {
                shutdown();
            }

            OutgoingResponse response = responseBuilder.build();
            if (response != null) {
                ctx.writeAndFlush(responseBuilder.build()).addListener(ChannelFutureListener.CLOSE);
            }
            else {
                // TODO! append this to the list of handlers?
                handleUnknown(ctx);
            }
        }
    }

    private void handleUnknown(ChannelHandlerContext ctx) {
        OutgoingErrorResponse response = OutgoingErrorResponse.notFound();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
