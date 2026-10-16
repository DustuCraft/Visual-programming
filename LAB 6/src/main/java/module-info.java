module com.example.lab6 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.lab6 to javafx.fxml, javafx.base;
    exports com.example.lab6;
}