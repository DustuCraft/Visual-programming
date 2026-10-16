
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class HelloController {

    @FXML
    private Label lblTarget;

    @FXML
    private Button btnChangeText;

    @FXML
    private Button btnToggleVisible;

    @FXML
    private Button btnToggleEnabled;

    @FXML
    private ColorPicker colorPicker;

    private boolean secondText = false;

    @FXML
    protected void onChangeTextClick() {
        // Изменение текста надписи
        secondText = !secondText;
        lblTarget.setText(secondText ? "Текст изменён!" : "Исходный текст");
    }

    @FXML
    protected void onToggleVisibleClick() {
        // Изменение видимости надписи
        lblTarget.setVisible(!lblTarget.isVisible());
    }

    @FXML
    protected void onToggleEnabledClick() {
        // Изменение доступности (enable/disable) кнопки изменения текста
        btnChangeText.setDisable(!btnChangeText.isDisable());
    }

    @FXML
    protected void onColorChanged() {
        // Изменение стиля (цвета текста) надписи через ColorPicker
        Color color = colorPicker.getValue();
        String hex = String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
        lblTarget.setStyle("-fx-text-fill: " + hex + "; -fx-font-size: 18px;");
    }
}
