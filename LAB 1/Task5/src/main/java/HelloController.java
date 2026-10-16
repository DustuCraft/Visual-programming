
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {

    @FXML
    private TextField txtA;

    @FXML
    private TextField txtB;

    @FXML
    private Label lblResult;

    private double[] readValues() {
        double a = Double.parseDouble(txtA.getText().trim().replace(",", "."));
        double b = Double.parseDouble(txtB.getText().trim().replace(",", "."));
        return new double[]{a, b};
    }

    @FXML
    protected void onAddClick() {
        try {
            double[] v = readValues();
            lblResult.setText("Результат: " + (v[0] + v[1]));
        } catch (NumberFormatException e) {
            lblResult.setText("Ошибка: введите корректные числа");
        }
    }

    @FXML
    protected void onSubtractClick() {
        try {
            double[] v = readValues();
            lblResult.setText("Результат: " + (v[0] - v[1]));
        } catch (NumberFormatException e) {
            lblResult.setText("Ошибка: введите корректные числа");
        }
    }

    @FXML
    protected void onMultiplyClick() {
        try {
            double[] v = readValues();
            lblResult.setText("Результат: " + (v[0] * v[1]));
        } catch (NumberFormatException e) {
            lblResult.setText("Ошибка: введите корректные числа");
        }
    }

    @FXML
    protected void onDivideClick() {
        try {
            double[] v = readValues();
            if (v[1] == 0) {
                lblResult.setText("Ошибка: деление на ноль");
                return;
            }
            lblResult.setText("Результат: " + (v[0] / v[1]));
        } catch (NumberFormatException e) {
            lblResult.setText("Ошибка: введите корректные числа");
        }
    }
}
