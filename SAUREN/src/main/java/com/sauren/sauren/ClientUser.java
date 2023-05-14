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
    public void setState(boolean online)    {isOnline=online;}
    public void setChannel(Channel ch)  {userChannel=ch;}

    public String getName() {return name;}
    public String getIp()   {return ip;}
    public int getScreensDelay()    {return screenDealy;}
    public boolean userOnline() {return isOnline;}
    public Channel getChannel()    {return userChannel;}

}
