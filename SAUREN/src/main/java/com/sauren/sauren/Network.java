package com.sauren.sauren;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.*;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.Socket;

public class Network
{
    //запускаем сервер на порту, который взяли с формы
    private static int port;
    private static String serverIP;

    public Network()
    {
        port=checkPort(8189);//устанавлваем порт
        try
        {
            serverIP = Inet4Address.getLocalHost().getHostAddress();//берем текущий ip
        }catch(Exception ex){ex.printStackTrace();}
    }
    public static int getPort()    {return port;}
    public static String getServerIp() {return serverIP;}
    public static void Start()
    {
        EventLoopGroup bossGrop = new NioEventLoopGroup(1);//обработка запросов в параллельных потоках. Пул поток
        EventLoopGroup workerGroup = new NioEventLoopGroup();//для работы
        try{
            ServerBootstrap b = new ServerBootstrap();//Преднастройка сервера
            b.group(bossGrop,workerGroup)//используй 2 пула потока для работы с подключениями.
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 2048)
                    //.option(ChannelOption.SO_BACKLOG,Integer.MAX_VALUE)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception
                        {
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


    private static int checkPort(int port)
    {
        while(!isPortAviable(port))
            System.out.println("> Порт "+(port++)+" занят");
        return port;
    }
    private static boolean isPortAviable(int port) throws IllegalStateException
    {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (ConnectException e) {
            return true;
        } catch (IOException e) {
            throw new IllegalStateException("Error while trying to check open port", e);
        }
    }
}