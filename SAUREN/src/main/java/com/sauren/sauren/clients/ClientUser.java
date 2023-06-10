package com.sauren.sauren.clients;

import io.netty.channel.Channel;
import javafx.scene.image.Image;

public class ClientUser //класс клиента
{
    private String name;
    private String ip;
    private static int screenDealy;//задержка между скиншотами в миллисекундах
    private Image icon;//иконка
    private boolean isOnline;
    private Channel userChannel;
    public final UserFolderInfo userFolder;

    public ClientUser()
    {
        name="none";
        ip="none";
        screenDealy=1000;
        isOnline=false;
        userFolder=new UserFolderInfo(name+"_"+ip);
    }


    public void setName(String name)
    {
        this.name=name;
        userFolder.setUserFolder(name+"_"+ip);
    }
    public void setIp(String ip)
    {
        this.ip=ip;
        userFolder.setUserFolder(name+"_"+ip);
    }
    public static void setScreensDelay(int newDelay)   {screenDealy=newDelay;}
    public void setOnline(boolean online)    {isOnline=online;}
    public void setChannel(Channel ch)  {userChannel=ch;}
    //public void setLastScreenFolderPath(String path)    {lastScreenFolderPath=path;}
    //public void setLastScreenName(String path)    {lastScreenName=path;}


    public static int getScreensDelay()    {return screenDealy;}
    public boolean userOnline() {return isOnline;}
    public Channel getChannel()    {return userChannel;}
    public String getName() {return name;}
    public String getIp()   {return ip;}
    public String getLastOnlineDate()
    {
        if(userFolder.getLastScreenName()!=null && !userFolder.getLastScreenName().equals("null"))
        {
            String date=userFolder.getLastOnlineDayFolderName();
            date = date.substring(4);//убрать день недели
            //May_18
            date+=" "+userFolder.getLastScreenName().substring(0,5);
            //May_18 22'00
            date = date.replaceFirst("_", ",");//May, 18 22'00
            date = date.replaceFirst("\'", ":");//May, 18 22:00
            //
            return date;
        }
        return "now";
    }
}
