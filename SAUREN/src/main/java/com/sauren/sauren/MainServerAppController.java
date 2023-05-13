package com.sauren.sauren;

import io.netty.channel.Channel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

public class MainServerAppController implements Initializable
{
    //ip:port
    public static String infoConnection;
    @FXML
    public ChoiceBox<String> choiceUserStatusCB;

    @FXML
    public TextField serverPortTF;//порт
    @FXML
    private Label serverIPLbl;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try {

            //проверяем, занят ли порт и устанавливаем свободный если он занят!
            checkPort(  Integer.parseInt(   serverPortTF.getText()  ) );

            //устанавливаем текст
            serverIPLbl.setText(Inet4Address.getLocalHost().getHostAddress());

            //помещаем данные с формы в переменную
            infoConnection = serverIPLbl.getText()+":"+serverPortTF.getText();

            //запускаем сервер с нужными нам параметрами (infoConnection)
            new Thread(Network::Start).start();

            new Thread(()->{


                while (true){
                    try {
                        Thread.sleep(1000);
                        for (String user:ServerHandler.nameUsersOnlineStringList){
                            System.out.print(user+"\n");
                        }

                        for (Channel user:ServerHandler.usersOnlineChannekList){
                            System.out.println(user.remoteAddress());
                        }


                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //Заполнение ChoiceBox значениями о статусе пользователя.
        ObservableList<String> userStatus = FXCollections.observableArrayList("Online", "Offline", "All Users");
        choiceUserStatusCB.setItems(userStatus);
        choiceUserStatusCB.setValue(userStatus.get(0));
    }

    //геттеры
    public static String getPort(){
        int index = infoConnection.indexOf(":");
        return infoConnection.substring(index+1);
    }
    public static String getIp(){
        return infoConnection.replace(":"+getPort(),"");
    }
    private void checkPort(int port){
        while(!isPortAviable(port))
            System.out.println(">Порт "+(port++)+" занят");
        serverPortTF.setText(Integer.toString(port));
    }
    private static boolean isPortAviable(int port) throws IllegalStateException {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (ConnectException e) {
            return true;
        } catch (IOException e) {
            throw new IllegalStateException("Error while trying to check open port", e);
        }
    }
}
