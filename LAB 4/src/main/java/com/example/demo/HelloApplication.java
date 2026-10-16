package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 700);
        scene.getStylesheets().add(HelloApplication.class.getResource("style.css").toExternalForm());

        primaryStage.setTitle("Панель мониторинга оборудования");
        primaryStage.setScene(scene);
        // Минимальные размеры окна, чтобы компоненты не перекрывались при ресайзе
        primaryStage.setMinWidth(820);
        primaryStage.setMinHeight(520);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}