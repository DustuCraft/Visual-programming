package com.example.lab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.css.PseudoClass;
import javafx.scene.control.cell.PropertyValueFactory;

public class RegistrationController {
    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAge;
    @FXML private ComboBox<String> cmbSubject;
    @FXML private TextField txtCount;
    @FXML private TextField txtSum;
    @FXML private CheckBox chkConsent;
    @FXML private Label lblNameError;
    @FXML private Label lblEmailError;
    @FXML private Label lblAgeError;
    @FXML private Label lblSubjectError;
    @FXML private Label lblCountError;
    @FXML private Label lblSumError;
    @FXML private Label lblConsentError;
    @FXML private Label lblStatus;
    @FXML private TableView<Participant> tableParticipants;
    @FXML private TableColumn<Participant, String> colName;
    @FXML private TableColumn<Participant, String> colEmail;
    @FXML private TableColumn<Participant, Integer> colAge;
    @FXML private TableColumn<Participant, String> colSubject;
    @FXML private TableColumn<Participant, Integer> colCount;
    @FXML private TableColumn<Participant, Integer> colSum;
    @FXML private Button btnRegister;

    private final ObservableList<Participant> participants = FXCollections.observableArrayList();
    private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");

    @FXML
    private void initialize() {
        cmbSubject.setItems(FXCollections.observableArrayList(
                "Математика", "Информатика", "Физика", "Английский язык"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        colCount.setCellValueFactory(new PropertyValueFactory<>("examsCount"));
        colSum.setCellValueFactory(new PropertyValueFactory<>("sum"));
        tableParticipants.setItems(participants);

        txtAge.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().matches("\\d{0,3}") ? change : null));
        txtCount.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().matches("\\d{0,1}") ? change : null));
        txtSum.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().matches("\\d{0,7}") ? change : null));

        chkConsent.selectedProperty().addListener((obs, oldValue, newValue) ->
                btnRegister.setDisable(!newValue));
        btnRegister.setDisable(true);

        txtCount.textProperty().addListener((obs, oldValue, newValue) -> updateExpectedSum());
        txtName.focusedProperty().addListener((obs, oldValue, focused) -> { if (!focused) validateForm(false); });
        txtEmail.focusedProperty().addListener((obs, oldValue, focused) -> { if (!focused) validateForm(false); });
        txtAge.focusedProperty().addListener((obs, oldValue, focused) -> { if (!focused) validateForm(false); });
        txtSum.focusedProperty().addListener((obs, oldValue, focused) -> { if (!focused) validateForm(false); });
    }

    private void updateExpectedSum() {
        try {
            int count = Integer.parseInt(txtCount.getText());
            if (count >= 1 && count <= 5) {
                lblStatus.setText("Стоимость: " + RegistrationValidator.expectedSum(count) + " ₸");
            } else {
                lblStatus.setText("Стоимость одного экзамена — 5000 ₸");
            }
        } catch (RuntimeException e) {
            lblStatus.setText("Стоимость одного экзамена — 5000 ₸");
        }
    }

    @FXML
    private void onRegister() {
        ValidationResult result = validateForm(true);
        if (!result.isValid()) {
            new Alert(Alert.AlertType.WARNING, "Исправьте ошибки в форме").showAndWait();
            return;
        }

        try {
            Participant item = new Participant(
                    txtName.getText().trim(),
                    txtEmail.getText().trim(),
                    Integer.parseInt(txtAge.getText().trim()),
                    cmbSubject.getValue(),
                    Integer.parseInt(txtCount.getText().trim()),
                    Integer.parseInt(txtSum.getText().trim()));
            participants.add(item);
            lblStatus.setText("Регистрация успешно добавлена");
            clearForm();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Проверьте числовые поля").showAndWait();
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Не удалось завершить регистрацию").showAndWait();
        }
    }

    private ValidationResult validateForm(boolean showAll) {
        ValidationResult result = RegistrationValidator.validate(
                txtName.getText(), txtEmail.getText(), txtAge.getText(),
                cmbSubject.getValue(), txtCount.getText(), txtSum.getText(), chkConsent.isSelected());

        showError(lblNameError, result.error("name"));
        showError(lblEmailError, result.error("email"));
        showError(lblAgeError, result.error("age"));
        showError(lblSubjectError, result.error("subject"));
        showError(lblCountError, result.error("count"));
        showError(lblSumError, result.error("sum"));
        showError(lblConsentError, result.error("consent"));

        markInvalid(txtName, result.error("name"));
        markInvalid(txtEmail, result.error("email"));
        markInvalid(txtAge, result.error("age"));
        markInvalid(cmbSubject, result.error("subject"));
        markInvalid(txtCount, result.error("count"));
        markInvalid(txtSum, result.error("sum"));
        return result;
    }

    private void showError(Label label, String message) {
        label.setText(message);
    }

    private void markInvalid(Control control, String message) {
        control.pseudoClassStateChanged(INVALID, !message.isEmpty());
    }

    @FXML
    private void onClear() {
        clearForm();
        lblStatus.setText("Форма очищена");
    }

    private void clearForm() {
        txtName.clear();
        txtEmail.clear();
        txtAge.clear();
        cmbSubject.getSelectionModel().clearSelection();
        txtCount.clear();
        txtSum.clear();
        chkConsent.setSelected(false);
        for (Label label : new Label[]{lblNameError, lblEmailError, lblAgeError,
                lblSubjectError, lblCountError, lblSumError, lblConsentError}) {
            label.setText("");
        }
        for (Control control : new Control[]{txtName, txtEmail, txtAge, cmbSubject, txtCount, txtSum}) {
            control.pseudoClassStateChanged(INVALID, false);
        }
    }
}