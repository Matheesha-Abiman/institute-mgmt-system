package com.example.institute1.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.example.institute1.dto.AdminDto;
import com.example.institute1.model.AdminModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private TableView<AdminDto> adminTable;

    @FXML
    private Button btnDelete, btnReset, btnSave, btnUpdate;

    @FXML
    private TableColumn<AdminDto, String> colId, colName, colEmail, colPassword;

    @FXML
    private TextField lblAdminEmail, lblAdminName;

    @FXML
    private Label lblAdminId, lblDateTime;

    @FXML
    private JFXPasswordField lblAdminPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("adminName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("adminEmail"));
        // colPassword.setCellValueFactory(new PropertyValueFactory<>("adminPassword"));

        try {
            refreshTable();
            lblAdminId.setText(AdminModel.getNextAdminID());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        adminTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectRow();
            }
        });

        initializeDateTime();
    }

    private void initializeDateTime() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd || HH:mm:ss");
            lblDateTime.setText(LocalDateTime.now().format(formatter));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void selectRow() {
        AdminDto selectedAdmin = adminTable.getSelectionModel().getSelectedItem();
        if (selectedAdmin != null) {
            lblAdminId.setText(selectedAdmin.getAdminId());
            lblAdminName.setText(selectedAdmin.getAdminName());
            lblAdminEmail.setText(selectedAdmin.getAdminEmail());
            lblAdminPassword.setText(selectedAdmin.getAdminPassword());
        }
    }

    private void refreshTable() {
        try {
            List<AdminDto> admins = AdminModel.getAllAdmins();
            adminTable.setItems(FXCollections.observableArrayList(admins));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void adminSave(ActionEvent event) {
        try {
            if (validateAdminInputs()) {
                AdminDto adminDto = new AdminDto(
                        lblAdminId.getText(),
                        lblAdminName.getText(),
                        lblAdminEmail.getText(),
                        lblAdminPassword.getText()
                );

                if (AdminModel.saveAdmin(adminDto)) {
                    new Alert(Alert.AlertType.INFORMATION, "Admin Saved").show();
                    refreshTable();
                    lblAdminId.setText(AdminModel.getNextAdminID());
                    adminReset(null);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Admin save failed").show();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void adminUpdate(ActionEvent event) {
        AdminDto selectedAdmin = adminTable.getSelectionModel().getSelectedItem();
        if (selectedAdmin != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update this admin?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                AdminDto updatedAdminDto = new AdminDto(
                        lblAdminId.getText(),
                        lblAdminName.getText(),
                        lblAdminEmail.getText(),
                        lblAdminPassword.getText()
                );

                try {
                    if (AdminModel.updateAdmin(updatedAdminDto)) {
                        new Alert(Alert.AlertType.INFORMATION, "Admin updated successfully").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Admin update failed").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "No Admin selected for update").show();
        }
    }

    @FXML
    private void adminDelete(ActionEvent event) {
        AdminDto selectedAdmin = adminTable.getSelectionModel().getSelectedItem();
        if (selectedAdmin != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this admin?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                try {
                    if (AdminModel.deleteAdmin(selectedAdmin.getAdminId())) {
                        new Alert(Alert.AlertType.INFORMATION, "Admin deleted successfully.").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete Admin.").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No admin selected for deletion.").show();
        }
    }

    @FXML
    private void adminReset(ActionEvent event) {
        lblAdminId.setText("");
        lblAdminName.clear();
        lblAdminEmail.clear();
        lblAdminPassword.clear();
    }

    private boolean validateAdminInputs() {
        String idPattern = "^[A-Za-z0-9]{2,10}$";
        String namePattern = "^(Mr\\.|Mrs\\.|Miss\\.|Ms\\.|Dr\\.)?\\s?[A-Za-z\\s'-]{2,50}$";
        String emailPattern = "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,3}$";
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

        String adminId = lblAdminId.getText();
        String adminName = lblAdminName.getText();
        String adminEmail = lblAdminEmail.getText();
        String adminPassword = lblAdminPassword.getText();

        boolean isValidAdminId = adminId.matches(idPattern);
        boolean isValidAdminName = adminName.matches(namePattern);
        boolean isValidAdminEmail = adminEmail.matches(emailPattern);
        boolean isValidAdminPassword = adminPassword.matches(passwordPattern);

        lblAdminId.setStyle(isValidAdminId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblAdminName.setStyle(isValidAdminName ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblAdminEmail.setStyle(isValidAdminEmail ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblAdminPassword.setStyle(isValidAdminPassword ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");

        if (!isValidAdminId) new Alert(Alert.AlertType.WARNING, "Invalid Admin ID").show();
        if (!isValidAdminName) new Alert(Alert.AlertType.WARNING, "Invalid name").show();
        if (!isValidAdminEmail) new Alert(Alert.AlertType.WARNING, "Invalid email format").show();
        if (!isValidAdminPassword) new Alert(Alert.AlertType.WARNING, "Password must be at least 8 characters, with at least one letter and one number").show();

        return isValidAdminId && isValidAdminName && isValidAdminEmail && isValidAdminPassword;
    }
}
