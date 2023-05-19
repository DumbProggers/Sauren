module com.sauren.sauren {
    requires javafx.controls;
    requires javafx.fxml;
    requires netty.all;
    requires java.desktop;


    opens com.sauren.sauren to javafx.fxml;
    exports com.sauren.sauren;
    exports com.sauren.sauren.UIelements;
    opens com.sauren.sauren.UIelements to javafx.fxml;
}