package com.example.lab6;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class HelloController {
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilterGenre;
    @FXML private TableView<Game> tableGames;
    @FXML private TableColumn<Game, String> colName;
    @FXML private TableColumn<Game, String> colGenre;
    @FXML private TableColumn<Game, String> colPlatform;
    @FXML private TableColumn<Game, Double> colRating;
    @FXML private TextField txtName;
    @FXML private ComboBox<String> cmbGenre;
    @FXML private ComboBox<String> cmbPlatform;
    @FXML private TextField txtRating;
    @FXML private Label lblCount;

    private final ObservableList<Game> games = FXCollections.observableArrayList();
    private FilteredList<Game> filteredGames;

    @FXML
    public void initialize() {
        games.addAll(
                new Game("The Witcher 3", "RPG", "PC", 9.7),
                new Game("Minecraft", "Песочница", "PC", 9.0),
                new Game("Mario Kart 8", "Гонки", "Nintendo Switch", 9.2)
        );

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colPlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

        cmbGenre.getItems().addAll("RPG", "Спорт", "Песочница", "Гонки", "Шутер");
        cmbPlatform.getItems().addAll("PC", "PlayStation", "Xbox", "Nintendo Switch");
        cmbFilterGenre.getItems().addAll("Все", "RPG", "Спорт", "Песочница", "Гонки", "Шутер");
        cmbFilterGenre.setValue("Все");

        filteredGames = new FilteredList<>(games, g -> true);
        tableGames.setItems(filteredGames);

        txtSearch.textProperty().addListener((obs, o, n) -> applyFilter());
        cmbFilterGenre.setOnAction(e -> applyFilter());

        updateCount();
    }

    @FXML
    private void onAddClick() {
        String name = txtName.getText().trim();
        String genre = cmbGenre.getValue();
        String platform = cmbPlatform.getValue();
        String ratingText = txtRating.getText().trim().replace(',', '.');

        if (name.isBlank() || genre == null || platform == null || ratingText.isBlank()) {
            showError("Заполните все поля.");
            return;
        }
        try {
            double rating = Double.parseDouble(ratingText);
            if (rating < 0 || rating > 10) {
                showError("Рейтинг должен быть от 0 до 10.");
                return;
            }
            games.add(new Game(name, genre, platform, rating));
            clearInput();
            applyFilter();
        } catch (NumberFormatException e) {
            showError("Рейтинг должен быть числом.");
        }
    }

    @FXML
    private void onDeleteClick() {
        Game selected = tableGames.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите игру для удаления.");
            return;
        }
        games.remove(selected);
        applyFilter();
    }

    @FXML
    private void onClearFilterClick() {
        txtSearch.clear();
        cmbFilterGenre.setValue("Все");
        applyFilter();
    }

    private void applyFilter() {
        String search = txtSearch.getText().trim().toLowerCase();
        String genre = cmbFilterGenre.getValue();

        filteredGames.setPredicate(game -> {
            boolean matchesSearch = game.getName().toLowerCase().contains(search);
            boolean matchesGenre = genre == null
                    || genre.equals("Все")
                    || game.getGenre().equals(genre);
            return matchesSearch && matchesGenre;
        });
        updateCount();
    }

    private void updateCount() {
        lblCount.setText("Найдено записей: " + tableGames.getItems().size());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearInput() {
        txtName.clear();
        txtRating.clear();
        cmbGenre.getSelectionModel().clearSelection();
        cmbPlatform.getSelectionModel().clearSelection();
        txtName.requestFocus();
    }
}