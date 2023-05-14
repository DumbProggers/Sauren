package com.sauren.sauren;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;
import java.util.*;

public class ServerHandler extends SimpleChannelInboundHandler<Object>{//класс обработчик (In) - работаем на вход данных
    public static String file_dir = "D:\\saurenScreens\\";
    private String ClientName;
    private String username ="";
    private String message ="";
    public static List<Channel> usersOnlineChannekList = new ArrayList<>();
    public static List<String> nameUsersOnlineStringList = new ArrayList<String>();
    public static int delay;//задержка отправки
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        ClientName = ctx.channel().remoteAddress().toString();
        System.out.println("> Client Connected: " + ClientName);
        usersOnlineChannekList.add(ctx.channel());
        System.out.println("Users online: "+ usersOnlineChannekList.size());
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
    {
        System.out.println(ctx.channel().remoteAddress()+" Disconected");
        nameUsersOnlineStringList.remove(ClientName+":"+username);
        System.out.println(ClientName+" disconected");
        usersOnlineChannekList.remove(ctx.channel());
        System.out.println("Users online: "+ usersOnlineChannekList.size());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.fillInStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception
    {
        if(o instanceof String){//получаем "user\ПРИЛОЖЕНИЕ\ПРОЕКТ"
            message = o.toString();
            username = o.toString();
            int index = username.indexOf("\\");
            username = username.substring(0,index);

            nameUsersOnlineStringList.add(ClientName+":"+username);
            //удалить повторения
            Set<String> set = new HashSet<>(nameUsersOnlineStringList);
            nameUsersOnlineStringList.clear();
            nameUsersOnlineStringList.addAll(set);
        }
        if(o instanceof FileUploadFile){     saveFile((FileUploadFile) o);  }
        if(o instanceof Integer)
        {
            delay = (int) o;
            System.out.println("DELAY "+username+": "+delay);
        }
    }

   public void saveFile(FileUploadFile o) throws IOException
   {
        byte[] bytes = o.getBytes();
       int byteRead = o.getEndPos();

        String filename = getCurrentDate()+".png";
        String path = file_dir + File.separator + message + File.separator + filename;
        String pathToScreens = file_dir + File.separator + message;

        File theDir = new File(pathToScreens);
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        File file = new File(path);

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
       int start = 0;
       randomAccessFile.seek(start);
        randomAccessFile.write(bytes);
        randomAccessFile.close();

        System.out.println(username+" send file"+"\nFile save to:"+path);
    }

    public static String getCurrentDate()
    {
        Date date = new Date();
        String dateNow = date.toString();
        dateNow = dateNow.replace(" ", "_");
        String newDateNow = dateNow.replace(":", "_");
        newDateNow = removeLastNchars(newDateNow, 10);//last: 13 - minutes 10 - seconds
        return newDateNow;
    }
    public static String removeLastNchars(String str, int n) {
        return str.substring(0, str.length() - n);
    }

}
