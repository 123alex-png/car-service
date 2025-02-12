package com.car.maintenance.controller;

import com.car.maintenance.util.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Qu Li 
 * Created on 2025-02-08
 */
@Component
public class MainController {

    @FXML
    private Button btnViewCustomers; // 绑定 FXML 里的按钮

    @FXML
    private Button bthViewServiceProducts;

    @FXML
    private Button bthViewMaintenances;

    @FXML
    public void initialize() {
        btnViewCustomers.setOnAction(event -> handle("/fxml/OwnerListView.fxml", "客户单管理"));
        bthViewServiceProducts.setOnAction(event -> handle("/fxml/CarServiceProductListView.fxml", "服务产品管理"));
        bthViewMaintenances.setOnAction(event -> handle("/fxml/MaintenanceListView.fxml", "保养记录管理"));
    }

    private void handle(String url, String title) {
        try {
            Stage stage = (Stage) btnViewCustomers.getScene().getWindow();
            Parent root = SpringFXMLLoader.load(url).load();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}