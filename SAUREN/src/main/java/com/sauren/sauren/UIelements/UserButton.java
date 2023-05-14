package com.sauren.sauren.UIelements;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

public class UserButton extends VBox
{
    @FXML
    private Label userNameLbl;
    @FXML
    private Label userIPLbl;
    @FXML
    private Label userStateLbl;
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
    public void setUserState(boolean online)
    {
        if(online)
        {
            userStateLbl.setText("online");
            userStateLbl.setTextFill(Paint.valueOf("#3adf6e"));
        }
        else
        {
            userStateLbl.setText("offline");
            userStateLbl.setTextFill(Paint.valueOf("#de3c3c"));
        }
    }
}
