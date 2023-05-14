package com.sauren.sauren.UIelements;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class UserButton extends VBox
{
    @FXML
    private Label userNameLbl;
    @FXML
    private Label userIPLbl;
    public UserButton()
    {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("UserButton.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        }catch(Exception ex){ex.printStackTrace();}
    }

    public UserButton(String Name,String ip)
    {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("UserButton.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        }catch(Exception ex){ex.printStackTrace();}

        userNameLbl.setText(Name);
        userIPLbl.setText(ip);
    }

    public void setUserName(String name)    {userNameLbl.setText(name);}
    public void setUserIp(String ip)    {userIPLbl.setText(ip);}
}
