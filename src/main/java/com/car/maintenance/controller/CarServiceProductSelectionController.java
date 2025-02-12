package com.car.maintenance.controller;

import com.car.maintenance.model.CarServiceProduct;
import com.car.maintenance.model.MaintenanceDetail;
import com.car.maintenance.service.CarServiceProductService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Qu Li 
 * Created on 2025-02-11
 */
@Component
public class CarServiceProductSelectionController {
    @FXML
    private TableView<CarServiceProduct> serviceTable;

    @FXML
    private TableColumn<CarServiceProduct, String> colServiceName, colUnitPrice;

    @FXML
    private TextField txtPrice, txtQuantity;

    @FXML
    private Button btnConfirm, btnCancel;

    @Autowired
    private CarServiceProductService carServiceProductService;

    private MaintenanceDetailController parentController;

    @FXML
    public void initialize() {
        colServiceName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colUnitPrice.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getUnitPrice())));

        btnConfirm.setOnAction(event -> selectServiceProduct());
        btnCancel.setOnAction(event -> closeWindow());
    }

    public void setMaintenanceDetailController(MaintenanceDetailController parentController) {
        this.parentController = parentController;
        loadServiceProducts();
    }

    private void loadServiceProducts() {
        serviceTable.getItems().setAll(carServiceProductService.getAllCarServiceProducts());
    }

    private void selectServiceProduct() {
        CarServiceProduct selectedService = serviceTable.getSelectionModel().getSelectedItem();

        if (selectedService == null) {
            showAlert("请选择一个服务项目");
            return;
        }

        try {
            double price = Double.parseDouble(txtPrice.getText());
            int quantity = Integer.parseInt(txtQuantity.getText());
            parentController.addDetails(new MaintenanceDetail(null, null, selectedService, quantity, price));
            if (parentController != null) {
                parentController.refreshTable();
            }
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("请输入正确的服务价格");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("输入错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
