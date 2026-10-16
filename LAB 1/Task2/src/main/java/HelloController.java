
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloController {

    @FXML
    private Label lblResult;

    @FXML
    protected void onGreetClick() {
        lblResult.setText("Здравствуйте! Рады видеть вас в приложении.");
    }

    @FXML
    protected void onInfoClick() {
        lblResult.setText("Лабораторная работа №2. Тема: обработка событий JavaFX.");
    }

    @FXML
    protected void onExitClick() {
        // Закрываем окно приложения
        Stage stage = (Stage) lblResult.getScene().getWindow();
        stage.close();
    }
}
