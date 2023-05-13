package com.sauren.sauren;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/*|     ---<Bychkovsky Vladislav>---+---<Sidorov Ilya>---+---<Rakhmanov Temur>---      |*/

public class MainServerApp extends Application // Главный класс всего сервера
{
    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root= FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainServerApp.fxml")));
        Scene mainSc=new Scene(root);
        stage.setScene(mainSc);
        stage.show();
    }

    public static void main(String[] args) {    launch(args);   }
}
