module com.example.synkit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.synkit to javafx.fxml;
    exports com.example.synkit;
}