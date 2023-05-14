package com.sauren.sauren.UIelements;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class UserButton extends VBox
{
    @FXML
    private ImageView userIcoImg;
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

        roundImage(userIcoImg);

        // set a clip to apply rounded border to the original image.




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


    public static void roundImage(ImageView image){
        Rectangle clip = new Rectangle(
                image.getFitWidth(), image.getFitHeight()
        );
        clip.setArcWidth(100);
        clip.setArcHeight(100);
        image.setClip(clip);

        // snapshot the rounded image.
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage imageCh = image.snapshot(parameters, null);

        // remove the rounding clip so that our effect can show through.
        image.setClip(null);

        // apply a shadow effect.
        image.setEffect(new DropShadow(5, Color.GRAY));

        // store the rounded image in the imageView.
        image.setImage(imageCh);
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
