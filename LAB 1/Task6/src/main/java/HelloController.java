
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.Year;

public class HelloController {

    @FXML
    private TextField txtBirthYear;

    @FXML
    private Label lblResult;

    @FXML
    protected void onCalculateClick() {
        String input = txtBirthYear.getText();

        // Проверка на пустое поле
        if (input == null || input.isBlank()) {
            showError("Поле не заполнено. Введите год рождения.");
            return;
        }

        int birthYear;
        try {
            // Проверка на ввод текста вместо числа
            birthYear = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            showError("Некорректный ввод. Введите год рождения числом.");
            return;
        }

        int currentYear = Year.now().getValue();

        // Проверка корректности года рождения
        if (birthYear < 1900 || birthYear > currentYear) {
            showError("Некорректный год рождения. Введите значение от 1900 до " + currentYear + ".");
            return;
        }

        int age = currentYear - birthYear;
        lblResult.setText("Ваш примерный возраст: " + age + " лет");
    }

    private void showError(String message) {
        lblResult.setText("");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка ввода");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
