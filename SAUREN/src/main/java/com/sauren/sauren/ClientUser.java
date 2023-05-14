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

    public String getName() {return name;}
    public String getIp()   {return ip;}
    public int getScreensDelay()    {return screenDealy;}
    public boolean userOnline() {return isOnline;}
    public Channel getChannel()    {return userChannel;}
    public String getLastFilePath() {return lastFilePath;}

}
