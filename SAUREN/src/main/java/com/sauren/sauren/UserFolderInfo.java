package com.sauren.sauren;

import java.util.Date;

public class UserFolderInfo
{
    private static String baseFolderPath="D:\\saurenScreens";
    private String lastScreenName;
    private String userName;
    private String lastScreenFolder;
    private String lastOnlineDayFolderName;

    public UserFolderInfo(String userName)
    {
        this.userName=userName;
        lastOnlineDayFolderName=getDayFolderName(new Date());
    }
    public static String getDayFolderName(Date day)
    {
        String dayFolder=day.toString();
        dayFolder=dayFolder.substring(0,10);
        dayFolder=dayFolder.replaceAll(" ","_");
        return dayFolder;
    }

    public void setLastScreenName(String name){     lastScreenName=name;   }
    public void setUserName(String name){   userName=name;}
    public void setLastOnlineDayFolderName(String day){ lastOnlineDayFolderName=day;}
    public void setLastOnlineDayFolderName(Date day){ lastOnlineDayFolderName=getDayFolderName(day);}
    public void setLastScreenFolder(String folder){ lastScreenFolder=folder;}
    public static void setBaseFolderPath(String path){  baseFolderPath=path;    }

    public String getLastScreenName(){     return lastScreenName;  }
    public String getLastScreenFolder(){     return lastScreenFolder;  }
    public String getLastOnlineDayFolderName(){ return lastOnlineDayFolderName;}
    public static String getBaseFolderPath(){   return baseFolderPath;  }
    public String getFullPathToLastScreenFolder()
    {return baseFolderPath+"\\"+lastOnlineDayFolderName+"\\"+userName+"\\"+lastScreenFolder;}

    public String getFullPathToLastScreen()
    {return baseFolderPath+"\\"+lastOnlineDayFolderName+"\\"+userName+"\\"+lastScreenFolder+"\\"+lastScreenName;}

    public String getFullPathToUserFolderByDay(Date day)
    {return baseFolderPath+"\\"+getDayFolderName(day)+"\\"+userName;}
    public String getFullPathToUserFolderByDay(String dayFolderName)
    {return baseFolderPath+"\\"+dayFolderName+"\\"+userName;}
}
