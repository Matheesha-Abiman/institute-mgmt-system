package com.example.institute1.controller;

import com.example.institute1.db.DBConnection;
import com.example.institute1.dto.StaffDto;
import com.example.institute1.model.StaffModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class StaffController implements Initializable {

    @FXML
    private TableView<StaffDto> StaffTable;

    @FXML
    private Button btnDelete, btnReset, btnSave, btnUpdate, btnGenarateReport;

    @FXML
    private TableColumn<StaffDto, String> colAdminId, colStaffDesc, colStaffEmail, colStaffId, colStaffName;

    @FXML
    private TableColumn<StaffDto, Double> colStaffSalary;

    @FXML
    private TextField lblAdminId, lblStaffDesc, lblStaffEmail, lblStaffName, lblStaffSalary;

    @FXML
    private Label lblStaffId, lblDateTime;

    @FXML
    void report(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Map<String, Object> parameters = new HashMap<>();

            parameters.put("todayDate", LocalDate.now().toString());
            parameters.put("time", LocalTime.now().toString());

            JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/report/StaffDetailsNewReport.jrxml"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    connection
            );

            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            new Alert(Alert.AlertType.ERROR, "Fail to load report..!");
            e.printStackTrace();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Data empty..!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void staffSave(ActionEvent event) {
        try {
            if (validateStaffInputs()) {
                StaffDto staffDto = new StaffDto(
                        lblStaffId.getText(),
                        lblAdminId.getText(),
                        lblStaffName.getText(),
                        lblStaffEmail.getText(),
                        lblStaffDesc.getText(),
                        Double.parseDouble(lblStaffSalary.getText())
                );

                if (StaffModel.saveStaff(staffDto)) {
                    new Alert(Alert.AlertType.INFORMATION, "Staff saved successfully").show();
                    refreshTable();
                    lblStaffId.setText(StaffModel.getNextStaffID());
                    staffReset(null);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save staff").show();
                }
            }
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error occurred: " + e.getMessage()).show();
        }
    }

    @FXML
    void staffUpdate(ActionEvent event) {
        StaffDto selectedStaff = StaffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update this staff?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.YES) {
                try {
                    StaffDto updatedStaffDto = new StaffDto(
                            lblStaffId.getText(),
                            lblAdminId.getText(),
                            lblStaffName.getText(),
                            lblStaffEmail.getText(),
                            lblStaffDesc.getText(),
                            Double.parseDouble(lblStaffSalary.getText())
                    );

                    if (StaffModel.updateStaff(updatedStaffDto)) {
                        new Alert(Alert.AlertType.INFORMATION, "Staff updated successfully").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update staff").show();
                    }
                } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Error occurred: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "No staff selected for update").show();
        }
    }

    @FXML
    void staffDelete(ActionEvent event) {
        StaffDto selectedStaff = StaffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this staff?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.YES) {
                try {
                    if (StaffModel.deleteStaff(selectedStaff.getStaffId())) {
                        new Alert(Alert.AlertType.INFORMATION, "Staff deleted successfully.").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete staff.").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No staff selected for deletion.").show();
        }
    }

    @FXML
    void staffReset(ActionEvent event) {
        lblStaffId.setText("");
        lblAdminId.clear();
        lblStaffName.clear();
        lblStaffEmail.clear();
        lblStaffDesc.clear();
        lblStaffSalary.clear();
    }

    private void initializeDateTime() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd || HH:mm:ss");
            lblDateTime.setText(LocalDateTime.now().format(formatter));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colStaffId.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        colStaffName.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        colStaffEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colStaffDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStaffSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        try {
            refreshTable();
            lblStaffId.setText(StaffModel.getNextStaffID());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        StaffTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectRow();
            }
        });
        initializeDateTime();
    }

    private void selectRow() {
        StaffDto selectedStaff = StaffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff != null) {
            lblStaffId.setText(selectedStaff.getStaffId());
            lblAdminId.setText(selectedStaff.getAdminId());
            lblStaffName.setText(selectedStaff.getStaffName());
            lblStaffEmail.setText(selectedStaff.getEmail());
            lblStaffDesc.setText(selectedStaff.getDescription());
            lblStaffSalary.setText(String.valueOf(selectedStaff.getSalary()));
        }
    }

    private void refreshTable() {
        try {
            List<StaffDto> staffs = StaffModel.getAllStaff();
            ObservableList<StaffDto> staffList = FXCollections.observableArrayList(staffs);
            StaffTable.setItems(staffList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load staff data").show();
        }
    }

    private boolean validateStaffInputs() {
        String idPattern = "^[A-Za-z0-9]{2,10}$";
        String namePattern = "^[A-Za-z\\s'-]{2,50}$";
        String descriptionPattern = "^[A-Za-z0-9\\s,.'-]{2,100}$";
        String emailPattern = "^[\\w-]+(?:\\.[\\w-]+)*@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,7}$";
        String salaryPattern = "^[0-9]+(\\.[0-9]{1,2})?$";

        if (!lblStaffId.getText().matches(idPattern)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Staff ID").show();
            return false;
        }
        if (!lblStaffName.getText().matches(namePattern)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Staff Name").show();
            return false;
        }
        if (!lblStaffEmail.getText().matches(emailPattern)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Email").show();
            return false;
        }
        if (!lblStaffDesc.getText().matches(descriptionPattern)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Staff Description").show();
            return false;
        }
        if (!lblStaffSalary.getText().matches(salaryPattern)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Salary").show();
            return false;
        }

        return true;
    }
}
