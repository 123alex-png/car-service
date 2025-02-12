package com.car.maintenance.controller;

import com.car.maintenance.service.CarServiceProductService;
import com.car.maintenance.model.CarServiceProduct;
import com.car.maintenance.util.SpringFXMLLoader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Qu Li
 * Created on 2025-02-10
 */
@Component
public class CarServiceProductListController {

    @FXML
    private TableView<CarServiceProduct> serviceTable;

    @FXML
    private TableColumn<CarServiceProduct, String> colName;

    @FXML
    private TableColumn<CarServiceProduct, String> colUnitPrice;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnAddService;

    @Autowired
    private CarServiceProductService carServiceProductService;

    private void loadData() {
        serviceTable.setItems(FXCollections.observableArrayList(carServiceProductService.getAllCarServiceProducts()));
        serviceTable.refresh();
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        loadData();

        serviceTable.setRowFactory(tv -> {
            TableRow<CarServiceProduct> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem viewItem = new MenuItem("查看");
            viewItem.setOnAction(event -> showAddServiceDialog(row.getItem(), false));

            MenuItem editItem = new MenuItem("编辑");
            editItem.setOnAction(event -> showAddServiceDialog(row.getItem(), true));

            contextMenu.getItems().addAll(viewItem, editItem);
            row.setContextMenu(contextMenu);

            return row;
        });

        btnBack.setOnAction(event -> backToMainPage());
        btnAddService.setOnAction(actionEvent -> showAddServiceDialog(null, true));
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

    private void showAddServiceDialog(CarServiceProduct carServiceProduct, boolean isEditable) {
        Dialog<CarServiceProduct> dialog = new Dialog<>();
        dialog.setTitle(carServiceProduct == null ? "添加服务" : (isEditable ? "编辑服务信息" : "查看服务信息"));
        dialog.setHeaderText(carServiceProduct == null ? "请输入新服务信息" : "服务详情");

        ButtonType confirmButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtName = new TextField();
        txtName.setPromptText("服务名称");

        TextField txtUnitPrice = new TextField();
        txtUnitPrice.setPromptText("服务参考单价");

        if (carServiceProduct != null) {
            txtName.setText(carServiceProduct.getName());
            txtUnitPrice.setText(String.valueOf(carServiceProduct.getUnitPrice()));
        }

        txtName.setEditable(isEditable);
        txtUnitPrice.setEditable(isEditable);

        grid.add(new Label("服务名称:"), 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(new Label("参考单价:"), 0, 1);
        grid.add(txtUnitPrice, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(!isEditable);

        AtomicReference<CarServiceProduct> resultCarServiceProduct = new AtomicReference<>(carServiceProduct);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType && isEditable) {
                try {
                    double unitPrice = Double.parseDouble(txtUnitPrice.getText());

                    if (resultCarServiceProduct.get() == null) {
                        resultCarServiceProduct.set(new CarServiceProduct(null, txtName.getText(), unitPrice));
                    } else {
                        resultCarServiceProduct.get().setName(txtName.getText());
                        resultCarServiceProduct.get().setUnitPrice(unitPrice);
                    }
                    return resultCarServiceProduct.get();
                } catch (NumberFormatException e) {
                    showAlert("输入错误", "请输入正确的单价（数字）！");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::saveCarServiceProduct);
    }

    private void saveCarServiceProduct(CarServiceProduct carServiceProduct) {
        if (carServiceProduct != null) {
            carServiceProductService.saveCarServiceProduct(carServiceProduct);
            refreshTable();
        }
    }

    public void refreshTable() {
        loadData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
