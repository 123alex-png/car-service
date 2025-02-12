package com.car.maintenance.controller;

import com.car.maintenance.model.MaintenanceRecord;
import com.car.maintenance.service.MaintenanceRecordService;
import com.car.maintenance.util.SpringFXMLLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author Qu Li
 * Created on 2025-02-10
 */
@Component
public class MaintenanceListController {

    @FXML
    private TableView<MaintenanceRecord> maintenanceTable;

    @FXML
    private TableColumn<MaintenanceRecord, String> colCar;

    @FXML
    private TableColumn<MaintenanceRecord, String> colOwnerName;

    @FXML
    private TableColumn<MaintenanceRecord, String> colDate;

    @FXML
    private TableColumn<MaintenanceRecord, String> colRemark;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnAddMaintenance;

    @Autowired
    private MaintenanceRecordService maintenanceRecordService;

    @FXML
    public void initialize() {
        colCar.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCar().getLicensePlate()));

        colOwnerName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCar().getOwner().getName()));

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colRemark.setCellValueFactory(new PropertyValueFactory<>("remark"));

        loadData();

        maintenanceTable.setRowFactory(tv -> {
            TableRow<MaintenanceRecord> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem viewItem = new MenuItem("查看");
            viewItem.setOnAction(event -> openMaintenanceDialog(row.getItem(), false));

            MenuItem editItem = new MenuItem("编辑");
            editItem.setOnAction(event -> openMaintenanceDialog(row.getItem(), true));

            contextMenu.getItems().addAll(viewItem, editItem);
            row.setContextMenu(contextMenu);

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openMaintenanceDialog(row.getItem(), false);
                }
            });

            return row;
        });

        btnBack.setOnAction(event -> backToMainPage());
        btnAddMaintenance.setOnAction(actionEvent -> openMaintenanceDialog(null, true));
    }

    private void loadData() {
        List<MaintenanceRecord> records = maintenanceRecordService.getAllRecords();
        maintenanceTable.setItems(FXCollections.observableArrayList(records));
    }

    public void refreshTable() {
        loadData();
        maintenanceTable.refresh();
    }

    private void openMaintenanceDialog(MaintenanceRecord record, boolean isEditable) {
        try {
            FXMLLoader loader = SpringFXMLLoader.load("/fxml/MaintenanceDetailView.fxml");
            Parent root = loader.load();

            MaintenanceDetailController controller = loader.getController();
            controller.setMaintenanceRecord(record, isEditable, this); // 传递数据

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle(isEditable ? "编辑保养记录" : "查看保养记录");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backToMainPage() {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            Parent root = SpringFXMLLoader.load("/fxml/MainView.fxml").load();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("汽车维修管理系统");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
