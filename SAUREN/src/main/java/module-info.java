module com.sauren.sauren {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sauren.sauren to javafx.fxml;
    exports com.sauren.sauren;
}