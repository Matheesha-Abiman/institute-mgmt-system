package com.example.institute1.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResetpassController {

    @FXML
    private JFXPasswordField conformPassTf;

    @FXML
    private JFXPasswordField newPassTf;

    @FXML
    private JFXTextField resetTf;

    @FXML
    private Label lblDateTime;

    private String email;
    private String otp;

    public void setEmailAndOTP(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    @FXML
    void clickPassResetSubmit(ActionEvent event) {
        String resetCode = resetTf.getText().trim();
        String newPassword = newPassTf.getText();
        String confirmPassword = conformPassTf.getText();

        if (resetCode.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "All fields are required.").show();
        } else if (!resetCode.equals(otp)) {
            new Alert(Alert.AlertType.ERROR, "Invalid reset code.").show();
        } else if (!newPassword.equals(confirmPassword)) {
            new Alert(Alert.AlertType.ERROR, "Passwords do not match.").show();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Password reset successfully.").show();

            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/dashboard.fxml"));
                Stage stage = (Stage) resetTf.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to load the dashboard.").show();
            }

            clearFields();
        }
    }

    private void clearFields() {
        resetTf.clear();
        newPassTf.clear();
        conformPassTf.clear();
    }

    private void initializeDateTime() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            lblDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd || HH:mm:ss")));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @FXML
    public void initialize() {
        initializeDateTime();
    }

    public void enterNewPassword(ActionEvent actionEvent) {
    }

    public void enterConformPassword(ActionEvent actionEvent) {
    }

    public void enterResetCode(ActionEvent actionEvent) {
    }
}
