package com.example.institute1.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Label LblStudentCourse;

    @FXML
    private Button btnExit;

    @FXML
    private AnchorPane content;

    @FXML
    private AnchorPane content2;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label lblAdmin;

    @FXML
    private Label lblCourse;

    @FXML
    private Label lblLecture;

    @FXML
    private Label lblModules;

    @FXML
    private Label lblParents;

    @FXML
    private Label lblPayment;

    @FXML
    private Label lblStaff;

    @FXML
    private Label lblStudent;

    @FXML
    void navigateToStudentCoursePage(MouseEvent event) { navigateTo("/view/StudentCourse.fxml");}

    @FXML
    void navigateToAdminPage(MouseEvent event) {
        navigateTo("/view/admin.fxml");
    }

    @FXML
    void navigateToCoursePage(MouseEvent event) {
        navigateTo("/view/course.fxml");
    }

    @FXML
    void navigateToFeePage(MouseEvent event) {
        navigateTo("/view/fee.fxml");
    }

    @FXML
    void navigateToLecturePage(MouseEvent event) {
        navigateTo("/view/lecture.fxml");
    }

    @FXML
    void navigateToModulePage(MouseEvent event) {
        navigateTo("/view/module.fxml");
    }

    @FXML
    void navigateToParentPage(MouseEvent event) {
        navigateTo("/view/parent.fxml");
    }

    @FXML
    void navigateToStaffPage(MouseEvent event) {
        navigateTo("/view/staff.fxml");
    }

    @FXML
    void navigateToStudentPage(MouseEvent event) {
        navigateTo("/view/student.fxml");
    }

    @FXML
    void navigateToPaymentPage(MouseEvent event) {
        navigateTo("/view/payment.fxml"); // Navigate to the payment page
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navigateTo("/view/admin.fxml");
    }

    private void navigateTo(String fxmlPath) {
        try {
            content.getChildren().clear();
            AnchorPane load = FXMLLoader.load(getClass().getResource(fxmlPath));
            content.getChildren().add(load);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load page!").show();
        }
    }

    @FXML
    void navegateToExit(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to Exit this System?",
                ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
            rootPane.getChildren().clear();
            try {
                Parent dashboard = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login.fxml")));
                rootPane.getChildren().add(dashboard);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
