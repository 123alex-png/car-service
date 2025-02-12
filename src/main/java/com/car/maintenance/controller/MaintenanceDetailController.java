package com.car.maintenance.controller;

import com.car.maintenance.model.Car;
import com.car.maintenance.model.MaintenanceDetail;
import com.car.maintenance.model.MaintenanceRecord;
import com.car.maintenance.model.Owner;
import com.car.maintenance.service.CarService;
import com.car.maintenance.service.MaintenanceDetailService;
import com.car.maintenance.service.MaintenanceRecordService;
import com.car.maintenance.util.SpringFXMLLoader;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MaintenanceDetailController {

    @FXML
    private TextField txtCarPlate;

    @FXML
    private TextField txtOwnerName;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField txtRemark;

    @FXML
    private TableView<MaintenanceDetail> detailTable;

    @FXML
    private TableColumn<MaintenanceDetail, String> colService;

    @FXML
    private TableColumn<MaintenanceDetail, Integer> colQuantity;

    @FXML
    private TableColumn<MaintenanceDetail, Double> colPrice;

    @FXML
    private TableColumn<MaintenanceDetail, Double> colTotal;

    @FXML
    private Button btnConfirm, btnCancel, btnAddDetail, btnDeleteDetail;

    @FXML
    private Button btnSelectOwner, btnSelectCar;

    @Autowired
    private MaintenanceRecordService maintenanceRecordService;

    @Autowired
    private MaintenanceDetailService maintenanceDetailService;

    @Autowired
    private CarService carService;

    private MaintenanceRecord record;

    private MaintenanceListController parentController;

    private Owner selectedOwner;

    private Car selectedCar;

    private List<MaintenanceDetail> details = new ArrayList<>();

    private List<MaintenanceDetail> savedDetails = new ArrayList<>();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setMaintenanceRecord(MaintenanceRecord record, boolean isEditable, MaintenanceListController parentController) {
        this.record = record;
        this.parentController = parentController;

        if (record != null) {
            if (record.getCar() != null) {
                txtCarPlate.setText(record.getCar().getLicensePlate());
            }
            txtOwnerName.setText(record.getCar().getOwner().getName());
            Optional.ofNullable(record.getDate()).ifPresent(datePicker::setValue);
            txtRemark.setText(record.getRemark());

            this.selectedCar = record.getCar();
            this.selectedOwner = record.getCar().getOwner();

            // 加载 `MaintenanceDetail` 相关数据
            loadDetails();
        }

        // 设置 UI 可编辑状态
        txtCarPlate.setEditable(isEditable);
        txtOwnerName.setEditable(isEditable);
        datePicker.setDisable(!isEditable);
        txtRemark.setEditable(isEditable);

        btnConfirm.setVisible(isEditable);
        btnAddDetail.setDisable(!isEditable);
        btnDeleteDetail.setDisable(!isEditable);
    }

    @FXML
    public void initialize() {
        colService.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getService().getName()));
        colQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        colTotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getQuantity() * cellData.getValue().getPrice()).asObject());

        btnAddDetail.setOnAction(event -> addMaintenanceDetail());
        btnDeleteDetail.setOnAction(event -> deleteMaintenanceDetail());
        btnConfirm.setOnAction(event -> saveRecord());
        btnCancel.setOnAction(event -> close());
        btnSelectOwner.setOnAction(event -> selectOwner());
        btnSelectCar.setOnAction(event -> selectCar());
    }

    public void addDetails(MaintenanceDetail detail) {
        details.add(detail);
    }

    private void loadDetails() {
        detailTable.getItems().setAll(details);
        if (record != null) {
            savedDetails = maintenanceDetailService.getDetailsByRecord(record.getId());
            detailTable.getItems().addAll(savedDetails);
        }
        detailTable.refresh();
    }

    private void addMaintenanceDetail() {
        try {
            FXMLLoader loader = SpringFXMLLoader.load("/fxml/CarServiceProductSelection.fxml");
            Parent root = loader.load();

            CarServiceProductSelectionController controller = loader.getController();
            controller.setMaintenanceDetailController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("选择服务项目");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void selectOwner() {
        try {
            FXMLLoader loader = SpringFXMLLoader.load("/fxml/OwnerSelection.fxml");
            Parent root = loader.load();
            OwnerSelectionController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("选择车主");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            Optional<Owner> selected = controller.getSelectedOwner();
            selected.ifPresent(owner -> {
                this.selectedOwner = owner;
                txtOwnerName.setText(owner.getName());

                List<Car> cars = carService.getCarsByOwnerId(owner.getId());
                if (!cars.isEmpty()) {
                    this.selectedCar = cars.get(0);
                    txtCarPlate.setText(selectedCar.getLicensePlate());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectCar() {
        if (selectedOwner == null) {
            showAlert("请选择车主", "请先选择车主，然后再选择车辆。");
            return;
        }
        try {
            FXMLLoader loader = SpringFXMLLoader.load("/fxml/CarSelection.fxml");
            Parent root = loader.load();
            CarSelectionController controller = loader.getController();
            controller.setOwner(selectedOwner);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("选择车辆");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            Optional<Car> selected = controller.getSelectedCar();
            selected.ifPresent(car -> {
                this.selectedCar = car;
                txtCarPlate.setText(car.getLicensePlate());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteMaintenanceDetail() {
        MaintenanceDetail selectedDetail = detailTable.getSelectionModel().getSelectedItem();
        if (selectedDetail != null) {
            detailTable.getItems().remove(selectedDetail);
            detailTable.refresh();
        }
    }

    @FXML
    private void saveRecord() {
        if (selectedCar == null) {
            showAlert("错误", "车主未拥有车辆！");
            return;
        }
        if (record == null) {
            record = new MaintenanceRecord();
        }
        record.setCar(selectedCar);
        record.setDate(datePicker.getValue());
        record.setRemark(txtRemark.getText());
        maintenanceRecordService.saveRecord(record);

        List<MaintenanceDetail> currentDetails = new ArrayList<>(detailTable.getItems());
        List<MaintenanceDetail> deletedDetails = new ArrayList<>(savedDetails);

        deletedDetails.removeAll(currentDetails);  // 旧数据 - 现在的数据 = 需要删除的

        for (MaintenanceDetail detail : deletedDetails) {
            maintenanceDetailService.deleteDetail(detail.getId());
        }

        currentDetails.forEach(detail -> detail.setRecord(record));

        maintenanceDetailService.saveAllDetails(currentDetails);

        parentController.refreshTable();

        close();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void close() {
        details.clear();

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshTable() {
        loadDetails();
    }
}
