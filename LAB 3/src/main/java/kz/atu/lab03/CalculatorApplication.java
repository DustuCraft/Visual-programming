package kz.atu.lab03;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CalculatorApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                CalculatorApplication.class.getResource("calculator-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 420, 520);
        stage.setTitle("Интерактивный калькулятор");


        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
