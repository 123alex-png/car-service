package com.car.maintenance.controller;

import com.car.maintenance.model.Owner;
import com.car.maintenance.service.OwnerService;
import com.car.maintenance.util.SpringFXMLLoader;
import com.car.maintenance.model.Car;
import com.car.maintenance.service.CarService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Qu Li 
 * Created on 2025-02-10
 */
@Component
public class OwnerListController {

    @FXML
    private TableView<Owner> ownerTable;

    @FXML
    private TableColumn<Owner, String> colName;

    @FXML
    private TableColumn<Owner, String> colPhone;

    @FXML
    private TableColumn<Owner, String> colCars;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnAddOwner;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private CarService carService;

    private void loadData() {
        Map<Long, List<Car>> carsMap = carService.getAllCars().stream()
                .collect(Collectors.groupingBy(car -> car.getOwner().getId()));

        colCars.setCellValueFactory(cellData -> {
            Owner owner = cellData.getValue();
            List<Car> cars = carsMap.getOrDefault(owner.getId(), Collections.emptyList());
            String licensePlates = cars.stream()
                    .map(Car::getLicensePlate)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            return new SimpleStringProperty(licensePlates);
        });

        ownerTable.setItems(FXCollections.observableArrayList(
                ownerService.getAllOwners()
        ));
        ownerTable.refresh();
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        loadData();

        ownerTable.setRowFactory(tv -> {
            TableRow<Owner> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            // "查看" 按钮
            MenuItem viewItem = new MenuItem("查看");
            viewItem.setOnAction(event -> openOwnerDialog(row.getItem(), false));

            // "编辑" 按钮
            MenuItem editItem = new MenuItem("编辑");
            editItem.setOnAction(event -> openOwnerDialog(row.getItem(), true));

            contextMenu.getItems().addAll(viewItem, editItem);
            row.setContextMenu(contextMenu);

            return row;
        });


        btnBack.setOnAction(event -> backToMainPage());

        btnAddOwner.setOnAction(actionEvent -> openOwnerDialog(null, true));
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

    private void openOwnerDialog(Owner owner, boolean isEditable) {
        try {
            FXMLLoader loader = SpringFXMLLoader.load("/fxml/AddOwnerView.fxml");
            Parent root = loader.load();

            AddOwnerController controller = loader.getController();
            controller.setOwner(owner, isEditable, this); // 传递 owner 和编辑模式

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 500, 400));
            stage.setTitle(isEditable ? "编辑车主" : "查看车主");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); // 等待用户输入
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void refreshTable() {
        loadData();
    }

}
