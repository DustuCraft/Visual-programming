package kz.atu.lab11.model;

import javafx.beans.property.*;

/**
 * Модель данных: вакансия.
 * Поля хранятся в JavaFX Properties, чтобы TableView
 * автоматически отслеживал их изменения.
 */
public class Vacancy {
    private final StringProperty title = new SimpleStringProperty();     // должность
    private final StringProperty company = new SimpleStringProperty();   // компания
    private final StringProperty category = new SimpleStringProperty();  // сфера (аналог «группы»)
    private final DoubleProperty salary = new SimpleDoubleProperty();    // зарплата, ₸ (аналог GPA)

    public Vacancy(String t, String c, String cat, double s) {
        setTitle(t);
        setCompany(c);
        setCategory(cat);
        setSalary(s);
    }

    public String getTitle() { return title.get(); }
    public void setTitle(String v) { title.set(v); }
    public StringProperty titleProperty() { return title; }

    public String getCompany() { return company.get(); }
    public void setCompany(String v) { company.set(v); }
    public StringProperty companyProperty() { return company; }

    public String getCategory() { return category.get(); }
    public void setCategory(String v) { category.set(v); }
    public StringProperty categoryProperty() { return category; }

    public double getSalary() { return salary.get(); }
    public void setSalary(double v) { salary.set(v); }
    public DoubleProperty salaryProperty() { return salary; }
}
