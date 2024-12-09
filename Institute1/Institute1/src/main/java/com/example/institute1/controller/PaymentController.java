package com.example.institute1.controller;

import com.example.institute1.dto.PaymentDto;
import com.example.institute1.model.PaymentModel;
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

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnGenerateReport;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<PaymentDto, String> colAdminId;

    @FXML
    private TableColumn<PaymentDto, Double> colPaymentAmount;

    @FXML
    private TableColumn<PaymentDto, Date> colPaymentDate;

    @FXML
    private TableColumn<PaymentDto, String> colPaymentId;

    @FXML
    private TableColumn<PaymentDto, String> colStudentId;

    @FXML
    private TextField lblAdminId;

    @FXML
    private TextField lblPayemtnAmount;

    @FXML
    private DatePicker lblPaymentDate;

    @FXML
    private Label lblPaymentId;

    @FXML
    private TextField lblStudentId;

    @FXML
    private TableView<PaymentDto> paymentTable;

    @FXML
    private Label lblDateTime;

    @FXML
    void paymentSave(ActionEvent event) {
        try {
            if (validatePaymentInputs()) {

                PaymentDto paymentDto = new PaymentDto(
                        lblPaymentId.getText(),
                        lblAdminId.getText(),
                        lblStudentId.getText(),
                        java.sql.Date.valueOf(lblPaymentDate.getValue()).toLocalDate(),
                        Double.parseDouble(lblPayemtnAmount.getText())
                );

                if (PaymentModel.savePayment(paymentDto)) {
                    new Alert(Alert.AlertType.INFORMATION, "Payment Saved").show();
                    refreshTable();
                    lblPaymentId.setText(PaymentModel.getNextPaymentID());
                    paymentReset(null);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Payment save failed").show();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred while saving the payment").show();
        }
    }

    @FXML
    void paymentUpdate(ActionEvent event) {
        PaymentDto selectedPayment = paymentTable.getSelectionModel().getSelectedItem();

        if (selectedPayment != null) {
            PaymentDto updatedPaymentDto = new PaymentDto(
                    lblPaymentId.getText(),
                    lblAdminId.getText(),
                    lblStudentId.getText(),
                    java.sql.Date.valueOf(lblPaymentDate.getValue()).toLocalDate(),
                    Double.parseDouble(lblPayemtnAmount.getText())
            );

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update this payment?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.YES) {
                try {
                    if (PaymentModel.updatePayment(updatedPaymentDto)) {
                        new Alert(Alert.AlertType.INFORMATION, "Payment Updated").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Payment update failed").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred while updating the payment").show();
                }
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Payment not found").show();
        }
    }

    @FXML
    void paymentDelete(ActionEvent event) {
        PaymentDto selectedPayment = paymentTable.getSelectionModel().getSelectedItem();

        if (selectedPayment != null) {
            String paymentId = selectedPayment.getPaymentId();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this payment?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.YES) {
                try {
                    if (PaymentModel.deletePayment(paymentId)) {
                        new Alert(Alert.AlertType.INFORMATION, "Payment Deleted").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Payment delete failed").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No payment selected for deletion.").show();
        }
    }

    @FXML
    void paymentReset(ActionEvent event) {
        lblAdminId.clear();
        lblStudentId.clear();
        lblPaymentDate.setValue(null);
        lblPayemtnAmount.clear();
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
        colPaymentId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colPaymentDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colPaymentAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        try {
            refreshTable();
            lblPaymentId.setText(PaymentModel.getNextPaymentID());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        paymentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectRow();
            }
        });

        initializeDateTime();
    }

    private void selectRow() {
        PaymentDto selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            lblPaymentId.setText(selectedPayment.getPaymentId());
            lblAdminId.setText(selectedPayment.getAdminId());
            lblStudentId.setText(selectedPayment.getStudentId());
            lblPaymentDate.setValue(selectedPayment.getPaymentDate());
            lblPayemtnAmount.setText(selectedPayment.getAmount().toString());
        }
    }

    private void refreshTable() {
        try {
            List<PaymentDto> payments = PaymentModel.getAllPayments();
            paymentTable.setItems(FXCollections.observableArrayList(payments));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean validatePaymentInputs() {
        String idPattern = "^[A-Za-z0-9]{2,10}$";
        String amountPattern = "^(\\d+(\\.\\d{1,2})?)$";
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";

        String paymentId = lblPaymentId.getText();
        String adminId = lblAdminId.getText();
        String studentId = lblStudentId.getText();
        String paymentAmount = lblPayemtnAmount.getText();
        String paymentDate = lblPaymentDate.getValue() != null ? lblPaymentDate.getValue().toString() : "";

        boolean isValidPaymentId = paymentId.matches(idPattern);
        boolean isValidAdminId = adminId.matches(idPattern);
        boolean isValidStudentId = studentId.matches(idPattern);
        boolean isValidPaymentAmount = paymentAmount.matches(amountPattern);
        boolean isValidPaymentDate = paymentDate.matches(datePattern);

        lblPaymentId.setStyle(isValidPaymentId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblAdminId.setStyle(isValidAdminId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblStudentId.setStyle(isValidStudentId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblPayemtnAmount.setStyle(isValidPaymentAmount ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblPaymentDate.setStyle(isValidPaymentDate ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");

        return isValidPaymentId && isValidAdminId && isValidStudentId && isValidPaymentAmount && isValidPaymentDate;
    }
}
