package com.car.maintenance.controller;

import com.car.maintenance.model.Car;
import com.car.maintenance.model.Owner;
import com.car.maintenance.service.CarService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author Qu Li 
 * Created on 2025-02-11
 */
@Component
public class CarSelectionController {
    @FXML
    private TableView<Car> carSelectionTable;

    @FXML
    private TableColumn<Car, String> colLicensePlate, colBrand, colModel;

    @FXML
    private Button btnSelect, btnCancel;

    @Autowired
    private CarService carService;

    private Optional<Car> selectedCar = Optional.empty();

    private Owner owner;

    @FXML
    public void initialize() {
        colLicensePlate.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));

        btnSelect.setDisable(true);
        carSelectionTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnSelect.setDisable(newValue == null);
        });

        btnSelect.setOnAction(event -> selectCar());
        btnCancel.setOnAction(event -> closeWindow());
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
        loadCars();
    }

    private void loadCars() {
        List<Car> cars = carService.getCarsByOwnerId(owner.getId());
        ObservableList<Car> observableCars = FXCollections.observableArrayList(cars);
        carSelectionTable.setItems(observableCars);
        carSelectionTable.refresh();
    }

    private void selectCar() {
        selectedCar = Optional.ofNullable(carSelectionTable.getSelectionModel().getSelectedItem());
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public Optional<Car> getSelectedCar() {
        return selectedCar;
    }
}
