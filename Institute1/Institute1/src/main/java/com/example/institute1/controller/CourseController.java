package com.example.institute1.controller;

import com.example.institute1.dto.CourseDto;
import com.example.institute1.model.CourseModel;
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

public class CourseController implements Initializable {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<?, ?> colAdminId;

    @FXML
    private TableColumn<?, ?> colCourseDesc;

    @FXML
    private TableColumn<?, ?> colCourseFee;

    @FXML
    private TableColumn<?, ?> colCourseID;

    @FXML
    private TableColumn<?, ?> colCourseName;

    @FXML
    private TableView<CourseDto> courseTable;

    @FXML
    private TextField lblAdminId;

    @FXML
    private TextField lblCourseDesc;

    @FXML
    private TextField lblCourseFee;

    @FXML
    private Label lblCourseId;

    @FXML
    private TextField lblCourseName;

    @FXML
    private Label lblDateTime;

    @FXML
    void courseSave(ActionEvent event) {
        try {
            if (validateCourseInputs()) {
                CourseDto courseDto = new CourseDto(
                        lblCourseId.getText(),
                        lblAdminId.getText(),
                        lblCourseName.getText(),
                        lblCourseFee.getText(),
                        lblCourseDesc.getText()
                );
                if (CourseModel.saveCourse(courseDto)) {
                    new Alert(Alert.AlertType.INFORMATION, "Course Saved").show();
                    refreshTable();
                    lblCourseId.setText(CourseModel.getNextCourseID());
                    courseReset(null);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Course Not Saved").show();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void courseUpdate(ActionEvent event) {
        CourseDto selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to update this course?",
                    ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                CourseDto updatedCourseDto = new CourseDto(
                        lblCourseId.getText(),
                        lblAdminId.getText(),
                        lblCourseName.getText(),
                        lblCourseFee.getText(),
                        lblCourseDesc.getText()
                );
                try {
                    if (CourseModel.updateCourse(updatedCourseDto)) {
                        new Alert(Alert.AlertType.INFORMATION, "Course updated successfully").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Course update failed").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No Course selected for update").show();
        }
    }

    @FXML
    void courseDelete(ActionEvent event) {
        CourseDto selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this course?",
                    ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                try {
                    if (CourseModel.deleteCourse(selectedCourse.getCourseId())) {
                        new Alert(Alert.AlertType.INFORMATION, "Course deleted successfully").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete course").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No Course selected for deletion").show();
        }
    }

    @FXML
    void courseReset(ActionEvent event) {
        lblCourseId.setText("");
        lblAdminId.clear();
        lblCourseName.clear();
        lblCourseFee.clear();
        lblCourseDesc.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCourseID.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCourseFee.setCellValueFactory(new PropertyValueFactory<>("courseFee"));
        colCourseDesc.setCellValueFactory(new PropertyValueFactory<>("courseDescription"));

        try {
            refreshTable();
            lblCourseId.setText(CourseModel.getNextCourseID());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        courseTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
        CourseDto selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            lblCourseId.setText(selectedCourse.getCourseId());
            lblAdminId.setText(selectedCourse.getAdminId());
            lblCourseName.setText(selectedCourse.getCourseName());
            lblCourseFee.setText(selectedCourse.getCourseFee());
            lblCourseDesc.setText(selectedCourse.getCourseDescription());
        }
    }

    private void refreshTable() {
        try {
            List<CourseDto> courses = CourseModel.getAllCourses();
            courseTable.setItems(FXCollections.observableArrayList(courses));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean validateCourseInputs() {
        String idPattern = "^[C][0-9]{3}$";
        String namePattern = "^[A-Za-z\\s]{2,50}$";
        String feePattern = "^[0-9]+(\\.[0-9]{1,2})?$";
        String descPattern = "^.{0,200}$";

        String courseId = lblCourseId.getText();
        String courseName = lblCourseName.getText();
        String courseFee = lblCourseFee.getText();
        String courseDesc = lblCourseDesc.getText();

        boolean isValidCourseId = courseId.matches(idPattern);
        boolean isValidCourseName = courseName.matches(namePattern);
        boolean isValidCourseFee = courseFee.matches(feePattern);
        boolean isValidCourseDesc = courseDesc.matches(descPattern);

        lblCourseId.setStyle(isValidCourseId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblCourseName.setStyle(isValidCourseName ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblCourseFee.setStyle(isValidCourseFee ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblCourseDesc.setStyle(isValidCourseDesc ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");

        if (!isValidCourseId) new Alert(Alert.AlertType.WARNING, "Invalid Course ID. Must be in format C001.").show();
        if (!isValidCourseName) new Alert(Alert.AlertType.WARNING, "Invalid Course Name. Only letters and spaces allowed, 2-50 characters.").show();
        if (!isValidCourseFee) new Alert(Alert.AlertType.WARNING, "Invalid Fee. Must be a number, up to 2 decimal places.").show();
        if (!isValidCourseDesc) new Alert(Alert.AlertType.WARNING, "Description too long. Maximum 200 characters.").show();

        return isValidCourseId && isValidCourseName && isValidCourseFee && isValidCourseDesc;
    }
}
