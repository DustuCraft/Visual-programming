
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TextField txtFio;

    @FXML
    private TextField txtCity;

    @FXML
    private RadioButton rbMale;

    @FXML
    private RadioButton rbFemale;

    @FXML
    private ComboBox<String> cbCourse;

    @FXML
    private CheckBox chkNewsletter;

    @FXML
    private CheckBox chkSms;

    @FXML
    private Label lblResult;

    private final ToggleGroup genderGroup = new ToggleGroup();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rbMale.setToggleGroup(genderGroup);
        rbFemale.setToggleGroup(genderGroup);
        rbMale.setSelected(true);

        cbCourse.getItems().addAll("1 курс", "2 курс", "3 курс", "4 курс", "Магистратура");
        cbCourse.getSelectionModel().selectFirst();
    }

    @FXML
    protected void onSubmitClick() {
        if (txtFio.getText() == null || txtFio.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Заполните поле ФИО.");
            alert.showAndWait();
            return;
        }

        Toggle selected = genderGroup.getSelectedToggle();
        String gender = selected == rbMale ? "Мужской" : "Женский";

        StringBuilder extras = new StringBuilder();
        if (chkNewsletter.isSelected()) extras.append("рассылка новостей; ");
        if (chkSms.isSelected()) extras.append("SMS-уведомления; ");
        if (extras.length() == 0) extras.append("не выбрано");

        String summary = String.format(
                "ФИО: %s%nГород: %s%nПол: %s%nКурс: %s%nДополнительно: %s",
                txtFio.getText(), txtCity.getText(), gender,
                cbCourse.getValue(), extras.toString());

        lblResult.setText(summary);
    }
}
