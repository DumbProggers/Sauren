package com.sauren.sauren.UIelements;

import com.sauren.sauren.ClientUser;
import com.sauren.sauren.ServerHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class UserPieChart
{
    public static void redraw(PieChart pieChart, Label infoUserPieChart, ClientUser currentUser) throws ParseException //вывод круговой диаграммы(обновление)
    {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        //V |сохраняю путь к папке пользователя за текущий день| V
        File file = new File(currentUser.getUserFolderInfo().getFullPathToUserFolderByDay(new Date()));
        File[] dirs = file.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname){   return pathname.isDirectory();  }

        });
        String info="";
        addDatainPieChart(dirs,pieChartData,info);
        pieChart.setData(pieChartData);
        drawPieChar(pieChart,pieChartData,dirs,infoUserPieChart,currentUser);
    }
    public static void addDatainPieChart(File[] dirs,ObservableList<PieChart.Data> pieChartData,String info) throws ParseException {
        int count = 0;
        if (dirs == null)   System.out.println(">|PieChart| User NOT SELECTED");
        else
        {
            for (File program: dirs){
                //pieChartData.add(count++,new PieChart.Data(program.getName(),(getFilesCount(program.getAbsolutePath()))*(int)getDelay()));
                pieChartData.add(count++,new PieChart.Data(program.getName(),(getFilesCount(program.getAbsolutePath()))*getDelay()));
                info+="APPLICATION|\t"+program.getName()+"\t|TOTAL TIME|\t"+getTimeinExe(program.getAbsolutePath())+"\n\n";
                for (File project: Objects.requireNonNull(new File(program.getAbsolutePath()).listFiles())){
                    long msInProject = (getFilesCount(project.getAbsolutePath())*getDelay());
                    info+="\tWINDOW|\t"+project.getName()+"\n\t\t|TIME|\t"+getTimeinProject(msInProject)+"\n";
                }
            }
        }
    }
    public static void addHandlerPieChar(PieChart pieChart, Label userInfo)
    {
        //Обработка событий
        for (final PieChart.Data data2 : pieChart.getData()) {
            data2.getNode().addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    try {
                        userInfo.setText("Project: "+data2.getName()+"\n"
                                +"Working hours in ("+data2.getName()+"):\n\t"+getTimeinProject((long) data2.getPieValue()));
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }

    public static void drawPieChar(PieChart pieChart,ObservableList<PieChart.Data> pieChartData,File[] dirs,Label userInfo, ClientUser currentUser){
        addHandlerPieChar(pieChart,userInfo);
        for (final PieChart.Data data : pieChart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    pieChartData.clear();
                    pieChart.setData(pieChartData);
                    data.getChart();
                    System.out.println(data.getName());
                    int index = 0;
                    for (File program: dirs){
                        if(data.getName().equals(program.getName())) {
                            for (File project : new File(program.getAbsolutePath()).listFiles()) {
                                if (project.isDirectory()) {
                                    //System.out.println(data.getName()+" - "+project.getName());
                                    try {
                                        pieChartData.add(index++, new PieChart.Data(project.getName(), (getFilesCount(project.getAbsolutePath())) * getDelay()));
                                    } catch (ParseException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            }
                        }
                    }
                    for (final PieChart.Data data2 : pieChart.getData()) {
                        data2.getNode().addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                try {
                                    userInfo.setText("Project: "+data.getName()+"\n"
                                            +"Working hours in ("+data2.getName()+"):\n\t"+getTimeinProject((long) data2.getPieValue()));
                                } catch (ParseException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
                    }
                    for (final PieChart.Data data2 : pieChart.getData()) {
                        data2.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle(MouseEvent e) {
                                String pathToScreen = currentUser.getUserFolderInfo().getFullPathToUserFolderByDay(new Date())+"\\"+data.getName()+"\\"+data2.getName();
                                try {
                                    Desktop.getDesktop().open(new File(pathToScreen));
                                    //Runtime.getRuntime().exec("explorer "+pathToScreen);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
                    }
                    pieChart.setData(pieChartData);
                }
            });
        }
    }

    public static long getDelay() throws ParseException
    {
        return ClientUser.getScreensDelay();
    }
    public static String getTimeinExe(String pathAbs) throws ParseException {
        int filesCount = getFilesCount(pathAbs);

        long diffAll = filesCount* getDelay();
        long diffSecondsAll = diffAll / 1000 % 60;
        long diffMinutesAll = diffAll / (60 * 1000) % 60;
        long diffHoursAll = diffAll / (60 * 60 * 1000) % 24;
        long diffDaysAll = diffAll / (24 * 60 * 60 * 1000);
        return diffDaysAll+" days "+diffHoursAll+" hours "+diffMinutesAll+" min "+diffSecondsAll+" sec";
    }
    public static String getTimeinProject(long timeInProject) throws ParseException
    {
        long diffSecondsAll = timeInProject / 1000 % 60;
        long diffMinutesAll = timeInProject / (60 * 1000) % 60;
        long diffHoursAll = timeInProject / (60 * 60 * 1000) % 24;
        long diffDaysAll = timeInProject / (24 * 60 * 60 * 1000);
        return diffDaysAll+" days "+diffHoursAll+" hours "+diffMinutesAll+" min "+diffSecondsAll+" sec";
    }
    public static int getFilesCount(String dirPath)
    {
        int count = 0;
        File[] files = new File(dirPath).listFiles();
        if (files!=null)
            for (File file : files)
            {
                if (file.isDirectory()) {
                    count += getFilesCount(file.getAbsolutePath());
                } else {
                    count++;
                }
            }
        return count;
    }

}
