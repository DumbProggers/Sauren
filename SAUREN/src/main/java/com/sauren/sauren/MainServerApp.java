package com.sauren.sauren;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainServerApp extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root= FXMLLoader.load(getClass().getResource("MainServerApp.fxml"));
        Scene mainSc=new Scene(root);
        stage.setScene(mainSc);
        stage.show();
    }

    public static void main(String[] args) {    launch(args);   }
}
