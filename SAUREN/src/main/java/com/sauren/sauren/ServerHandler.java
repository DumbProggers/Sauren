package com.sauren.sauren;

import com.sauren.sauren.clients.ClientUser;
import com.sauren.sauren.clients.DataBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;
import java.util.*;

public class ServerHandler extends SimpleChannelInboundHandler<Object>//класс обработчик (In) - работаем на вход данных
{
    public static ArrayList<ClientUser> users=new ArrayList<>();//массив со всеми когда-либо подключенными пользователями
    public final static DataBase dataBase=new DataBase(users);
    private static String getIpFromCTX(ChannelHandlerContext ctx)
    {
        String ip=ctx.channel().remoteAddress().toString();
        ip=ip.substring(1,ip.indexOf(":"));//убираем лишнее
        return ip;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx)//пользователь подключился
    {
        String curIP=getIpFromCTX(ctx);
        for(ClientUser currentUsr:users)
        {
            if(currentUsr.getIp().equals(curIP) && currentUsr.userOnline())  return;//если такой пользователь существует и он подключен
            else if(currentUsr.getIp().equals(curIP))//если пользователь существует, но не подключен
            {
                currentUsr.setOnline(true);
                currentUsr.setChannel(ctx.channel());
                System.out.println("> User "+currentUsr.getName() + " now Online!!!");
                dataBase.saveUsersData();//обновить базу с пользователями
                MainServerAppController.setNeedToUpdateUsersPanel(true);//чтобы обновить панель со всеми пользователями
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
        dataBase.saveUsersData();//обновить базу с пользователями
        MainServerAppController.setNeedToUpdateUsersPanel(true);//чтобы обновить панель со всеми пользователями
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
                System.out.println("fff");
                System.out.println("> User "+currentUsr.getName()+" disconnected!");
                currentUsr.getChannel().close();
                dataBase.saveUsersData();//обновить базу с пользователями
                MainServerAppController.setNeedToUpdateUsersPanel(true);//чтобы обновить панель со всеми пользователями
                return;
            }
        }
        System.out.println(">>>Disconnection error??? (ServerHandler - handlerRemoved)");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){ cause.fillInStackTrace(); }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception//получение сообщения от клиента
    {
        String curIp=getIpFromCTX(ctx);
        for(ClientUser currentUsr:users)
        {
            if(currentUsr.getIp().equals(curIp)) //если нашли отправителя
            {
                String message;
                if (o instanceof String)//получаем "user\ПРИЛОЖЕНИЕ\ПРОЕКТ"
                {
                    message = o.toString();
                    //получаем текущее приложение пользователя.
                    int index = message.indexOf("\\");
                    String newName=message.substring(0, index);
                    if(!currentUsr.getName().equals(newName))//если нужно сменить имя, меняем, вызываем обновление панели
                    {
                        currentUsr.setName(newName);
                        dataBase.saveUsersData();//обновить базу с пользователями
                        MainServerAppController.setNeedToUpdateUsersPanel(true);//чтобы обновить панель со всеми пользователями
                    }
                    currentUsr.userFolder.setLastScreenFolder(message.substring(index+1));//сохраняем папку для скриншота
                }
                if (o instanceof FileUploadFile)
                {
                    saveFile((FileUploadFile) o,currentUsr);
                }
                if (o instanceof Integer) //если пришла задержка
                {
                    ClientUser.setScreensDelay((int)o);
                }
                break;
            }
        }
    }

   public static void saveFile(FileUploadFile o,ClientUser sender) throws IOException
   {
        byte[] bytes = o.getBytes();
        String curDay=getCurrentDate();
        sender.userFolder.setLastScreenName(curDay.substring(11)+".png");

        sender.userFolder.setLastOnlineDayFolderName(curDay.substring(0,10));

        File theDir = new File(sender.userFolder.getFullPathToLastScreenFolder());
        if (!theDir.exists())    theDir.mkdirs();


        File file = new File(sender.userFolder.getFullPathToLastScreen());

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
       int start = 0;
       randomAccessFile.seek(start);
       randomAccessFile.write(bytes);
       randomAccessFile.close();
    }

    public static String getCurrentDate()
    {
        Date date = new Date();
        String dateNow = date.toString();
        dateNow = dateNow.replace(" ", "_");
        String newDateNow = dateNow.replace(":", "'");
        newDateNow = removeLastNchars(newDateNow, 10);//last: 13 - minutes 10 - seconds
        return newDateNow;
    }
    public static String removeLastNchars(String str, int n) {
        return str.substring(0, str.length() - n);
    }
}
