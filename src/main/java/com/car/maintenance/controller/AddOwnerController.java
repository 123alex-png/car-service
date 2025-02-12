package com.car.maintenance.controller;

import com.car.maintenance.model.Owner;
import com.car.maintenance.model.Car;
import com.car.maintenance.service.CarService;
import com.car.maintenance.service.OwnerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Qu Li 
 * Created on 2025-02-10
 */
@Component
public class AddOwnerController {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhone;

    @FXML
    private TableView<Car> carTable;

    @FXML
    private TableColumn<Car, String> colLicensePlate, colBrand, colModel;

    @FXML
    private TableColumn<Car, Button> colDelete;

    @FXML
    private Button btnAddCar, btnConfirm, btnCancel;

    private ObservableList<Car> cars = FXCollections.observableArrayList();

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private CarService carService;

    private OwnerListController ownerListController;

    private Owner owner;

    private boolean isEditable;

    public void setOwnerListController(OwnerListController ownerListController) {
        this.ownerListController = ownerListController;
    }

    public void setOwner(Owner owner, boolean isEditable, OwnerListController ownerListController) {
        this.owner = owner;
        this.isEditable = isEditable;
        this.ownerListController = ownerListController;

        // 如果是编辑或查看模式，填充数据
        if (owner != null) {
            txtName.setText(owner.getName());
            txtPhone.setText(owner.getPhone());

            // 加载已有车辆
            cars.setAll(carService.getCarsByOwnerId(owner.getId()));
        }

        // 设置字段是否可编辑
        txtName.setEditable(isEditable);
        txtPhone.setEditable(isEditable);
        btnAddCar.setDisable(!isEditable);

        // 设置按钮可见性
        btnConfirm.setVisible(isEditable);
    }

    @FXML
    public void initialize() {
        colLicensePlate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLicensePlate()));
        colBrand.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBrand()));
        colModel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));

        // 车辆删除按钮
        colDelete.setCellValueFactory(cellData -> {
            Button deleteButton = new Button("删除");
            deleteButton.setOnAction(event -> cars.remove(cellData.getValue()));
            return new javafx.beans.property.SimpleObjectProperty<>(deleteButton);
        });

        carTable.setRowFactory(tv -> {
            TableRow<Car> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Car car = row.getItem();
                    showAddCarDialog(car); // 传递选中的 Car
                }
            });
            return row;
        });

        carTable.setItems(cars);

        btnAddCar.setOnAction(event -> showAddCarDialog(null));
        btnConfirm.setOnAction(event -> saveOwner());
        btnCancel.setOnAction(event -> closeWindow());
    }

    private void showAddCarDialog(Car car) {
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle(car == null ? "添加车辆" : (isEditable ? "编辑车辆" : "查看车辆"));
        dialog.setHeaderText(car == null ? "请输入新车辆信息" : "车辆详情");

        ButtonType confirmButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtLicensePlate = new TextField();
        txtLicensePlate.setPromptText("车牌号");

        TextField txtBrand = new TextField();
        txtBrand.setPromptText("品牌");

        TextField txtModel = new TextField();
        txtModel.setPromptText("型号");

        if (car != null) {
            txtLicensePlate.setText(car.getLicensePlate());
            txtBrand.setText(car.getBrand());
            txtModel.setText(car.getModel());
        }

        txtLicensePlate.setEditable(isEditable);
        txtBrand.setEditable(isEditable);
        txtModel.setEditable(isEditable);

        grid.add(new Label("车牌号:"), 0, 0);
        grid.add(txtLicensePlate, 1, 0);
        grid.add(new Label("品牌:"), 0, 1);
        grid.add(txtBrand, 1, 1);
        grid.add(new Label("型号:"), 0, 2);
        grid.add(txtModel, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(!isEditable);

        AtomicReference<Car> resultCar = new AtomicReference<>(car);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType && isEditable) {
                if (resultCar.get() == null) {
                    resultCar.set(new Car(null, null, txtLicensePlate.getText(), txtBrand.getText(), txtModel.getText()));
                    cars.add(resultCar.get());
                } else {
                    resultCar.get().setLicensePlate(txtLicensePlate.getText());
                    resultCar.get().setBrand(txtBrand.getText());
                    resultCar.get().setModel(txtModel.getText());
                    carTable.refresh(); // 更新 UI
                }
                return resultCar.get();
            }
            return null;
        });

        dialog.showAndWait();
    }


    private void saveOwner() {
        if (txtName.getText().isEmpty()) {
            showAlert("错误", "姓名不能为空！");
            return;
        }

        if (owner == null) {
            // 新增车主
            owner = new Owner(null, txtName.getText(), txtPhone.getText());
            owner = ownerService.saveOwner(owner);
        } else {
            // 更新车主
            owner.setName(txtName.getText());
            owner.setPhone(txtPhone.getText());
            ownerService.saveOwner(owner);
        }

        List<Car> existingCars = carService.getCarsByOwnerId(owner.getId());

        List<Car> carsToDelete = existingCars.stream()
                .filter(car -> !cars.contains(car)) // 旧列表中存在但新列表中不存在的车
                .toList();

        for (Car car : carsToDelete) {
            carService.deleteCar(car.getId());
        }

        for (Car car : cars) {
            car.setOwner(owner);
            carService.saveCar(car);
        }

        if (ownerListController != null) {
            ownerListController.refreshTable();
        }

        closeWindow();
    }


    private void closeWindow() {
        Stage stage = (Stage) btnConfirm.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}