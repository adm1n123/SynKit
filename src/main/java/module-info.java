module com.example.synkit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jsch;


    opens com.example.synkit to javafx.fxml;
    exports com.example.synkit;
}