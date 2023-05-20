package com.sauren.sauren;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;
import java.util.*;

public class ServerHandler extends SimpleChannelInboundHandler<Object>//класс обработчик (In) - работаем на вход данных
{

    public static String file_dir = "D:\\saurenScreens\\";//основа пути к изображениям
    public static ArrayList<ClientUser> users=new ArrayList<>();//массив со всеми когда-либо подключенными пользователями

    private String getIpFromCTX(ChannelHandlerContext ctx)
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
                saveUsersToUsersBase();//обновить базу с пользователями
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
        saveUsersToUsersBase();//обновить базу с пользователями
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
                    currentUsr.setName(message.substring(0, index));
                    currentUsr.setLastFilePath(message);
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
    private static String getApp(String message)
    {
        int index = message.indexOf("\\");
        int indexLast = message.lastIndexOf("\\");
        String app = message.substring(index+1,indexLast);
        return app;
    }

   public void saveFile(FileUploadFile o,ClientUser sender) throws IOException
   {
        byte[] bytes = o.getBytes();
       int byteRead = o.getEndPos();

        String filename = getCurrentDate()+".png";
        String pathToScreens = file_dir +getCurrentDate().substring(0,10)/*деньНедели_меяц_день*/+ File.separator + sender.getLastFilePath();
        String fullPath = pathToScreens + File.separator + filename;

        sender.setLastScreenPath(fullPath);
        File theDir = new File(pathToScreens);
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        File file = new File(fullPath);

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
        String newDateNow = dateNow.replace(":", "\'");
        newDateNow = removeLastNchars(newDateNow, 10);//last: 13 - minutes 10 - seconds
        return newDateNow;
    }
    public static String removeLastNchars(String str, int n) {
        return str.substring(0, str.length() - n);
    }

    public static void saveUsersToUsersBase()
    {
        String data="";
        for(ClientUser usr:users)   {   data += usr.getName() + ":" + usr.getIp()+":"+usr.getLastScreenPath() + "\n";   }
        //сохранил пользователя в формате "Имя:IP:ПутьКпоследнемуСкрину"

        try(FileWriter writer = new FileWriter("usersBase.txt", false))
        {
            // запись всей строки
            writer.write(data);
            // запись по символам
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    public static String readFile(String file)
    {
        String dt = "";
        try(BufferedReader br = new BufferedReader (new FileReader(file)))
        {
            // чтение посимвольно
            int c;
            while((c=br.read())!=-1){
                //System.out.print((char)c);
                dt+=(char)c;
            }
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
        return dt;
    }

    public static void getUsersFromBase()//заполнить массив users пользователями, которые когда-либо подключались
    {
        users.clear();
        String data=readFile("usersBase.txt");
        while(data.length()>1)
        {
            int index=data.indexOf(":");
            String temp=data.substring(0,index);
            ClientUser usr=new ClientUser();
            usr.setName(temp);
            data=data.substring(index+1);

            index=data.indexOf(":");
            temp=data.substring(0,index);
            usr.setIp(temp);
            data= data.substring(index+1);

            index=data.indexOf("\n");
            temp=data.substring(0,index);
            usr.setLastScreenPath(temp);
            data= data.substring(index+1);

            users.add(usr);
        }
    }
}
