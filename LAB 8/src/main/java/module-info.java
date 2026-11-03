module kz.atu.lab08 {
    requires javafx.controls;
    requires javafx.fxml;

    opens kz.atu.lab08 to javafx.fxml;
    exports kz.atu.lab08;
}
