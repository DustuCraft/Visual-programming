module kz.atu.lab03 {
    requires javafx.controls;
    requires javafx.fxml;

    opens kz.atu.lab03 to javafx.fxml;
    exports kz.atu.lab03;
}
