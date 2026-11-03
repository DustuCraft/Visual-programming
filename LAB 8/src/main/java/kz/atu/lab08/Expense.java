package kz.atu.lab08;

import java.time.LocalDate;

/** Одна запись о расходе. */
public record Expense(LocalDate date, String category, double amount, String note) {
}
