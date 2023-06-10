module com.sauren.sauren {
    requires javafx.controls;
    requires javafx.fxml;
    requires netty.all;
    requires java.desktop;


    opens com.sauren.sauren to javafx.fxml;
    exports com.sauren.sauren;
    exports com.sauren.sauren.UIelements;
    opens com.sauren.sauren.UIelements to javafx.fxml;
    exports com.sauren.sauren.clients;
    opens com.sauren.sauren.clients to javafx.fxml;
    exports com.sauren.sauren.modules;
    opens com.sauren.sauren.modules to javafx.fxml;
}