package kz.atu.lab03;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML
    private TextField txtNumber1;
    @FXML
    private TextField txtNumber2;
    @FXML
    private Label lblResult;
    @FXML
    private Label lblOperation;
    @FXML
    private Label lblCounter;

    // Задание 4 (самостоятельная часть): счётчик выполненных операций
    private int operationCount = 0;

    /**
     * Общий обработчик ActionEvent для всех арифметических кнопок.
     * Источник события определяется через event.getSource().
     */
    @FXML
    private void onOperation(ActionEvent event) {

        // 1. Проверка пустых полей
        if (txtNumber1.getText().isBlank() || txtNumber2.getText().isBlank()) {
            showError("Введите оба числа.");
            return;
        }

        try {
            // 2. Преобразование введённых строк в числа (может выбросить NumberFormatException)
            double number1 = Double.parseDouble(txtNumber1.getText());
            double number2 = Double.parseDouble(txtNumber2.getText());

            // 3. Определение источника события — какая именно кнопка была нажата
            Button button = (Button) event.getSource();
            String operation = button.getText();

            double result;

            switch (operation) {
                case "+":
                    result = number1 + number2;
                    break;

                case "-":
                    result = number1 - number2;
                    break;

                case "×":
                    result = number1 * number2;
                    break;

                case "÷":
                    if (number2 == 0) {
                        showError("Деление на ноль невозможно.");
                        return;
                    }
                    result = number1 / number2;
                    break;

                // Задание 1 (самостоятельная часть): возведение в степень
                case "xʸ":
                    result = Math.pow(number1, number2);
                    break;

                // Задание 2 (самостоятельная часть): остаток от деления
                case "%":
                    if (number2 == 0) {
                        showError("Деление на ноль невозможно.");
                        return;
                    }
                    result = number1 % number2;
                    break;

                // Индивидуальный вариант №20: (A + B) / 2
                case "(A+B)/2":
                    result = (number1 + number2) / 2;
                    break;

                default:
                    return;
            }

            // Задание 3 (самостоятельная часть): округление результата до 2 знаков
            lblResult.setText(String.format("Результат: %.2f", result));
            lblOperation.setText("Операция: " + operation);

            // Задание 4 (самостоятельная часть): счётчик операций
            operationCount++;
            lblCounter.setText("Выполнено операций: " + operationCount);

        } catch (NumberFormatException e) {
            showError("Введите корректные числа.");
        }
    }

    @FXML
    private void onClearClick() {
        txtNumber1.clear();
        txtNumber2.clear();
        lblResult.setText("Результат: 0");
        lblOperation.setText("Операция: —");
        txtNumber1.requestFocus();
    }

    @FXML
    private void onExitClick() {
        Platform.exit();
    }

    // Работа со свойством disable
    @FXML
    private void onLockClick() {
        txtNumber1.setDisable(true);
        txtNumber2.setDisable(true);
    }

    @FXML
    private void onUnlockClick() {
        txtNumber1.setDisable(false);
        txtNumber2.setDisable(false);
        txtNumber1.requestFocus();
    }

    // Обработка MouseEvent
    @FXML
    private void onMouseEntered() {
        lblOperation.setText("Выберите операцию");
    }

    @FXML
    private void onMouseExited() {
        if (lblResult.getText().equals("Результат: 0")) {
            lblOperation.setText("Операция: —");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
