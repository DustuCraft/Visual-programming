package kz.atu.lab08;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Преобразование списка расходов в CSV (разделитель ';') и обратно. */
public final class ExpenseCsv {
    private static final String HEADER = "date;category;amount;note";

    private ExpenseCsv() { }

    public static String toCsv(List<Expense> list) {
        StringBuilder sb = new StringBuilder(HEADER).append('\n');
        for (Expense e : list) {
            sb.append(e.date()).append(';')
              .append(escape(e.category())).append(';')
              .append(String.format(Locale.ROOT, "%.2f", e.amount())).append(';')
              .append(escape(e.note())).append('\n');
        }
        return sb.toString();
    }

    public static List<Expense> parse(String text) {
        List<Expense> result = new ArrayList<>();
        String[] lines = text.split("\\R");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.isBlank()) continue;
            if (i == 0 && line.startsWith("date;")) continue; // заголовок
            List<String> f = splitLine(line);
            if (f.size() < 3) {
                throw new IllegalArgumentException("Строка " + (i + 1) + ": ожидается минимум 3 поля");
            }
            try {
                LocalDate date = LocalDate.parse(f.get(0).trim());
                double amount = Double.parseDouble(f.get(2).trim().replace(',', '.'));
                String note = f.size() > 3 ? f.get(3) : "";
                result.add(new Expense(date, f.get(1), amount, note));
            } catch (RuntimeException ex) {
                throw new IllegalArgumentException("Строка " + (i + 1) + ": неверный формат данных", ex);
            }
        }
        return result;
    }

    private static String escape(String s) {
        String v = s == null ? "" : s.replace('\n', ' ').replace('\r', ' ');
        if (v.contains(";") || v.contains("\"")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    private static List<String> splitLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                quoted = true;
            } else if (c == ';') {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields;
    }
}
