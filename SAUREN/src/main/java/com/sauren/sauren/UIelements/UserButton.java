package com.sauren.sauren.UIelements;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class UserButton extends VBox
{
    public UserButton()
    {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("UserButton.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        }catch(Exception ex){ex.printStackTrace();}
    }
}
