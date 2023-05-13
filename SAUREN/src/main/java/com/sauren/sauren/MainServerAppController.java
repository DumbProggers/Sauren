package com.sauren.sauren;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainServerAppController implements Initializable
{
    @FXML
    public ChoiceBox choiceUserStatusCB;

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


            serverIPLbl.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //Заполнение ChoiceBox значениями о статусе пользователя.
        ObservableList<String> userStatus = FXCollections.observableArrayList("Online", "Offline", "All Users");
        choiceUserStatusCB.setItems(userStatus);
        choiceUserStatusCB.setValue(userStatus.get(0));

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
