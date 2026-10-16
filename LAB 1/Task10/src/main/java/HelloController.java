
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class HelloController {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtSurname;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private PasswordField pfConfirmPassword;

    @FXML
    private CheckBox chkAgree;

    @FXML
    private Label lblResult;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    @FXML
    protected void onRegisterClick() {
        // Проверка пустых полей
        if (isBlank(txtName.getText()) || isBlank(txtSurname.getText())
                || isBlank(txtEmail.getText()) || isBlank(pfPassword.getText())
                || isBlank(pfConfirmPassword.getText())) {
            showError("Заполните все поля формы.");
            return;
        }

        // Проверка формата email
        if (!EMAIL_PATTERN.matcher(txtEmail.getText().trim()).matches()) {
            showError("Введите корректный адрес электронной почты.");
            return;
        }

        // Проверка длины пароля
        if (pfPassword.getText().length() < 6) {
            showError("Пароль должен содержать не менее 6 символов.");
            return;
        }

        // Проверка совпадения паролей
        if (!pfPassword.getText().equals(pfConfirmPassword.getText())) {
            showError("Пароли не совпадают.");
            return;
        }

        // Проверка согласия с условиями
        if (!chkAgree.isSelected()) {
            showError("Необходимо согласиться с условиями использования.");
            return;
        }

        lblResult.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        lblResult.setText("Регистрация успешно завершена, " + txtName.getText() + "!");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Регистрация");
        alert.setHeaderText(null);
        alert.setContentText("Пользователь " + txtName.getText() + " " + txtSurname.getText()
                + " успешно зарегистрирован.");
        alert.showAndWait();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private void showError(String message) {
        lblResult.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        lblResult.setText(message);
    }
}
