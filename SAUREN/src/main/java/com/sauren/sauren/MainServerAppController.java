package com.sauren.sauren;

import io.netty.channel.Channel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.*;
import java.util.ResourceBundle;

public class MainServerAppController implements Initializable
{
    @FXML
    private ChoiceBox<String> choiceUserStatusCB;
    @FXML
    private TextField serverPortTF;//порт
    @FXML
    private Label serverIPLbl;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try {
            Network mainNetwork=new Network();
            //запускаем сервер с нужными нам параметрами (infoConnection)
            new Thread(()->{
                mainNetwork.Start();
            }).start();
            serverIPLbl.setText(Network.getServerIp());//установить текст для ip
            serverPortTF.setText(Integer.toString(Network.getPort()));//установить текст для порта
            System.out.println(Network.getServerIp());
            System.out.println(Network.getPort());

            new Thread(()->{
                while (true){
                    try
                    {
                        Thread.sleep(1000);
                        for (String user:ServerHandler.nameUsersOnlineStringList)
                        {
                            System.out.print(user+"\n");
                        }

                        for (Channel user:ServerHandler.usersOnlineChannekList)
                        {
                            System.out.println(user.remoteAddress());
                        }
                    } catch (InterruptedException e) { throw new RuntimeException(e); }
                }
            }).start();
        } catch (Exception e) { throw new RuntimeException(e); }

        //Заполнение ChoiceBox значениями о статусе пользователя.
        ObservableList<String> userStatus = FXCollections.observableArrayList("Online", "Offline", "All Users");
        choiceUserStatusCB.setItems(userStatus);
        choiceUserStatusCB.setValue(userStatus.get(0));
    }
}
