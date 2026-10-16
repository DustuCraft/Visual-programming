
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TextField txtProduct;

    @FXML
    private TextField txtPrice;

    @FXML
    private Spinner<Integer> spQuantity;

    @FXML
    private Label lblResult;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Настройка спиннера: количество товара от 1 до 100, шаг 1, начальное значение 1
        spQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        spQuantity.setEditable(true);
    }

    @FXML
    protected void onCalculateClick() {
        String product = txtProduct.getText();

        if (product == null || product.isBlank()) {
            showError("Введите наименование товара.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(txtPrice.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            showError("Введите корректную цену товара.");
            return;
        }

        if (price < 0) {
            showError("Цена не может быть отрицательной.");
            return;
        }

        int quantity = spQuantity.getValue();
        double total = price * quantity;

        lblResult.setText(String.format("Товар: %s%nКоличество: %d%nИтого к оплате: %.2f",
                product, quantity, total));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка ввода");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
