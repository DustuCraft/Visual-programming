package kz.atu.lab08;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

/** Контроллер окна добавления / редактирования записи. */
public class ExpenseDialogController {
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> cbCategory;
    @FXML private TextField txtAmount;
    @FXML private TextField txtNote;
    @FXML private Label lblError;

    private ObservableList<String> categories = FXCollections.observableArrayList();
    private Expense result;

    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
    }

    /** Общий список категорий из главного окна (новые категории добавляются в него). */
    public void setCategories(ObservableList<String> categories) {
        this.categories = categories;
        cbCategory.setItems(categories);
    }

    /** Передача редактируемой записи (null — создание новой). */
    public void setExpense(Expense e) {
        if (e == null) return;
        datePicker.setValue(e.date());
        cbCategory.setValue(e.category());
        txtAmount.setText(String.valueOf(e.amount()));
        txtNote.setText(e.note());
    }

    public Optional<Expense> getResult() {
        return Optional.ofNullable(result);
    }

    @FXML
    private void onSave() {
        LocalDate date = datePicker.getValue();
        String category = cbCategory.getEditor().getText();
        category = category == null ? "" : category.trim();

        if (date == null) { lblError.setText("Укажите дату"); return; }
        if (category.isEmpty()) { lblError.setText("Выберите или введите категорию"); return; }
        double amount;
        try {
            amount = Double.parseDouble(txtAmount.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            lblError.setText("Сумма должна быть числом");
            return;
        }
        if (!Double.isFinite(amount) || amount <= 0) {
            lblError.setText("Сумма должна быть больше нуля");
            return;
        }

        boolean found = false;
        for (String c : categories) {
            if (c.equalsIgnoreCase(category)) {
                category = c;
                found = true;
                break;
            }
        }
        if (!found) categories.add(category);

        String note = txtNote.getText() == null ? "" : txtNote.getText().trim();
        result = new Expense(date, category, amount, note);
        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        ((Stage) txtAmount.getScene().getWindow()).close();
    }
}
