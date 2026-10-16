package com.example.lab_2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.URISyntaxException;

public class HelloController {

    // --- меню ---
    @FXML private Button menuListButton;
    @FXML private Button menuAddButton;

    // --- экран списка ---
    @FXML private VBox listViewRoot;
    @FXML private ListView<Movie> movieListView;
    @FXML private Label emptyListLabel;

    // --- экран добавления ---
    @FXML private HBox addViewRoot;
    @FXML private ImageView posterImageView;
    @FXML private TextField imageUrlField;
    @FXML private Label imageStatusLabel;
    @FXML private Button loadImageButton;
    @FXML private Button clearImageButton;

    @FXML private TextField titleField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField directorField;
    @FXML private TextField rolesField;
    @FXML private TextField budgetField;

    @FXML private HBox starsBox;
    @FXML private Label ratingValueLabel;

    @FXML private Button saveButton;
    @FXML private Button resetButton;
    @FXML private Label infoLabel;

    private static final String PLACEHOLDER_TEXT = "Постер не загружен";
    private static final int MAX_STARS = 5;
    private static final String STAR_FILLED = "★";
    private static final String STAR_EMPTY = "☆";

    private final Label[] starLabels = new Label[MAX_STARS];
    private int currentRating = 0;

    private final ObservableList<Movie> movies = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        buildStars();
        setupMovieList();

        imageUrlField.setOnAction(e -> onLoadImageClicked());
        infoLabel.setText("");

