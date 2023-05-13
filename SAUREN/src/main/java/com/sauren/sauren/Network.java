package com.sauren.sauren;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.ArrayList;
import java.util.List;

public class Network {
    //запускаем сервер на порту, который взяли с формы
    public static final int port = Integer.parseInt(MainServerAppController.getPort());
    public static void Start(){
        EventLoopGroup bossGrop = new NioEventLoopGroup(1);//обработка запросов в паралельных потоках. Пул поток
        EventLoopGroup workerGroup = new NioEventLoopGroup();//для работы
        try{
            ServerBootstrap b = new ServerBootstrap();//Преднастройка сервера
            b.group(bossGrop,workerGroup)//используй 2 пула потока для работы с подключениями.
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 2048)
                    //.option(ChannelOption.SO_BACKLOG,Integer.MAX_VALUE)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(FileUploadFile.class.getClassLoader())), new ServerHandler());
                        }//Когда к нам ктото подключится вся информация хранится в SocketChanel
                    });//сервер запущен

            System.out.println("SERVER START ON PORT " + port);

            ChannelFuture future = b.bind(port).sync();//сервак начинает стартовать, она нужна что бы понимать как работать с серваком
            future.channel().closeFuture().sync();//блокирующая информация, ждем когда сервер кто-то остановит.

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //закрываем потоки, если сервер кто-то смог остановить и соответсвенно закрываем все. Если их не закрыть
            bossGrop.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}