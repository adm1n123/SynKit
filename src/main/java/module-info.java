module com.example.synkit {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.synkit to javafx.fxml;
    exports com.example.synkit;
}