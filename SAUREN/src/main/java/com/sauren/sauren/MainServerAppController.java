package com.sauren.sauren;

import com.sauren.sauren.UIelements.UserButton;
import com.sauren.sauren.UIelements.UserPieChart;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.awt.Desktop;

public class MainServerAppController implements Initializable
{
    @FXML
    public Label infoUserPieChart;
    @FXML
    private ChoiceBox<String> choiceUserStatusCB;
    ObservableList<String> userStatusOL = FXCollections.observableArrayList("Online", "Offline", "All Users");//"фильтры" для левой панели
    Timeline getLastScreenT;//таймер для получения последнего полученного скриншота пользователя
    @FXML
    private TextField connectionInfoLbl;//ip:port
    @FXML
    private VBox clientsVB;
    @FXML
    private VBox userInfoVB;
    @FXML
    private Label curUserNameLbl;
    @FXML
    private Label curUserStateLbl;
    @FXML
    private ImageView lastScreenImg;
    @FXML
    private Label curUserIPLbl;
    @FXML
    private PieChart pieChart;
    @FXML
    private TextField msgToUserTF;
    private static ClientUser currentUser;//выбранный пользователь
    private static boolean needToUpdateUsersPanel=false;//нужно ли обновить левую панель со всеми пользователями(true, когда подключился, отключился, или обновил "фильтр")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        pieChart.setStyle("-fx-text-fill: #191970;");//для белого цвета текста
        connectionInfoLbl.setStyle("-fx-text-inner-color: white;" + "-fx-background-color:  #0067a5");//для белого цвета текста
        userInfoVB.setVisible(false);//прячу панель с информацией о выбранном пользователе
        try
        {
            Network mainNetwork=new Network();//чтобы сработал конструктор
            //запускаем сервер с нужными нам параметрами (infoConnection)
            new Thread(Network::Start).start();

            connectionInfoLbl.setText(Network.getServerIp()+":"+Network.getPort());//вывести ip и порт сервера
            ServerHandler.getUsersFromBase();//получить данные ранее подключенных пользователей

            Timeline timeline = new Timeline (//таймер для периодичекого обновления базы и панели пользователей
                    new KeyFrame(
                            Duration.millis(1000), //1 секунда
                            ae ->
                            {
                                updateClientsPannel();
                            })
            );
            timeline.setCycleCount(Integer.MAX_VALUE); //Ограничим число повторений
            timeline.play(); //Запускаем

        } catch (Exception e) { throw new RuntimeException(e); }

        //Заполнение ChoiceBox значениями о статусе пользователя.
        choiceUserStatusCB.setItems(userStatusOL);
        choiceUserStatusCB.setValue(userStatusOL.get(0));
    }


    public void updateClientsPannel()//заново вывести кнопки пользователей в левую панель
    {
        if(needToUpdateUsersPanel)
        {
            System.out.println(">UPDATE USERS PANEL(left)");
            clientsVB.getChildren().clear();
            boolean onlineUsersFilter = choiceUserStatusCB.getValue().equals(userStatusOL.get(0));
            boolean offlineUsersFilter = choiceUserStatusCB.getValue().equals(userStatusOL.get(1));
            boolean allUsersFilter = choiceUserStatusCB.getValue().equals(userStatusOL.get(2));
            for (ClientUser usr : ServerHandler.users)
            {
                if ((onlineUsersFilter && usr.userOnline()) || (offlineUsersFilter && !usr.userOnline()) || allUsersFilter)
                {//создаем кнопку
                    UserButton newBtn = new UserButton(this, usr);
                    newBtn.setUserName(usr.getName());
                    newBtn.setUserIp(usr.getIp());
                    newBtn.setUserState(usr.userOnline());
                    newBtn.setLastOnlineDate(usr.getLastOnlineDate());
                    clientsVB.getChildren().add(newBtn);
                }
            }
            needToUpdateUsersPanel=false;
        }
    }

    public  void changeShowenUsersState(ActionEvent actionEvent)
    {
        needToUpdateUsersPanel=true;
        updateClientsPannel();
    }

    public void showUserInfo(ClientUser usr) throws ParseException//показывать всю информацию о пользователе в правой части
    {
        currentUser=usr;
        userInfoVB.setVisible(true);
        curUserNameLbl.setText(usr.getName());//установка имени
        curUserIPLbl.setText(usr.getIp());//установка ip пользователя
        if(getLastScreenT!=null)    getLastScreenT.stop();
        getLastScreenT= new Timeline (//таймер для периодичекого обновления последнего скриншота
                new KeyFrame(
                        Duration.millis(500), //0,5  сек
                        ae ->
                        {
                            if(new File(    usr.userFolder.getFullPathToLastScreen()   ).exists())//если файл с путем существует
                                lastScreenImg.setImage(new Image(   usr.userFolder.getFullPathToLastScreen()  ));//установка последнего полученного скриншота

                            if(usr.userOnline())//установка статуса (онлайн/оффлайн)
                            {
                                curUserStateLbl.setText("online");
                                curUserStateLbl.setTextFill(Paint.valueOf("#3adf6e"));
                            }
                            else
                            {
                                curUserStateLbl.setText("offline");
                                curUserStateLbl.setTextFill(Paint.valueOf("#de3c3c"));
                            }
                        })
        );
        getLastScreenT.setCycleCount(Integer.MAX_VALUE); //Ограничим число повторений
        getLastScreenT.play(); //Запускаем

        UserPieChart.redraw(pieChart,infoUserPieChart,currentUser);
    }
    public static void setNeedToUpdateUsersPanel(boolean need){ needToUpdateUsersPanel=need;}

    public void sendMessageToUser(ActionEvent actionEvent) //отправить сообщение клиенту
    {
        if(currentUser.userOnline())
        {
            if(msgToUserTF.getText().length()>0)
            {
                String msg = "$MSG$";
                msg += msgToUserTF.getText();
                msgToUserTF.setText("");
                currentUser.getChannel().writeAndFlush(msg);
                System.out.println(">SEND MESSAGE TO USER " + currentUser.getName() + ": " + msg);
            }
        }
        else System.out.println(">!CAN`T SEND MESSAGE, USER IS OFFLINE");
    }
}
