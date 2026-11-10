package kz.atu.lab11.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kz.atu.lab11.model.Vacancy;
import kz.atu.lab11.service.VacancyService;

import java.util.Locale;

/** Контроллер: только реакция на события интерфейса. Бизнес-правила — в VacancyService. */
public class VacancyController {

    private static final String ALL = "Все";

    @FXML private TableView<Vacancy> tableVacancies;
    @FXML private TableColumn<Vacancy, String> colTitle, colCompany, colCategory;
    @FXML private TableColumn<Vacancy, Number> colSalary;
    @FXML private TextField txtTitle, txtCompany, txtCategory, txtSalary, txtSearch;
    @FXML private ComboBox<String> cmbCategoryFilter;
    @FXML private Label lblCount, lblAverage;

    private final VacancyService service = new VacancyService();
    private FilteredList<Vacancy> filtered;

    @FXML
    private void initialize() {
        colTitle.setCellValueFactory(c -> c.getValue().titleProperty());
        colCompany.setCellValueFactory(c -> c.getValue().companyProperty());
        colCategory.setCellValueFactory(c -> c.getValue().categoryProperty());
        colSalary.setCellValueFactory(c -> c.getValue().salaryProperty());
        colSalary.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("%,.0f", value.doubleValue()));
            }
        });

        // тестовые данные
        service.addVacancy("Java-разработчик", "Kaspi.kz", "IT", 900_000);
        service.addVacancy("Бухгалтер", "Halyk Bank", "Финансы", 350_000);
        service.addVacancy("Аналитик данных", "Beeline", "IT", 700_000);

        filtered = new FilteredList<>(service.getVacancies(), v -> true);
        SortedList<Vacancy> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableVacancies.comparatorProperty());
        tableVacancies.setItems(sorted);

        refreshCategories();

        txtSearch.textProperty().addListener((o, a, b) -> applyFilter());
        cmbCategoryFilter.valueProperty().addListener((o, a, b) -> applyFilter());
        // счётчик и среднее обновляются при любом изменении видимого списка
        filtered.addListener((ListChangeListener<Vacancy>) c -> updateStats());

        // выбор строки заполняет поля для редактирования
        tableVacancies.getSelectionModel().selectedItemProperty().addListener((o, old, v) -> {
            if (v != null) {
                txtTitle.setText(v.getTitle());
                txtCompany.setText(v.getCompany());
                txtCategory.setText(v.getCategory());
                txtSalary.setText(formatSalary(v.getSalary()));
            }
        });

        updateStats();
    }

    // ---------- CRUD ----------

    @FXML
    private void onAdd() {
        try {
            service.addVacancy(txtTitle.getText(), txtCompany.getText(),
                    txtCategory.getText(), readSalary());
            refreshCategories();
            applyFilter();
            clearFields();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        try {
            Vacancy v = tableVacancies.getSelectionModel().getSelectedItem();
            service.updateVacancy(v, txtTitle.getText(), txtCompany.getText(),
                    txtCategory.getText(), readSalary());
            refreshCategories();
            applyFilter();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        try {
            Vacancy v = tableVacancies.getSelectionModel().getSelectedItem();
            if (v != null && !confirmDelete(v)) return;
            service.deleteVacancy(v);
            refreshCategories();
            applyFilter();
            clearFields();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // ---------- фильтрация ----------

    private void applyFilter() {
        String q = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        String category = cmbCategoryFilter.getValue();
        filtered.setPredicate(v ->
                v.getTitle().toLowerCase(Locale.ROOT).contains(q)
                        && (category == null || ALL.equals(category) || v.getCategory().equals(category)));
        updateStats();
    }

    /** Динамический список сфер: пересобирается из данных, выбранное значение сохраняется. */
    private void refreshCategories() {
        String selected = cmbCategoryFilter.getValue();
        cmbCategoryFilter.getItems().setAll(ALL);
        cmbCategoryFilter.getItems().addAll(service.getCategories());
        cmbCategoryFilter.setValue(
                selected != null && cmbCategoryFilter.getItems().contains(selected) ? selected : ALL);
    }

    private void updateStats() {
        lblCount.setText("Найдено вакансий: " + filtered.size());
        lblAverage.setText(String.format("Средняя зарплата: %,.0f ₸", service.averageSalary(filtered)));
    }

    // ---------- вспомогательные методы ----------

    private double readSalary() {
        try {
            String text = txtSalary.getText().trim().replace(" ", "").replace(',', '.');
            return Double.parseDouble(text);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Введите числовую зарплату");
        }
    }

    private String formatSalary(double s) {
        return s == Math.rint(s) ? String.valueOf((long) s) : String.valueOf(s);
    }

    private void clearFields() {
        txtTitle.clear();
        txtCompany.clear();
        txtCategory.clear();
        txtSalary.clear();
        tableVacancies.getSelectionModel().clearSelection();
    }

    private boolean confirmDelete(Vacancy v) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Подтверждение");
        a.setHeaderText(null);
        a.setContentText("Удалить вакансию «" + v.getTitle() + "» (" + v.getCompany() + ")?");
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    private void showError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка");
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
