
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Button btnAction;

    @FXML
    private TextField txtInput;

    @FXML
    private Label lblEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Обработка изменения текста в поле ввода
        txtInput.textProperty().addListener((obs, oldValue, newValue) ->
                lblEvent.setText("Событие: изменение текста. Текущее значение: \"" + newValue + "\""));

        // Обработка события наведения мыши
        btnAction.setOnMouseEntered((MouseEvent e) ->
                lblEvent.setText("Событие: курсор мыши наведён на кнопку"));

        btnAction.setOnMouseExited((MouseEvent e) ->
                lblEvent.setText("Событие: курсор мыши покинул кнопку"));

        // Обработка нажатия мыши прямо на кнопке (доп. к onAction)
        btnAction.setOnMousePressed((MouseEvent e) ->
                lblEvent.setText(String.format("Событие: нажатие мыши в точке (%.0f, %.0f)", e.getX(), e.getY())));
    }

    @FXML
    protected void onActionButtonClick() {
        // Стандартное событие onAction
        lblEvent.setText("Событие: onAction — кнопка нажата и отпущена");
    }
}
