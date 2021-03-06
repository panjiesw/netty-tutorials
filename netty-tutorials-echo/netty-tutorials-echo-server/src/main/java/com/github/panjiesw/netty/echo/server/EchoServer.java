package com.github.panjiesw.netty.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @author Panjie SW.
 */
public class EchoServer {
  private final int port;

  private EchoServer(int port) {
    this.port = port;
  }

  private void start() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(group)
        .channel(NioServerSocketChannel.class)
        .localAddress(new InetSocketAddress(this.port))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addFirst(new LoggingHandler(EchoServerHandler.class, LogLevel.DEBUG));
            ch.pipeline().addLast(new EchoServerHandler());
          }
        });
      ChannelFuture f = b.bind().sync();
      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: " + EchoServer.class.getSimpleName() + " <port>");
    } else {
      int port = Integer.parseInt(args[0]);
      new EchoServer(port).start();
    }
  }
}
