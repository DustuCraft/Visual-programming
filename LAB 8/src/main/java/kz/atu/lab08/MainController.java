package kz.atu.lab08;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MainController {
    @FXML private TableView<Expense> table;
    @FXML private TableColumn<Expense, String> colDate;
    @FXML private TableColumn<Expense, String> colCategory;
    @FXML private TableColumn<Expense, String> colAmount;
    @FXML private TableColumn<Expense, String> colNote;
    @FXML private Label lblStatus;
    @FXML private Label lblTotal;

    private final ObservableList<Expense> items = FXCollections.observableArrayList();
    private final ObservableList<String> categories = FXCollections.observableArrayList(
            "Питание", "Транспорт", "Жильё", "Учёба", "Развлечения", "Здоровье", "Прочее");
    private Color currentColor = Color.BLACK;
    private File currentFile;
    private boolean dirty;
    private Stage chartStage;

    @FXML
    private void initialize() {
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date().toString()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().category()));
        colAmount.setCellValueFactory(c ->
                new SimpleStringProperty(String.format(Locale.ROOT, "%.2f", c.getValue().amount())));
        colAmount.setStyle("-fx-alignment: CENTER-RIGHT;");
        colNote.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().note()));

        table.setItems(items);
        items.addListener((ListChangeListener<Expense>) c -> updateTotal());
        table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) onEdit();
        });
        updateTotal();

        table.sceneProperty().addListener((o, oldScene, scene) -> {
            if (scene != null) {
                scene.windowProperty().addListener((o2, oldW, w) -> {
                    if (w != null) {
                        w.setOnCloseRequest(ev -> {
                            if (!confirmDiscard()) ev.consume();
                        });
                    }
                });
            }
        });
    }

    /** Загрузка FXML с понятным сообщением, если файл не найден. */
    private FXMLLoader loaderFor(String name) throws IOException {
        var url = MainController.class.getResource("/kz/atu/lab08/" + name);
        if (url == null) {
            throw new IOException("Не найден ресурс /kz/atu/lab08/" + name
                    + " (проверьте папку src/main/resources/kz/atu/lab08/)");
        }
        return new FXMLLoader(url);
    }

    // ---------- Окно графиков (немодальное) ----------

    @FXML
    private void onShowChart() {
        try {
            if (chartStage != null && chartStage.isShowing()) {
                chartStage.toFront();
                return;
            }
            FXMLLoader loader = loaderFor("chart-view.fxml");
            Parent root = loader.load();
            ChartController controller = loader.getController();
            controller.setData(items);

            chartStage = new Stage();
            chartStage.initOwner(window());
            chartStage.initModality(Modality.NONE);
            chartStage.setTitle("Диаграммы расходов");
            chartStage.setScene(new Scene(root));
            chartStage.setOnHidden(e -> controller.dispose());
            chartStage.show();
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    // ---------- Модальное окно записи ----------

    @FXML
    private void onAdd() {
        try {
            showExpenseDialog("Новая запись", null).ifPresent(e -> {
                items.add(e);
                markDirty("Запись добавлена");
            });
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        int i = table.getSelectionModel().getSelectedIndex();
        if (i < 0) {
            lblStatus.setText("Выберите запись для редактирования");
            return;
        }
        try {
            showExpenseDialog("Редактирование записи", items.get(i)).ifPresent(e -> {
                items.set(i, e);
                markDirty("Запись изменена");
            });
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        int i = table.getSelectionModel().getSelectedIndex();
        if (i < 0) {
            lblStatus.setText("Выберите запись для удаления");
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Удалить выбранную запись?",
                ButtonType.OK, ButtonType.CANCEL);
        a.initOwner(window());
        a.setHeaderText(null);
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            items.remove(i);
            markDirty("Запись удалена");
        }
    }

    private Optional<Expense> showExpenseDialog(String title, Expense initial) throws IOException {
        FXMLLoader loader = loaderFor("expense-dialog.fxml");
        Parent root = loader.load();
        ExpenseDialogController controller = loader.getController();
        controller.setCategories(categories);
        controller.setExpense(initial);

        Stage dialog = new Stage();
        dialog.initOwner(window());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle(title);
        dialog.setResizable(false);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
        return controller.getResult();
    }

    // ---------- Модальное окно выбора цвета ----------

    @FXML
    private void onChooseColor() {
        try {
            FXMLLoader loader = loaderFor("color-dialog.fxml");
            Parent root = loader.load();
            ColorDialogController controller = loader.getController();
            controller.setInitialColor(currentColor);

            Stage dialog = new Stage();
            dialog.initOwner(window());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setTitle("Цвет текста");
            dialog.setResizable(false);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            controller.getSelectedColor().ifPresent(color -> {
                currentColor = color;
                String css = toCss(color);
                table.setStyle("-fx-text-background-color: " + css + ";");
                lblTotal.setStyle("-fx-text-fill: " + css + ";");
                lblStatus.setText("Цвет текста изменён");
            });
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    private String toCss(Color c) {
        return String.format("#%02X%02X%02X",
                Math.round(c.getRed() * 255), Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255));
    }

    // ---------- Файлы ----------

    @FXML
    private void onOpen() {
        if (!confirmDiscard()) return;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Открыть файл расходов");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-файлы", "*.csv"));
        File file = chooser.showOpenDialog(window());
        if (file == null) return;
        try {
            String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            List<Expense> loaded = ExpenseCsv.parse(text);
            items.setAll(loaded);
            for (Expense e : loaded) {
                if (!categories.contains(e.category())) categories.add(e.category());
            }
            currentFile = file;
            dirty = false;
            lblStatus.setText("Открыт: " + file.getName());
        } catch (IOException | RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onSave() {
        if (currentFile == null) {
            onSaveAs();
        } else {
            writeFile(currentFile);
        }
    }

    @FXML
    private void onSaveAs() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранить файл расходов");
        chooser.setInitialFileName("expenses.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-файлы", "*.csv"));
        File file = chooser.showSaveDialog(window());
        if (file == null) return;
        writeFile(file);
    }

    private void writeFile(File file) {
        try {
            Files.writeString(file.toPath(), ExpenseCsv.toCsv(items), StandardCharsets.UTF_8);
            currentFile = file;
            dirty = false;
            lblStatus.setText("Сохранён: " + file.getName());
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    // ---------- Прочее ----------

    @FXML
    private void onExit() {
        if (confirmDiscard()) Platform.exit();
    }

    @FXML
    private void onAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.initOwner(window());
        a.setTitle("О программе");
        a.setHeaderText("Учёт расходов");
        a.setContentText("Лабораторная работа №8. JavaFX, FXML, многооконное приложение.");
        a.showAndWait();
    }

    private boolean confirmDiscard() {
        if (!dirty) return true;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Есть несохранённые изменения. Продолжить без сохранения?",
                ButtonType.OK, ButtonType.CANCEL);
        a.initOwner(window());
        a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void markDirty(String message) {
        dirty = true;
        lblStatus.setText(message);
    }

    private void updateTotal() {
        double sum = items.stream().mapToDouble(Expense::amount).sum();
        lblTotal.setText(String.format(Locale.ROOT, "Итого: %.2f ₸", sum));
    }

    private Window window() {
        return table.getScene().getWindow();
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.initOwner(window());
        a.setTitle("Ошибка ввода-вывода");
        a.setHeaderText("Не удалось выполнить операцию");
        a.setContentText(message);
        a.showAndWait();
    }
}
