package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Контроллер главной формы "Панель мониторинга оборудования".
 * Устройства хранятся в списке devices и рисуются программно в devicesGrid,
 * поэтому: фильтры реально скрывают/показывают карточки, можно добавлять
 * произвольное количество устройств, а тестовая панель позволяет вручную
 * задать статус любому устройству.
 */
public class HelloController {

    // ---- Тип оборудования ----
    enum DeviceType {
        COMPUTER("Компьютер", "🖥"),
        PHONE("Телефон", "📱"),
        ROUTER("Роутер", "📡");

        private final String label;
        private final String icon;

        DeviceType(String label, String icon) {
            this.label = label;
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    // ---- Статус оборудования ----
    enum Status {
        ONLINE("В сети", "status-online", "status-dot-online"),
        WARNING("Ожидание", "status-warning", "status-dot-warning"),
        OFFLINE("Не в сети", "status-offline", "status-dot-offline");

        private final String label;
        private final String textStyle;
        private final String dotStyle;

        Status(String label, String textStyle, String dotStyle) {
            this.label = label;
            this.textStyle = textStyle;
            this.dotStyle = dotStyle;
        }

        public String getLabel() {
            return label;
        }

        public String getTextStyle() {
            return textStyle;
        }

        public String getDotStyle() {
            return dotStyle;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    // ---- Модель устройства ----
    static class Device {
        private final String name;
        private final DeviceType type;
        private Status status;

        Device(String name, DeviceType type, Status status) {
            this.name = name;
            this.type = type;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public DeviceType getType() {
            return type;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return name + " (" + type + ")";
        }
    }

    // ---- Верхняя панель / навигация ----
    @FXML private Button btnToggleNav;
    @FXML private VBox navPane;
    @FXML private Label lblOnlineCount;

    // ---- Устройства ----
    @FXML private GridPane devicesGrid;
    @FXML private TextField tfDeviceName;
    @FXML private ComboBox<DeviceType> cbNewDeviceType;

    // ---- Тестовая панель управления статусами ----
    @FXML private ComboBox<Device> cbTestDevice;
    @FXML private ComboBox<Status> cbTestStatus;

    // ---- Параметры записи ----
    @FXML private ComboBox<Device> cbDevice;
    @FXML private ComboBox<String> cbForm;
    @FXML private TextArea taNote;

    // ---- Нижняя строка состояния ----
    @FXML private Label lblStatusBar;

    private final ObservableList<Device> devices = FXCollections.observableArrayList();
    private DeviceType currentFilter = null; // null = показывать все типы
    private final Random random = new Random();

    private boolean navVisible = true;

    @FXML
    public void initialize() {
        // Стартовый набор устройств
        devices.addAll(
                new Device("Компьютер-01", DeviceType.COMPUTER, Status.ONLINE),
                new Device("Телефон-01", DeviceType.PHONE, Status.WARNING),
                new Device("Роутер-01", DeviceType.ROUTER, Status.OFFLINE)
        );

        // Форма добавления нового устройства
        cbNewDeviceType.getItems().addAll(DeviceType.values());
        cbNewDeviceType.getSelectionModel().selectFirst();

        // Тестовая панель — список устройств общий с моделью, обновляется сам
        cbTestDevice.setItems(devices);
        cbTestDevice.getSelectionModel().selectFirst();
        cbTestStatus.getItems().addAll(Status.values());
        cbTestStatus.getSelectionModel().selectFirst();

        // Параметры записи
        cbDevice.setItems(devices);
        cbDevice.getSelectionModel().selectFirst();
        cbForm.getItems().addAll("Очная", "Заочная", "Дистанционная");
        cbForm.getSelectionModel().selectFirst();

        rebuildDevicesGrid();
        updateOnlineCount();
    }

    // ===================== НАВИГАЦИЯ =====================

    @FXML
    private void handleToggleNavigation() {
        navVisible = !navVisible;
        navPane.setVisible(navVisible);
        navPane.setManaged(navVisible);
        btnToggleNav.setText(navVisible ? "Скрыть навигацию" : "Показать навигацию");
        lblStatusBar.setText(navVisible ? "Навигация отображена" : "Навигация скрыта");
    }

    @FXML
    private void handleAbout() {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("О программе");
        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.setResizable(false);

        AnchorPane root = new AnchorPane();
        root.setPrefSize(420, 280);
        root.getStyleClass().add("about-pane");

        Label title = new Label("Панель мониторинга оборудования");
        title.getStyleClass().add("about-title");
        AnchorPane.setTopAnchor(title, 20.0);
        AnchorPane.setLeftAnchor(title, 20.0);
        AnchorPane.setRightAnchor(title, 20.0);

        Label version = new Label("Версия 1.1");
        version.getStyleClass().add("about-version");
        AnchorPane.setTopAnchor(version, 55.0);
        AnchorPane.setLeftAnchor(version, 20.0);

        TextArea info = new TextArea(
                "Учебное приложение для мониторинга состояния оборудования " +
                        "(компьютеры, телефоны, роутеры). Можно добавлять устройства " +
                        "и вручную менять их статус через тестовую панель.");
        info.setEditable(false);
        info.setWrapText(true);
        info.getStyleClass().add("about-text");
        AnchorPane.setTopAnchor(info, 85.0);
        AnchorPane.setLeftAnchor(info, 20.0);
        AnchorPane.setRightAnchor(info, 20.0);
        AnchorPane.setBottomAnchor(info, 60.0);

        Button btnClose = new Button("Закрыть");
        btnClose.getStyleClass().add("close-button");
        btnClose.setOnAction(e -> aboutStage.close());
        AnchorPane.setBottomAnchor(btnClose, 15.0);
        AnchorPane.setRightAnchor(btnClose, 15.0);

        root.getChildren().addAll(title, version, info, btnClose);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(HelloApplication.class.getResource("style.css").toExternalForm());
        aboutStage.setScene(scene);
        aboutStage.show();
    }

    // ===================== ФИЛЬТРЫ (реально скрывают карточки) =====================

    @FXML
    private void handleFilterAll() {
        currentFilter = null;
        rebuildDevicesGrid();
        lblStatusBar.setText("Показаны все устройства");
    }

    @FXML
    private void handleFilterComputers() {
        currentFilter = DeviceType.COMPUTER;
        rebuildDevicesGrid();
        lblStatusBar.setText("Фильтр: компьютеры");
    }

    @FXML
    private void handleFilterPhones() {
        currentFilter = DeviceType.PHONE;
        rebuildDevicesGrid();
        lblStatusBar.setText("Фильтр: телефоны");
    }

    @FXML
    private void handleFilterRouters() {
        currentFilter = DeviceType.ROUTER;
        rebuildDevicesGrid();
        lblStatusBar.setText("Фильтр: роутеры");
    }

    // ===================== ДОБАВЛЕНИЕ УСТРОЙСТВА =====================

    @FXML
    private void handleAddDevice() {
        String name = tfDeviceName.getText();
        if (name == null || name.isBlank()) {
            lblStatusBar.setText("Введите название устройства");
            return;
        }
        DeviceType type = cbNewDeviceType.getValue();
        if (type == null) {
            type = DeviceType.COMPUTER;
        }

        Device device = new Device(name.trim(), type, Status.ONLINE);
        devices.add(device);

        tfDeviceName.clear();
        rebuildDevicesGrid();
        updateOnlineCount();
        lblStatusBar.setText("Устройство «" + device.getName() + "» добавлено");
    }

    // ===================== СЛУЧАЙНОЕ ОБНОВЛЕНИЕ СТАТУСОВ =====================

    @FXML
    private void handleRefreshStatus() {
        Status[] values = Status.values();
        for (Device d : devices) {
            d.setStatus(values[random.nextInt(values.length)]);
        }
        rebuildDevicesGrid();
        updateOnlineCount();
        lblStatusBar.setText("Статусы всех устройств обновлены случайно");
    }

    // ===================== ТЕСТОВАЯ ПАНЕЛЬ: РУЧНОЕ УПРАВЛЕНИЕ СТАТУСОМ =====================

    @FXML
    private void handleApplyTestStatus() {
        Device device = cbTestDevice.getValue();
        Status status = cbTestStatus.getValue();

        if (device == null || status == null) {
            lblStatusBar.setText("Выберите устройство и статус в тестовой панели");
            return;
        }

        device.setStatus(status);
        rebuildDevicesGrid();
        updateOnlineCount();
        lblStatusBar.setText("Статус «" + device.getName() + "» изменён на «" + status.getLabel() + "»");
    }

    // ===================== ОТРИСОВКА КАРТОЧЕК =====================

    private void rebuildDevicesGrid() {
        devicesGrid.getChildren().clear();

        List<Device> filtered = devices.stream()
                .filter(d -> currentFilter == null || d.getType() == currentFilter)
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            Label empty = new Label("Нет устройств для отображения");
            empty.getStyleClass().add("empty-label");
            devicesGrid.add(empty, 0, 0);
            return;
        }

        int col = 0;
        int row = 0;
        for (Device d : filtered) {
            devicesGrid.add(createDeviceCard(d), col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private AnchorPane createDeviceCard(Device d) {
        AnchorPane card = new AnchorPane();
        card.getStyleClass().add("device-card");
        card.setPrefHeight(150.0);

        VBox box = new VBox(8.0);
        box.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);

        Label icon = new Label(d.getType().getIcon());
        icon.getStyleClass().add("device-icon");

        Label name = new Label(d.getName());
        name.getStyleClass().add("device-name");

        Label status = new Label(d.getStatus().getLabel());
        status.getStyleClass().add(d.getStatus().getTextStyle());

        box.getChildren().addAll(icon, name, status);

        // Индикатор-кружок привязан к правому верхнему углу карточки (AnchorPane)
        Circle dot = new Circle(6.0);
        dot.getStyleClass().add(d.getStatus().getDotStyle());
        AnchorPane.setTopAnchor(dot, 10.0);
        AnchorPane.setRightAnchor(dot, 10.0);

        card.getChildren().addAll(box, dot);
        return card;
    }

    private void updateOnlineCount() {
        long online = devices.stream().filter(d -> d.getStatus() == Status.ONLINE).count();
        lblOnlineCount.setText("Онлайн: " + online + " из " + devices.size());
    }
}