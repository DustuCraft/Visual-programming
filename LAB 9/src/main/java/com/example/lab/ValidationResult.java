package com.example.lab;

import java.util.Map;

public record ValidationResult(Map<String, String> errors) {
    public boolean isValid() {
        return errors.isEmpty();
    }

    public String error(String key) {
        return errors.getOrDefault(key, "");
    }
}