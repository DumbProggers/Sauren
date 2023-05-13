module com.sauren.sauren {
    requires javafx.controls;
    requires javafx.fxml;
    requires netty.all;


    opens com.sauren.sauren to javafx.fxml;
    exports com.sauren.sauren;
}