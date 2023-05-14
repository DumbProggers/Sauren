package com.sauren.sauren;

import com.sauren.sauren.UIelements.UserButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainServerAppController implements Initializable
{
    @FXML
    private ChoiceBox<String> choiceUserStatusCB;
    ObservableList<String> userStatusOL = FXCollections.observableArrayList("Online", "Offline", "All Users");
    @FXML
    private TextField serverPortTF;//порт
    @FXML
    private Label serverIPLbl;
    @FXML
    private VBox clientsVB;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try
        {
            Network mainNetwork=new Network();
            //запускаем сервер с нужными нам параметрами (infoConnection)
            new Thread(()->{
                mainNetwork.Start();
            }).start();
            serverIPLbl.setText(Network.getServerIp());//установить текст для ip
            serverPortTF.setText(Integer.toString(Network.getPort()));//установить текст для порта

            ServerHandler.getUsersFromBase();

            new Thread(()->{//прохожусь по всем онлайн пользователям
                while (true){
                    try
                    {
                        Thread.sleep(1000);
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
               UserButton newBtn=new UserButton();
               newBtn.setUserName(usr.getName());
               newBtn.setUserIp(usr.getIp());
               newBtn.setUserState(usr.userOnline());
               clientsVB.getChildren().add(newBtn);
           }
       }
    }

    public  void changeShowenUsersState(ActionEvent actionEvent)
    {
        updateClientsPannel();
    }
}
