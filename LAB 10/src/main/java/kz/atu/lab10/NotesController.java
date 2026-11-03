package kz.atu.lab10;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NotesController {
    private static final String ALL = "Все";
    private static final List<String> PRIORITIES = List.of("Высокий", "Средний", "Низкий");

    @FXML private ListView<Note> listNotes;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbFilter;
    @FXML private TextField txtTitle;
    @FXML private ComboBox<String> cbPriority;
    @FXML private TextArea txtBody;
    @FXML private Label lblStatus;

    private final ObservableList<Note> notes = FXCollections.observableArrayList();
    private FilteredList<Note> filtered;
    private final NoteStorage storage = new NoteStorage();
    private boolean dirty = false;

    @FXML public void initialize() {
        filtered = new FilteredList<>(notes, n -> true);
        listNotes.setItems(filtered);

        cbPriority.setItems(FXCollections.observableArrayList(PRIORITIES));
        cbPriority.setValue("Средний");

        ObservableList<String> filterItems = FXCollections.observableArrayList(ALL);
        filterItems.addAll(PRIORITIES);
        cbFilter.setItems(filterItems);
        cbFilter.setValue(ALL);

        txtSearch.textProperty().addListener((o, a, b) -> applyFilter());
        cbFilter.valueProperty().addListener((o, a, b) -> applyFilter());

        notes.addListener((ListChangeListener<Note>) c -> dirty = true);

        listNotes.getSelectionModel().selectedItemProperty().addListener((obs, old, note) -> {
            if (note != null) {
                txtTitle.setText(note.getTitle());
                txtBody.setText(note.getBody());
                cbPriority.setValue(note.getPriority());
            }
        });
        lblStatus.setText("Готово");
    }

    private void applyFilter() {
        String q = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        String p = cbFilter.getValue();
        filtered.setPredicate(n ->
                (q.isEmpty() || n.getTitle().toLowerCase().contains(q))
                && (p == null || ALL.equals(p) || p.equals(n.getPriority())));
    }

    /** Возвращает заполненную заметку или null, если поля не прошли проверку. */
    private Note readForm() {
        String title = txtTitle.getText() == null ? "" : txtTitle.getText().trim();
        String body = txtBody.getText() == null ? "" : txtBody.getText().trim();
        String priority = cbPriority.getValue();
        if (title.isBlank() || body.isBlank()) {
            error("Введите название и текст рецепта.");
            return null;
        }
        if (priority == null) {
            error("Выберите приоритет.");
            return null;
        }
        return new Note(title, body, priority);
    }

    @FXML private void onAdd() {
        Note note = readForm();
        if (note == null) return;
        notes.add(note);
        listNotes.getSelectionModel().select(note);
        lblStatus.setText("Добавлен рецепт: " + note.getTitle());
    }

    @FXML private void onEdit() {
        Note selected = listNotes.getSelectionModel().getSelectedItem();
        if (selected == null) { error("Выберите рецепт для изменения."); return; }
        Note data = readForm();
        if (data == null) return;
        selected.setTitle(data.getTitle());
        selected.setBody(data.getBody());
        selected.setPriority(data.getPriority());
        dirty = true;
        listNotes.refresh();
        applyFilter();
        lblStatus.setText("Рецепт обновлён: " + selected.getTitle());
    }

    @FXML private void onDelete() {
        Note selected = listNotes.getSelectionModel().getSelectedItem();
        if (selected == null) { error("Выберите рецепт."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить выбранный рецепт?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            notes.remove(selected);
            txtTitle.clear();
            txtBody.clear();
            lblStatus.setText("Рецепт удалён");
        }
    }

    @FXML private void onSave() {
        FileChooser chooser = chooser();
        chooser.setTitle("Сохранить рецепты");
        chooser.setInitialFileName("recipes.json");
        File file = chooser.showSaveDialog(listNotes.getScene().getWindow());
        if (file == null) return;                       // отмена - не ошибка
        try {
            storage.save(file.toPath(), notes);
            dirty = false;
            lblStatus.setText("Сохранено: " + file.getName());
        } catch (IOException ex) {
            error("Не удалось сохранить файл.");
        }
    }

    @FXML private void onOpen() {
        if (!confirmDiscard()) return;
        FileChooser chooser = chooser();
        chooser.setTitle("Открыть рецепты");
        File file = chooser.showOpenDialog(listNotes.getScene().getWindow());
        if (file == null) return;
        try {
            List<Note> loaded = storage.load(file.toPath());   // сначала читаем...
            notes.setAll(loaded);                              // ...и только потом заменяем данные
            dirty = false;
            lblStatus.setText("Загружено рецептов: " + notes.size());
        } catch (IOException ex) {
            error("Не удалось прочитать JSON. Текущие данные сохранены.");
        }
    }

    /** Для вызова из NotesApplication при закрытии окна. */
    public boolean canClose() { return confirmDiscard(); }

    private boolean confirmDiscard() {
        if (!dirty) return true;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Есть несохранённые изменения. Продолжить без сохранения?",
                ButtonType.YES, ButtonType.NO);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private FileChooser chooser() {
        FileChooser c = new FileChooser();
        c.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));
        return c;
    }

    private void error(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }
}
