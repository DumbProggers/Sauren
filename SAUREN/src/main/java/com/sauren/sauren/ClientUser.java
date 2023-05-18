package com.sauren.sauren;

import io.netty.channel.Channel;
import javafx.scene.image.Image;

public class ClientUser //класс клиента
{
    private String name;
    private String ip;
    private int screenDealy;//задержка между скиншотами в миллисекундах
    private Image icon;//иконка
    private boolean isOnline;
    private Channel userChannel;
    private String lastFilePath;//путь для сохранения следующего полученного файла
    private String lastScreenPath;//путь последнего полученного скриншота

    public ClientUser()
    {
        this.name="none";
        ip="none";
        screenDealy=1000;
        isOnline=false;
    }
    public ClientUser(Channel ch,String name,String ip)
    {
        userChannel=ch;
        this.name=name;
        this.ip=ip;
        screenDealy=1000;
        isOnline=false;
    }

    public void setName(String name){this.name=name;}
    public void setIp(String ip)    {this.ip=ip;}
    public void setScreensDelay(int newDelay)   {screenDealy=newDelay;}
    public void setOnline(boolean online)    {isOnline=online;}
    public void setChannel(Channel ch)  {userChannel=ch;}
    public void setLastFilePath(String path)    {lastFilePath=path;}
    public void setLastScreenPath(String path)    {lastScreenPath=path;}
    public String getName() {return name;}
    public String getIp()   {return ip;}
    public int getScreensDelay()    {return screenDealy;}
    public boolean userOnline() {return isOnline;}
    public Channel getChannel()    {return userChannel;}
    public String getLastFilePath() {return lastFilePath;}
    public String getLastScreenPath() {return lastScreenPath;}
    public String getLastOnlineDate()
    {
        int index = lastScreenPath.indexOf('\\');
        String date=lastScreenPath;
        while(index!=-1)
        {
            date=date.substring(index+1);
            index = date.indexOf('\\');
           // System.out.println(date);
        }
        date=date.substring(4,16);//убрать день недели и все что после минут
        //May_18_22_00
        date=date.replaceFirst("_",",");//May, 18_22_00
        date=date.replaceFirst("_"," ");//May, 18 22_00
        date=date.replaceFirst("_",":");//May, 18 22:00
        //System.out.println(date);
        return date;
    }
}
