package com.sauren.sauren;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainServerAppController implements Initializable
{
    @FXML
    public ChoiceBox choiceUserStatusCB;
    @FXML
    private Label serverIPLbl;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try {
            serverIPLbl.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        //Заполнение ChoiceBox значениями о статусе пользователя.
        ObservableList<String> userStatus = FXCollections.observableArrayList("Online", "Offline", "All Users");
        choiceUserStatusCB.setItems(userStatus);
        choiceUserStatusCB.setValue(userStatus.get(0));

    }
}