        showListView();
    }

    // ==================== МЕНЮ ====================

    @FXML
    private void showListView() {
        listViewRoot.setVisible(true);
        listViewRoot.setManaged(true);

        addViewRoot.setVisible(false);
        addViewRoot.setManaged(false);

        emptyListLabel.setVisible(movies.isEmpty());
        emptyListLabel.setManaged(movies.isEmpty());
    }

    @FXML
    private void showAddView() {
        listViewRoot.setVisible(false);
        listViewRoot.setManaged(false);

        addViewRoot.setVisible(true);
        addViewRoot.setManaged(true);
    }

    // ==================== СПИСОК ФИЛЬМОВ ====================

    private void setupMovieList() {
        movieListView.setItems(movies);
        movieListView.setCellFactory(list -> new MovieCell());

        movieListView.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-padding: 0;"
        );
    }

    /**
     * Большая карточка фильма.
     */
    private static class MovieCell extends ListCell<Movie> {

        @Override
        protected void updateItem(Movie movie, boolean empty) {
            super.updateItem(movie, empty);

            if (empty || movie == null) {
                setGraphic(null);
                setText(null);
                setStyle("-fx-background-color: transparent;");
                return;
            }

            // ==================== ПОСТЕР ====================

            ImageView poster = new ImageView();

            poster.setFitWidth(170);
            poster.setFitHeight(240);
            poster.setPreserveRatio(true);
            poster.setSmooth(true);

            poster.setStyle(
                    "-fx-background-color: #0b0c11;" +
                            "-fx-background-radius: 12;"
            );

            if (movie.getPosterUrl() != null &&
                    !movie.getPosterUrl().isBlank()) {

                try {
                    poster.setImage(
                            new Image(
                                    movie.getPosterUrl(),
                                    170,
                                    240,
                                    true,
                                    true,
                                    true
                            )
                    );
                } catch (Exception ignored) {
                }
            }

            // ==================== НАЗВАНИЕ ====================

            Label titleLabel = new Label(movie.getTitle());

            titleLabel.setWrapText(true);
            titleLabel.setMaxWidth(470);

            titleLabel.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 27px;" +
                            "-fx-font-weight: bold;"
            );

            // ==================== ЖАНР + ГОД ====================

            Label metaLabel = new Label(
                    movie.getGenre() + "   •   " + movie.getYear()
            );

            metaLabel.setStyle(
                    "-fx-text-fill: #a8aac0;" +
                            "-fx-font-size: 15px;"
            );

            // ==================== РЕЙТИНГ ====================

            Label starsLabel = new Label(
                    renderStars(movie.getRating())
            );

            starsLabel.setStyle(
                    "-fx-text-fill: #f5c518;" +
                            "-fx-font-size: 20px;"
            );

            Label ratingLabel = new Label(
                    movie.getRating() + " / " + MAX_STARS
            );

            ratingLabel.setStyle(
                    "-fx-text-fill: #c7c9de;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;"
            );

            HBox ratingBox = new HBox(
                    10,
                    starsLabel,
                    ratingLabel
            );

            ratingBox.setAlignment(
                    javafx.geometry.Pos.CENTER_LEFT
            );

            // ==================== ИНФОРМАЦИЯ ====================

            Label directorLabel = createInfoLabel(
                    "Режиссёр",
                    movie.getDirector()
            );

            Label rolesLabel = createInfoLabel(
                    "В ролях",
                    movie.getRoles()
            );

            Label budgetLabel = createInfoLabel(
                    "Бюджет",
                    movie.getBudget()
            );

            VBox infoBox = new VBox(
                    8,
                    directorLabel,
                    rolesLabel,
                    budgetLabel
            );

            // ==================== ПРАВАЯ ЧАСТЬ ====================

            VBox textBox = new VBox(
                    13,
                    titleLabel,
                    metaLabel,
                    ratingBox,
                    infoBox
            );

            textBox.setFillWidth(true);
            textBox.setPrefWidth(500);

            // ==================== КАРТОЧКА ====================

            HBox card = new HBox(
                    24,
                    poster,
                    textBox
            );

            card.setAlignment(
                    javafx.geometry.Pos.CENTER_LEFT
            );

            card.setPrefHeight(270);
            card.setMaxWidth(Double.MAX_VALUE);

            card.setStyle(
                    "-fx-background-color: #1b1c27;" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-color: #292b3a;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 16;" +
                            "-fx-padding: 15;"
            );

            setGraphic(card);
            setText(null);

            setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-padding: 0 0 16 0;"
            );
        }

        private static Label createInfoLabel(
                String name,
                String value
        ) {
            Label label = new Label();

            String safeValue =
                    value == null || value.isBlank()
                            ? "Не указано"
                            : value;

            label.setText(
                    name + ":  " + safeValue
            );

            label.setWrapText(true);
            label.setMaxWidth(500);

            label.setStyle(
                    "-fx-text-fill: #c7c9de;" +
                            "-fx-font-size: 14px;"
            );

            return label;
        }

        private static String renderStars(int rating) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < MAX_STARS; i++) {
                sb.append(
                        i < rating
                                ? STAR_FILLED
                                : STAR_EMPTY
                );
            }

            return sb.toString();
        }
    }

    // ==================== ЗВЁЗДЫ ====================

    private void buildStars() {
        for (int i = 0; i < MAX_STARS; i++) {

            final int starIndex = i + 1;

            Label star = new Label(STAR_EMPTY);

            star.setStyle(
                    "-fx-font-size: 28px;" +
                            "-fx-text-fill: #4a4c60;" +
                            "-fx-cursor: hand;"
            );

            star.setOnMouseEntered(
                    e -> highlightStars(starIndex)
            );

            star.setOnMouseExited(
                    e -> highlightStars(currentRating)
            );

            star.setOnMouseClicked(e -> {
                currentRating = starIndex;
                highlightStars(currentRating);
                updateRatingLabel();
            });

            starLabels[i] = star;
            starsBox.getChildren().add(star);
        }
    }

    private void highlightStars(int count) {
        for (int i = 0; i < MAX_STARS; i++) {

            boolean filled = i < count;

            starLabels[i].setText(
                    filled ? STAR_FILLED : STAR_EMPTY
            );

            starLabels[i].setStyle(
                    "-fx-font-size: 28px;" +
                            "-fx-cursor: hand;" +
                            "-fx-text-fill: " +
                            (filled ? "#f5c518" : "#4a4c60") +
                            ";"
            );
        }
    }

    private void updateRatingLabel() {
        ratingValueLabel.setText(
                currentRating == 0
                        ? "Оценка не выбрана"
                        : "Оценка: " + currentRating +
                        " из " + MAX_STARS
        );
    }

    // ==================== ЗАГРУЗКА ПОСТЕРА ====================

    @FXML
    private void onLoadImageClicked() {

        String url =
                imageUrlField.getText() == null
                        ? ""
                        : imageUrlField.getText().trim();

        if (url.isEmpty()) {
            showStatus(
                    "Введите ссылку на изображение",
                    true
            );
            return;
        }

        if (!isValidUrl(url)) {
            showStatus(
                    "Некорректная ссылка (нужен http:// или https://)",
                    true
            );
            return;
        }

        showStatus("Загрузка...", false);
        loadImageButton.setDisable(true);

        Image image = new Image(url, true);

        image.progressProperty().addListener(
                (obs, oldVal, newVal) -> {

                    if (newVal.doubleValue() >= 1.0 &&
                            !image.isError()) {

                        posterImageView.setImage(image);

                        showStatus(
                                "Постер загружен",
                                false
                        );

                        loadImageButton.setDisable(false);
                    }
                }
        );

        image.errorProperty().addListener(
                (obs, wasError, isError) -> {

                    if (isError) {

                        posterImageView.setImage(null);

                        showStatus(
                                "Не удалось загрузить изображение",
                                true
                        );

                        loadImageButton.setDisable(false);
                    }
                }
        );
    }

    @FXML
    private void onClearImageClicked() {
        posterImageView.setImage(null);
        imageUrlField.clear();

        showStatus(
                PLACEHOLDER_TEXT,
                false
        );
    }

    // ==================== СОХРАНЕНИЕ ====================

    @FXML
    private void onSaveClicked() {

        String title =
                titleField.getText() == null
                        ? ""
                        : titleField.getText().trim();

        String genre =
                genreField.getText() == null
                        ? ""
                        : genreField.getText().trim();

        String yearText =
                yearField.getText() == null
                        ? ""
                        : yearField.getText().trim();

        String director =
                directorField.getText() == null
                        ? ""
                        : directorField.getText().trim();

        String roles =
                rolesField.getText() == null
                        ? ""
                        : rolesField.getText().trim();

        String budget =
                budgetField.getText() == null
                        ? ""
                        : budgetField.getText().trim();

        if (title.isEmpty() ||
                genre.isEmpty() ||
                yearText.isEmpty()) {

            infoLabel.setStyle(
                    "-fx-text-fill: #ff6b6b;"
            );

            infoLabel.setText(
                    "Заполните название, жанр и год перед сохранением."
            );

            return;
        }

        int year;

        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException ex) {

            infoLabel.setStyle(
                    "-fx-text-fill: #ff6b6b;"
            );

            infoLabel.setText(
                    "Год должен быть числом, например 1999."
            );

            return;
        }

        Movie movie = new Movie(
                title,
                genre,
                year,
                currentRating,
                imageUrlField.getText(),
                director,
                roles,
                budget
        );

        movies.add(0, movie);

        infoLabel.setStyle(
                "-fx-text-fill: #8ee39a;"
        );

        infoLabel.setText(
                "Фильм сохранён: " + movie.getTitle()
        );

        clearForm();
        showListView();
    }

    @FXML
    private void onResetClicked() {
        clearForm();
        infoLabel.setText("");
    }

    private void clearForm() {

        titleField.clear();
        genreField.clear();
        yearField.clear();
        directorField.clear();
        rolesField.clear();
        budgetField.clear();

        posterImageView.setImage(null);
        imageUrlField.clear();

        currentRating = 0;

        highlightStars(0);
        updateRatingLabel();

        showStatus(
                PLACEHOLDER_TEXT,
                false
        );
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ ====================

    private void showStatus(
            String text,
            boolean isError
    ) {

        imageStatusLabel.setText(text);

        imageStatusLabel.setStyle(
                isError
                        ? "-fx-text-fill: #ff6b6b;"
                        : "-fx-text-fill: #6b6d85;"
        );
    }

    private boolean isValidUrl(String url) {

        try {

            URI uri = new URI(url);

            String scheme = uri.getScheme();

            return scheme != null &&
                    (
                            scheme.equalsIgnoreCase("http") ||
                                    scheme.equalsIgnoreCase("https")
                    );

        } catch (URISyntaxException e) {
            return false;
        }
    }

    // ==================== МОДЕЛЬ ФИЛЬМА ====================

    public static class Movie {

        private final String title;
        private final String genre;
        private final int year;
        private final int rating;
        private final String posterUrl;

        private final String director;
        private final String roles;
        private final String budget;

        public Movie(
                String title,
                String genre,
                int year,
                int rating,
                String posterUrl,
                String director,
                String roles,
                String budget
        ) {

            this.title = title;
            this.genre = genre;
            this.year = year;
            this.rating = rating;
            this.posterUrl = posterUrl;
            this.director = director;
            this.roles = roles;
            this.budget = budget;
        }

        public String getTitle() {
            return title;
        }

        public String getGenre() {
            return genre;
        }

        public int getYear() {
            return year;
        }

        public int getRating() {
            return rating;
        }

        public String getPosterUrl() {
            return posterUrl;
        }

        public String getDirector() {
            return director;
        }

        public String getRoles() {
            return roles;
        }

        public String getBudget() {
            return budget;
        }
    }
}
