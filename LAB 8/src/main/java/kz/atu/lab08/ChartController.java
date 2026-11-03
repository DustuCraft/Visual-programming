package kz.atu.lab08;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Окно с диаграммами: расходы по категориям и по датам. */
public class ChartController {
    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;

    private ObservableList<Expense> items;
    private final ListChangeListener<Expense> listener = c -> refresh();

    public void setData(ObservableList<Expense> items) {
        this.items = items;
        items.addListener(listener);
        refresh();
    }

    public void dispose() {
        if (items != null) items.removeListener(listener);
    }

    private void refresh() {
        Map<String, Double> byCategory = items.stream().collect(
                Collectors.groupingBy(Expense::category, TreeMap::new,
                        Collectors.summingDouble(Expense::amount)));
        ObservableList<PieChart.Data> pie = FXCollections.observableArrayList();
        byCategory.forEach((cat, sum) -> pie.add(
                new PieChart.Data(String.format(Locale.ROOT, "%s (%.2f ₸)", cat, sum), sum)));
        pieChart.setData(pie);

        Map<LocalDate, Double> byDate = items.stream().collect(
                Collectors.groupingBy(Expense::date, TreeMap::new,
                        Collectors.summingDouble(Expense::amount)));
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        byDate.forEach((date, sum) -> series.getData().add(new XYChart.Data<>(date.toString(), sum)));
        barChart.getData().clear();
        barChart.getData().add(series);
    }
}
