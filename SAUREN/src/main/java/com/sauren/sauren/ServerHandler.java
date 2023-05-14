package com.sauren.sauren;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;
import java.util.*;

public class ServerHandler extends SimpleChannelInboundHandler<Object>{//класс обработчик (In) - работаем на вход данных
    public static String file_dir = "D:\\saurenScreens\\";
    public static ArrayList<ClientUser> users=new ArrayList<>();

    private String getIpFromCTX(ChannelHandlerContext ctx)
    {
        String ip=ctx.channel().remoteAddress().toString();
        ip=ip.substring(1,ip.indexOf(":"));//убираем лишнее
        return ip;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        String curIP=getIpFromCTX(ctx);
        System.out.println(curIP);

        for(ClientUser currentUsr:users)
        {
            if(currentUsr.getIp().equals(curIP) && currentUsr.userOnline())  return;//если такой пользователь существует и он подключен
            else if(currentUsr.getIp().equals(curIP))//если пользователь существует, но не подключен
            {
                currentUsr.setOnline(true);
                System.out.println("> User "+currentUsr.getName() + " now Online!!!");
                return;
            }
        }
        //если пользователя не существует, создаем и добавляем
        ClientUser newUsr=new ClientUser();
        newUsr.setChannel(ctx.channel());
        newUsr.setIp(curIP);
        newUsr.setOnline(true);
        System.out.println("> New User in System!!! "+newUsr.getIp());
        users.add(newUsr);
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)//пользователь отключился
    {
        String curIp=getIpFromCTX(ctx);
        for(ClientUser currentUsr:users)
        {
            if(currentUsr.getIp().equals(curIp))
            {
                currentUsr.setOnline(false);
                System.out.println("> User "+currentUsr.getName()+" disconnected!");
                currentUsr.getChannel().close();
                return;
            }
        }
        System.out.println(">>>Disconnection error??? (ServerHandler - handlerRemoved");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){ cause.fillInStackTrace(); }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception//получение сообщения от клиента
    {
        String curIp=getIpFromCTX(ctx);
        for(ClientUser currentUsr:users) {
            if(currentUsr.getIp().equals(curIp)) //если нашли отправителя
            {
                String message;
                if (o instanceof String)//получаем "user\ПРИЛОЖЕНИЕ\ПРОЕКТ"
                {
                    message = o.toString();
                    int index = message.indexOf("\\");
                    currentUsr.setName(message.substring(0, index));
                    currentUsr.setLastFilePath(message);
                }
                if (o instanceof FileUploadFile)
                {
                    saveFile((FileUploadFile) o,currentUsr);
                }
                if (o instanceof Integer) //если пришла задержка
                {
                    currentUsr.setScreensDelay((int)o);
                }
            }
        }
    }

   public void saveFile(FileUploadFile o,ClientUser sender) throws IOException
   {
        byte[] bytes = o.getBytes();
       int byteRead = o.getEndPos();

        String filename = getCurrentDate()+".png";
        String path = file_dir + File.separator + sender.getLastFilePath() + File.separator + filename;
        String pathToScreens = file_dir + File.separator + sender.getLastFilePath();

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

        System.out.println(sender.getName()+" send file"+"\nFile save to:"+path);
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
