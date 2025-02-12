package com.car.maintenance.controller;

import com.car.maintenance.model.Owner;
import com.car.maintenance.service.OwnerService;
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
public class OwnerSelectionController {
    @FXML
    private TableView<Owner> ownerSelectionTable;

    @FXML
    private TableColumn<Owner, String> colOwnerName, colPhone;

    @FXML
    private Button btnSelect, btnCancel;

    @Autowired
    private OwnerService ownerService;

    private Optional<Owner> selectedOwner = Optional.empty();

    @FXML
    public void initialize() {
        colOwnerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        loadOwners();

        btnSelect.setDisable(true);
        ownerSelectionTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnSelect.setDisable(newValue == null);
        });

        btnSelect.setOnAction(event -> selectOwner());
        btnCancel.setOnAction(event -> closeWindow());
    }

    private void loadOwners() {
        List<Owner> owners = ownerService.getAllOwners();
        ObservableList<Owner> observableList = FXCollections.observableArrayList(owners);
        ownerSelectionTable.setItems(observableList);
        ownerSelectionTable.refresh();
    }

    private void selectOwner() {
        selectedOwner = Optional.ofNullable(ownerSelectionTable.getSelectionModel().getSelectedItem());
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public Optional<Owner> getSelectedOwner() {
        return selectedOwner;
    }
}
