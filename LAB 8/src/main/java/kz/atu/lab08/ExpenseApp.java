package kz.atu.lab08;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ExpenseApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var url = ExpenseApp.class.getResource("/kz/atu/lab08/main-view.fxml");
        if (url == null) {
            throw new IOException("Не найден /kz/atu/lab08/main-view.fxml. "
                    + "Файлы .fxml должны лежать в src/main/resources/kz/atu/lab08/");
        }
        FXMLLoader loader = new FXMLLoader(url);
        stage.setScene(new Scene(loader.load(), 820, 540));
        stage.setTitle("Учёт расходов");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
