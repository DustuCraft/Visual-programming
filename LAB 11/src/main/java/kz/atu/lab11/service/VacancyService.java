package kz.atu.lab11.service;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kz.atu.lab11.model.Vacancy;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный слой: вся бизнес-логика каталога вакансий.
 * Не зависит от графического интерфейса, поэтому тестируется без запуска JavaFX.
 */
public class VacancyService {

    public static final double MAX_SALARY = 10_000_000;

    // extractor: изменение любого поля вакансии порождает событие "update" списка,
    // поэтому FilteredList/SortedList автоматически пересчитываются после редактирования
    private final ObservableList<Vacancy> vacancies = FXCollections.observableArrayList(
            v -> new Observable[]{
                    v.titleProperty(), v.companyProperty(),
                    v.categoryProperty(), v.salaryProperty()});

    public ObservableList<Vacancy> getVacancies() {
        return vacancies;
    }

    public void addVacancy(String title, String company, String category, double salary) {
        validate(title, company, category, salary);
        checkDuplicate(null, title, company);
        vacancies.add(new Vacancy(title.trim(), company.trim(), category.trim(), salary));
    }

    public void updateVacancy(Vacancy v, String title, String company, String category, double salary) {
        if (v == null) throw new IllegalArgumentException("Вакансия не выбрана");
        validate(title, company, category, salary);
        checkDuplicate(v, title, company);
        v.setTitle(title.trim());
        v.setCompany(company.trim());
        v.setCategory(category.trim());
        v.setSalary(salary);
    }

    public void deleteVacancy(Vacancy v) {
        if (v == null) throw new IllegalArgumentException("Вакансия не выбрана");
        vacancies.remove(v);
    }

    /** Список сфер, сформированный из данных (без повторов, по алфавиту). */
    public List<String> getCategories() {
        return vacancies.stream()
                .map(Vacancy::getCategory)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    /** Средняя зарплата по всем вакансиям (0, если список пуст). */
    public double averageSalary() {
        return averageSalary(vacancies);
    }

    /** Средняя зарплата по переданной коллекции (например, по отфильтрованным). */
    public double averageSalary(Collection<Vacancy> items) {
        return items.stream().mapToDouble(Vacancy::getSalary).average().orElse(0);
    }

    private void checkDuplicate(Vacancy self, String title, String company) {
        String t = title.trim();
        String c = company.trim();
        boolean exists = vacancies.stream().anyMatch(v ->
                v != self
                        && v.getTitle().equalsIgnoreCase(t)
                        && v.getCompany().equalsIgnoreCase(c));
        if (exists) {
            throw new IllegalArgumentException("Такая вакансия в этой компании уже есть");
        }
    }

    private void validate(String title, String company, String category, double salary) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Введите должность");
        if (company == null || company.isBlank())
            throw new IllegalArgumentException("Введите компанию");
        if (category == null || category.isBlank())
            throw new IllegalArgumentException("Введите сферу");
        if (!Double.isFinite(salary) || salary < 0 || salary > MAX_SALARY)
            throw new IllegalArgumentException("Зарплата должна быть от 0 до 10 000 000");
    }
}
