package kz.atu.lab10;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NotesApplication extends Application {
    @Override public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(NotesApplication.class.getResource("notes-view.fxml"));
        Scene scene = new Scene(loader.load(), 900, 560);
        NotesController controller = loader.getController();
        stage.setOnCloseRequest(e -> { if (!controller.canClose()) e.consume(); });
        stage.setScene(scene);
        stage.setTitle("Каталог рецептов");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
