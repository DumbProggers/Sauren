package com.sauren.sauren;

import com.sauren.sauren.UIelements.UserButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.awt.Desktop;

public class MainServerAppController implements Initializable
{
    @FXML
    public Label infoUserPieChart;
    @FXML
    private ChoiceBox<String> choiceUserStatusCB;
    ObservableList<String> userStatusOL = FXCollections.observableArrayList("Online", "Offline", "All Users");//"фильтры" для левой панели
    Timeline getLastScreenT;//таймер для получения последнего полученного скриншота пользователя
    @FXML
    private TextField connectionInfoLbl;//ip:port
    @FXML
    private VBox clientsVB;
    @FXML
    private VBox userInfoVB;
    @FXML
    private Label curUserNameLbl;
    @FXML
    private Label curUserStateLbl;
    @FXML
    private ImageView lastScreenImg;
    @FXML
    private Label curUserIPLbl;
    @FXML
    private PieChart pieChart;
    @FXML
    private TextField msgToUserTF;
    private static ClientUser currentUser;//выбранный пользователь
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        pieChart.setStyle("-fx-text-fill: #191970;");//для белого цвета текста
        connectionInfoLbl.setStyle("-fx-text-inner-color: white;" + "-fx-background-color:  #0067a5");//для белого цвета текста

        userInfoVB.setVisible(false);//прячу панель с информацией о выбранном пользователе
        try
        {
            Network mainNetwork=new Network();
            //запускаем сервер с нужными нам параметрами (infoConnection)
            new Thread(()->{     mainNetwork.Start();   }).start();

            connectionInfoLbl.setText(Network.getServerIp()+":"+Integer.toString(Network.getPort()));//вывести ip и порт сервера
            ServerHandler.getUsersFromBase();//получить данные ранее подключенных пользователей

            new Thread(()->{//прохожусь по всем онлайн пользователям
                while (true){
                    try
                    {
                        Thread.sleep(2000);
                        System.out.println("\n#Online users:");
                        for(ClientUser usr:ServerHandler.users)//прохожусь по всем онлайн пользователям
                        {
                            if(usr.userOnline()) {
                                System.out.println(usr.getName() + " : " + usr.getIp());
                            }
                        }
                        System.out.println("###########################");
                    } catch (InterruptedException e) { throw new RuntimeException(e); }
                }
            }).start();

            Timeline timeline = new Timeline (//таймер для периодичекого обновления базы и панели пользователей
                    new KeyFrame(
                            Duration.millis(1000), //1 секунда
                            ae ->
                            {
                                ServerHandler.saveUsersToUsersBase();
                                updateClientsPannel();
                            })
            );
            timeline.setCycleCount(Integer.MAX_VALUE); //Ограничим число повторений
            timeline.play(); //Запускаем

        } catch (Exception e) { throw new RuntimeException(e); }

        //Заполнение ChoiceBox значениями о статусе пользователя.
        choiceUserStatusCB.setItems(userStatusOL);
        choiceUserStatusCB.setValue(userStatusOL.get(0));
    }


    public void updateClientsPannel()//заново вывести кнопки пользователей в левую панель
    {
        clientsVB.getChildren().clear();
        boolean onlineUsersFilter=choiceUserStatusCB.getValue().equals(userStatusOL.get(0));
        boolean offlineUsersFilter=choiceUserStatusCB.getValue().equals(userStatusOL.get(1));
        boolean allUsersFilter=choiceUserStatusCB.getValue().equals(userStatusOL.get(2));
       for(ClientUser usr:ServerHandler.users)
       {
           if((onlineUsersFilter&&usr.userOnline()) || (offlineUsersFilter && !usr.userOnline()) || allUsersFilter)
           {//создаем кнопку
               UserButton newBtn=new UserButton(this,usr);
               newBtn.setUserName(usr.getName());
               newBtn.setUserIp(usr.getIp());
               newBtn.setUserState(usr.userOnline());
               newBtn.setLastOnlineDate(usr.getLastOnlineDate());
               clientsVB.getChildren().add(newBtn);
           }
       }
    }

    public  void changeShowenUsersState(ActionEvent actionEvent)
    {
        updateClientsPannel();
    }

    public void showUserInfo(ClientUser usr) throws ParseException//показывать всю информацию о пользователе в правой части
    {
        currentUser=usr;
        userInfoVB.setVisible(true);
        curUserNameLbl.setText(usr.getName());//установка имени
        curUserIPLbl.setText(usr.getIp());//установка ip пользователя
        if(getLastScreenT!=null)    getLastScreenT.stop();
        Timeline timeline = new Timeline (//таймер для периодичекого обновления последнего скриншота
                new KeyFrame(
                        Duration.millis(500), //0,5  сек
                        ae ->
                        {
                            if(new File(usr.getLastScreenPath()).exists())//если файл с путем существует
                                lastScreenImg.setImage(new Image(usr.getLastScreenPath()));//установка последнего полученного скриншота

                            if(usr.userOnline())//установка статуса (онлайн/оффлайн)
                            {
                                curUserStateLbl.setText("online");
                                curUserStateLbl.setTextFill(Paint.valueOf("#3adf6e"));
                            }
                            else
                            {
                                curUserStateLbl.setText("offline");
                                curUserStateLbl.setTextFill(Paint.valueOf("#de3c3c"));
                            }
                        })
        );
        getLastScreenT=timeline;
        getLastScreenT.setCycleCount(Integer.MAX_VALUE); //Ограничим число повторений
        getLastScreenT.play(); //Запускаем

        pieChart(usr);
    }
    public void pieChart(ClientUser user) throws ParseException //вывод круговой диаграммы(обновление)
    {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        //V |сохраняю путь к папке пользователя за текущий день| V
        File file = new File(ServerHandler.file_dir+File.separator+ServerHandler.getCurrentDate().substring(0,10)+File.separator+user.getName());
        File[] dirs = file.listFiles(new FileFilter()
        {
            @Override
                public boolean accept(File pathname){   return pathname.isDirectory();  }

        });
        String info="";
        addDatainPieChart(dirs,pieChartData,info);
        pieChart.setData(pieChartData);
        drawPieChar(pieChart,pieChartData,dirs,infoUserPieChart,user.getName());
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
    public static void addHandlerPieChar(PieChart pieChart,Label userInfo)
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

    public static void drawPieChar(PieChart pieChart,ObservableList<PieChart.Data> pieChartData,File[] dirs,Label userInfo,String userNameSelected){
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
                        data2.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                String pathToScreen = ServerHandler.file_dir+File.separator+ServerHandler.getCurrentDate().substring(0,10)+File.separator+currentUser.getName()+"\\"+data.getName()+"\\"+data2.getName();
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
        int delay=1000;
        return delay;
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
        if (files.length != 0&&files!=null)
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    count+=getFilesCount(file.getAbsolutePath());
                }
                else
                {
                    count++;
                }
            }
        return count;
    }

    public void sendMessageToUser(ActionEvent actionEvent) //отправить сообщение клиенту
    {
        if(currentUser.userOnline())
        {
            if(msgToUserTF.getText().length()>0)
            {
                String msg = "$MSG$";
                msg += msgToUserTF.getText();
                msgToUserTF.setText("");
                currentUser.getChannel().writeAndFlush(msg);
                System.out.println(">SEND MESSAGE TO USER " + currentUser.getName() + ": " + msg);
            }
        }
        else System.out.println(">!CAN`T SEND MESSAGE, USER IS OFFLINE");
    }
}
