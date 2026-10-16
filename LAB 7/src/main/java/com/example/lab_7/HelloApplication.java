package com.example.lab_7;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        HelloController controller = loader.getController();

        // Закрытие окна крестиком проходит через ту же проверку несохранённых изменений
        stage.setOnCloseRequest(event -> {
            if (!controller.confirmDiscardChanges()) {
                event.consume();
            }
        });

        stage.setTitle("Редактор заметок");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}