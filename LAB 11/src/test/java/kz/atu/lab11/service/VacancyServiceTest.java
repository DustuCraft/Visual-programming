package kz.atu.lab11.service;

import kz.atu.lab11.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VacancyServiceTest {

    private VacancyService service;

    @BeforeEach
    void setUp() {
        service = new VacancyService();
    }

    @Test
    void shouldAddVacancy() {
        service.addVacancy("Java-разработчик", "Kaspi.kz", "IT", 900_000);
        assertEquals(1, service.getVacancies().size());
    }

    @Test
    void shouldTrimFieldsOnAdd() {
        service.addVacancy("  Тестировщик ", " Beeline ", " IT ", 500_000);
        Vacancy v = service.getVacancies().get(0);
        assertEquals("Тестировщик", v.getTitle());
        assertEquals("Beeline", v.getCompany());
        assertEquals("IT", v.getCategory());
    }

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addVacancy("  ", "Kaspi.kz", "IT", 100_000));
    }

    @Test
    void shouldRejectNegativeSalary() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addVacancy("Дизайнер", "Kaspi.kz", "IT", -1));
    }

    @Test
    void shouldRejectTooLargeSalary() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addVacancy("Дизайнер", "Kaspi.kz", "IT", 20_000_000));
    }

    @Test
    void shouldRejectNaNSalary() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addVacancy("Дизайнер", "Kaspi.kz", "IT", Double.NaN));
    }

    @Test
    void shouldRejectDuplicateIgnoringCase() {
        service.addVacancy("Бухгалтер", "Halyk Bank", "Финансы", 350_000);
        assertThrows(IllegalArgumentException.class,
                () -> service.addVacancy("бухгалтер", "HALYK BANK", "Финансы", 400_000));
        assertEquals(1, service.getVacancies().size());
    }

    @Test
    void shouldAllowSameTitleInAnotherCompany() {
        service.addVacancy("Бухгалтер", "Halyk Bank", "Финансы", 350_000);
        service.addVacancy("Бухгалтер", "Kaspi.kz", "Финансы", 380_000);
        assertEquals(2, service.getVacancies().size());
    }

    @Test
    void shouldUpdateVacancy() {
        service.addVacancy("Аналитик", "Beeline", "IT", 600_000);
        Vacancy v = service.getVacancies().get(0);
        service.updateVacancy(v, "Старший аналитик", "Beeline", "IT", 800_000);
        assertEquals("Старший аналитик", v.getTitle());
        assertEquals(800_000, v.getSalary());
    }

    @Test
    void shouldAllowUpdateWithoutChangingKey() {
        service.addVacancy("Аналитик", "Beeline", "IT", 600_000);
        Vacancy v = service.getVacancies().get(0);
        assertDoesNotThrow(() -> service.updateVacancy(v, "Аналитик", "Beeline", "IT", 650_000));
    }

    @Test
    void shouldRejectUpdateCreatingDuplicate() {
        service.addVacancy("Аналитик", "Beeline", "IT", 600_000);
        service.addVacancy("Тестировщик", "Beeline", "IT", 500_000);
        Vacancy second = service.getVacancies().get(1);
        assertThrows(IllegalArgumentException.class,
                () -> service.updateVacancy(second, "Аналитик", "Beeline", "IT", 500_000));
    }

    @Test
    void shouldRejectUpdateWithoutSelection() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateVacancy(null, "A", "B", "C", 1));
    }

    @Test
    void shouldDeleteVacancy() {
        service.addVacancy("Аналитик", "Beeline", "IT", 600_000);
        service.deleteVacancy(service.getVacancies().get(0));
        assertTrue(service.getVacancies().isEmpty());
    }

    @Test
    void shouldRejectDeleteWithoutSelection() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteVacancy(null));
    }

    @Test
    void shouldCalculateAverageSalary() {
        service.addVacancy("A", "X", "IT", 400_000);
        service.addVacancy("B", "Y", "IT", 600_000);
        assertEquals(500_000, service.averageSalary(), 0.001);
    }

    @Test
    void averageSalaryOfEmptyListIsZero() {
        assertEquals(0, service.averageSalary(), 0.001);
    }

    @Test
    void shouldBuildSortedUniqueCategories() {
        service.addVacancy("A", "X", "Финансы", 1);
        service.addVacancy("B", "Y", "IT", 1);
        service.addVacancy("C", "Z", "IT", 1);
        assertEquals(List.of("IT", "Финансы"), service.getCategories());
    }
}
