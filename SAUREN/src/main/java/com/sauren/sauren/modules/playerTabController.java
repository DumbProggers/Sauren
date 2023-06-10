package com.sauren.sauren.modules;

import com.sauren.sauren.MainServerAppController;
import com.sauren.sauren.clients.ClientUser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

public class playerTabController extends VBox implements Initializable
{
    @FXML
    public Slider scrollbar;
    @FXML
    public Label labelNameScreen;
    @FXML
    public Button buttonPlayOrStops;
    @FXML
    public DatePicker datePicker;
    @FXML
    public ImageView image;

    private ClientUser currentUser;
    private String basePath;
    ArrayList<String> pathToScreens = new ArrayList<String>();
    static ArrayList<String> screens = new ArrayList<String>();
    String date = "Sat_Jun_10";
    String status;

    public playerTabController(){
        FXMLLoader loader=new FXMLLoader(getClass().getResource("playerTab.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        }catch(IOException ex){ex.printStackTrace();}
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       // scrollbar.setPrefSize(500,200);
        scrollbar.setShowTickLabels(true);
        scrollbar.setShowTickMarks(true);
        setPrefHeight(Integer.MAX_VALUE);
        setPrefWidth(Integer.MAX_VALUE);

        currentUser=MainServerAppController.getCurrentUser();
    }


    @FXML
    protected void getChoiceDate(){
        pathToScreens.clear();

        LocalDate localDate = datePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date1 = Date.from(instant);
        date = date1.toString().replace(" ","_").substring(0,10);

        currentUser=MainServerAppController.getCurrentUser();
        basePath=currentUser.userFolder.getFullPathToUserFolderByDay(date);
        System.out.println(basePath);

        if(!Files.exists(Path.of(basePath))){
            image.setImage(new Image("src/main/resources/com/sauren/sauren/UIelements/Files/kartinki-oshibki-32.jpeg"));
            status = "error";
            //pathToScreens.clear();
            scrollbar.setMax(0);
            scrollbar.setMin(0);
        }

        try(DirectoryStream<Path> dirs = Files.newDirectoryStream( Path.of( basePath ) ))
        {
            ArrayList<String> pathToScreenSelected = new ArrayList<String>();
            ArrayList<String> pathToScreenSelectedScreens = new ArrayList<String>();
            if (dirs == null)
            {
                image.setImage(new Image("com\\sauren\\sauren\\UIelements\\Files\\kartinki-oshibki-32.jpeg"));
                status = "error";
                //pathToScreens.clear();
                scrollbar.setMax(0);
                scrollbar.setMin(0);
            } else
            {
                scrollbar.setMin(0);

                addScreensToSctorage(basePath, pathToScreenSelected);
                for (String pathToStreen : pathToScreenSelected) {
                    listFilesForFolder(new File(pathToStreen), pathToScreenSelectedScreens);
                }
                scrollbar.setMax(pathToScreenSelectedScreens.size() - 1);
                image.setImage(new Image(pathToScreenSelectedScreens.get(0)));

                scrollbar.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                        String currenValue = pathToScreenSelectedScreens.get((int) scrollbar.getValue());
                        if (currenValue.contains(basePath)) {
                            System.out.println(currenValue);
                            image.setImage(new Image(currenValue));
                        }

                    }
                });
            }
        }catch (Exception ex){ex.printStackTrace();}
    }
    public static int addScreensToSctorage(String dirPath, ArrayList<String> paths)
    {
        File[] files = new File(dirPath).listFiles();
        int count = 0;
        if (files!=null)
            for (File file : files)
            {
                if (file.isDirectory()){
                    paths.add(file.getAbsolutePath());
                    addScreensToSctorage(file.getAbsolutePath(),paths);
                    File[] pathScreens = new File(file.getAbsolutePath()).listFiles();
                    for(File path:pathScreens)
                        if(path.isFile()) screens.add(path.getAbsolutePath());
                    count++;
                }
            }
        if(count>0){
            return 0;
        }
        else {
            return -1;
        }
    }
    public void listFilesForFolder(final File folder,ArrayList<String> folderToScreen) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry,folderToScreen);
            } else {
                folderToScreen.add(fileEntry.getAbsolutePath());
            }
        }
    }

}
