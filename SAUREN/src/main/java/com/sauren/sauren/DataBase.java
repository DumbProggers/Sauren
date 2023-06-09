package com.sauren.sauren;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class DataBase
{
    private static final String userBase="usersBase.txt";
    private ArrayList<ClientUser> usersList;
    DataBase(ArrayList<ClientUser> usersList){
        this.usersList=usersList;
    }
    public void saveUsersData()
    {
        String data="";
        for(ClientUser usr:usersList)
        {   data += usr.getName() + ":" + usr.getIp()+":"+usr.userFolder.getLastOnlineDayFolderName()+":"+usr.userFolder.getLastScreenFolder() +":"+usr.userFolder.getLastScreenName()+ "\n";   }
        //сохранил пользователя в формате "Имя:IP:ИмяПапкиДняПоследнейАктивности:ИмяПапкиПоследнегоСкрина:ИмяПоследнегоСкрина"

        try
        {
            Path file=Path.of(userBase);
            if(!Files.exists(file)) Files.createFile(file);

            Files.writeString(file,data);
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    private static String readFromFile(String file)
    {
        try {
            Path data = Path.of(file);
            if(!Files.exists(data)) Files.createFile(data);
            return Files.readString(data);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void getUsersFromBase()//заполнить массив users пользователями, которые когда-либо подключались
    {
        usersList.clear();
        String data=readFromFile(userBase);

        while(data.length()>1)
        {
            //имя
            int index=data.indexOf(":");
            String temp=data.substring(0,index);
            ClientUser usr=new ClientUser();
            usr.setName(temp);
            data=data.substring(index+1);

            //ip
            index=data.indexOf(":");
            temp=data.substring(0,index);
            usr.setIp(temp);
            data= data.substring(index+1);

            //имя папки дня последнего онлайна
            index=data.indexOf(":");
            temp=data.substring(0,index);
            usr.userFolder.setLastOnlineDayFolderName(temp);
            data= data.substring(index+1);

            //папка с последнего приложения(с последним сриншотом)
            index=data.indexOf(":");
            temp=data.substring(0,index);
            usr.userFolder.setLastScreenFolder(temp);
            data= data.substring(index+1);

            //имя последнего скриншота
            index=data.indexOf("\n");
            temp=data.substring(0,index);
            usr.userFolder.setLastScreenName(temp);
            data= data.substring(index+1);

            usersList.add(usr);
        }
    }
}
