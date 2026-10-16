package com.example.lab_7;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class HelloController {

    @FXML private TextArea txtEditor;
    @FXML private Label lblStatus;

    @FXML private MenuItem miNew;
    @FXML private MenuItem miOpen;
    @FXML private MenuItem miSave;
    @FXML private MenuItem miSaveAs;
    @FXML private MenuItem miCut;
    @FXML private MenuItem miCopy;
    @FXML private MenuItem miPaste;

    private Path currentFile;
    private boolean modified;
    /** true, пока текст меняется программно (Новый/Открыть) - это не правка пользователя */
    private boolean loading;

    @FXML
    public void initialize() {
        miNew.setAccelerator(KeyCombination.keyCombination("Shortcut+N"));
        miOpen.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
        miSave.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        miSaveAs.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+S"));
        miCut.setAccelerator(KeyCombination.keyCombination("Shortcut+X"));
        miCopy.setAccelerator(KeyCombination.keyCombination("Shortcut+C"));
        miPaste.setAccelerator(KeyCombination.keyCombination("Shortcut+V"));

        txtEditor.textProperty().addListener((obs, oldText, newText) -> {
            if (!loading) {
                modified = true;
                updateTitle();
            }
            lblStatus.setText("Символов: " + newText.length());
        });
    }

    // ---------- Команды (общие обработчики для меню, панели и контекстного меню) ----------

    @FXML
    private void onNew() {
        if (!confirmDiscardChanges()) return;
        setDocument("", null);
        lblStatus.setText("Новый документ");
    }

    @FXML private void onCopy() { txtEditor.copy(); }
    @FXML private void onCut() { txtEditor.cut(); }
    @FXML private void onPaste() { txtEditor.paste(); }

    @FXML
    private void onExit() {
        if (confirmDiscardChanges()) {
            Platform.exit();
        }
    }

    @FXML
    private void onOpen() {
        if (!confirmDiscardChanges()) return;
        File file = createChooser().showOpenDialog(getWindow());
        if (file == null) return; // отмена - не ошибка
        try {
            String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            setDocument(text, file.toPath());
            lblStatus.setText("Открыт: " + file.getName());
        } catch (IOException ex) {
            showError("Ошибка открытия: " + ex.getMessage());
        }
    }

    @FXML
    private void onSave() {
        saveDocument(false);
    }

    @FXML
    private void onSaveAs() {
        saveDocument(true);
    }

    @FXML
    private void onAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Редактор заметок. Лабораторная работа №7.", ButtonType.OK);
        a.setHeaderText("О программе");
        a.showAndWait();
    }

    // ---------- Вспомогательные методы ----------

    /**
     * @param forceChooser true - всегда показывать диалог (Сохранить как...)
     * @return true, если сохранение выполнено; false - отмена или ошибка
     */
    private boolean saveDocument(boolean forceChooser) {
        Path target = currentFile;
        if (target == null || forceChooser) {
            FileChooser chooser = createChooser();
            if (currentFile != null) {
                Path dir = currentFile.getParent();
                if (dir != null) chooser.setInitialDirectory(dir.toFile());
                chooser.setInitialFileName(currentFile.getFileName().toString());
            }
            File file = chooser.showSaveDialog(getWindow());
            if (file == null) return false; // отмена - не ошибка
            target = file.toPath();
        }
        try {
            Files.writeString(target, txtEditor.getText(), StandardCharsets.UTF_8);
            currentFile = target;
            modified = false;
            updateTitle();
            lblStatus.setText("Сохранено: " + currentFile.getFileName());
            return true;
        } catch (IOException ex) {
            showError("Ошибка сохранения: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Спрашивает о несохранённых изменениях.
     * @return true - можно продолжать (сохранили / отказались сохранять / изменений нет)
     */
    public boolean confirmDiscardChanges() {
        if (!modified) return true;
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.YES);
        ButtonType dontSave = new ButtonType("Не сохранять", ButtonBar.ButtonData.NO);
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "В документе есть несохранённые изменения. Сохранить их?", save, dontSave, cancel);
        a.setHeaderText("Несохранённые изменения");
        Optional<ButtonType> result = a.showAndWait();
        if (result.isEmpty() || result.get() == cancel) return false;
        if (result.get() == save) return saveDocument(false);
        return true;
    }

    private void setDocument(String text, Path file) {
        loading = true;
        try {
            txtEditor.setText(text);
        } finally {
            loading = false;
        }
        currentFile = file;
        modified = false;
        updateTitle();
    }

    private FileChooser createChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        return chooser;
    }

    private Window getWindow() {
        return txtEditor.getScene().getWindow();
    }

    private void updateTitle() {
        if (txtEditor.getScene() == null) return;
        Stage stage = (Stage) txtEditor.getScene().getWindow();
        String name = currentFile == null ? "Без имени" : currentFile.getFileName().toString();
        stage.setTitle((modified ? "*" : "") + name + " - Редактор заметок");
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        a.setHeaderText("Ошибка");
        a.showAndWait();
    }
}