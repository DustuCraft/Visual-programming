
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Label lblFio;

    @FXML
    private Label lblGroup;

    @FXML
    private Label lblSpeciality;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Данные визитной карточки можно задавать здесь программно
        // либо оставить значения, заданные в Scene Builder
        lblFio.setText("Иванов Иван Иванович");
        lblGroup.setText("Группа: ИС-21");
        lblSpeciality.setText("Специальность: Информационные системы");
    }
}
