package com.example.lab;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RegistrationValidator {
    private static final int PRICE_PER_EXAM = 5000;

    private RegistrationValidator() {}

    public static ValidationResult validate(String name, String email, String ageText,
                                            String subject, String countText, String sumText,
                                            boolean consent) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (name == null || name.trim().length() < 2 || name.trim().length() > 80) {
            errors.put("name", "ФИО должно содержать от 2 до 80 символов");
        }

        if (email == null || !email.trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.put("email", "Введите корректный email");
        }

        try {
            int age = Integer.parseInt(ageText.trim());
            if (age < 16 || age > 100) errors.put("age", "Возраст должен быть от 16 до 100");
        } catch (RuntimeException e) {
            errors.put("age", "Введите возраст целым числом");
        }

        if (subject == null || subject.isBlank()) {
            errors.put("subject", "Выберите предмет экзамена");
        }

        int count = 0;
        try {
            count = Integer.parseInt(countText.trim());
            if (count < 1 || count > 5) errors.put("count", "Количество экзаменов: от 1 до 5");
        } catch (RuntimeException e) {
            errors.put("count", "Введите целое количество экзаменов");
        }

        try {
            int sum = Integer.parseInt(sumText.trim());
            if (count >= 1 && count <= 5 && sum != count * PRICE_PER_EXAM) {
                errors.put("sum", "Неверная сумма. Нужно: " + (count * PRICE_PER_EXAM) + " ₸");
            } else if (sum <= 0) {
                errors.put("sum", "Сумма должна быть больше нуля");
            }
        } catch (RuntimeException e) {
            errors.put("sum", "Введите сумму целым числом");
        }

        if (!consent) errors.put("consent", "Подтвердите согласие");

        return new ValidationResult(errors);
    }

    public static int expectedSum(int count) {
        return count * PRICE_PER_EXAM;
    }
}