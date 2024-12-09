package com.example.institute1.controller;

import com.example.institute1.dto.CourseDto;
import com.example.institute1.model.CourseModel;
import com.example.institute1.model.StudentModel;
import com.example.institute1.dto.StudentDto;
import com.jfoenix.controls.JFXComboBox;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    @FXML
    private Button btnDelete, btnReset, btnSave, btnUpdate;

    @FXML
    private TableColumn<StudentDto, String> colAdminId, colParentId, colStudentAddress, colStudentId, colStudentName;

    @FXML
    private TableColumn<StudentDto, Date> colStudentDOB;

    @FXML
    private TextField lblAdminId, lblParentId, lblStudentAddress, lblStudentDOB, lblStudentName;

    @FXML
    private Label lblStudentId;

    @FXML
    private Label lblDateTime;

    @FXML
    private TableView<StudentDto> studentTable;

    @FXML
    private JFXComboBox<String> combo;

    @FXML
    void combo(ActionEvent event) throws SQLException, ClassNotFoundException {
        String selectCourseId = combo.getSelectionModel().getSelectedItem();
        CourseDto courseDto = CourseModel.searchById(selectCourseId);
    }

    private void loadCourseId() throws SQLException, ClassNotFoundException {
        ArrayList<String> courseIds = CourseModel.getAllCourseId();
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(courseIds);
        combo.setItems(observableList);
    }

    @FXML
    void studentDelete(ActionEvent event) {
        StudentDto selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this student?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.YES) {
                try {
                    boolean isDeleted = StudentModel.deleteStudent(selectedStudent.getStudentId());
                    if (isDeleted) {
                        new Alert(Alert.AlertType.INFORMATION, "Student deleted successfully.").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete student.").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No student selected for deletion.").show();
        }
    }

    private void refreshPage() throws SQLException, ClassNotFoundException {
        refreshTable();
        lblStudentId.setText(StudentModel.getNextStudentID());
        studentReset(null);
    }

    @FXML
    void studentReset(ActionEvent event) {
        lblStudentId.setText("");
        lblAdminId.clear();
        lblParentId.clear();
        lblStudentName.clear();
        lblStudentAddress.clear();
        lblStudentDOB.clear();
    }

    @FXML
    void studentSave(ActionEvent event) {
        try {
            if (validateInputs()) {
                String studentId = lblStudentId.getText();
                String adminId = lblAdminId.getText();
                String parentId = lblParentId.getText();
                String name = lblStudentName.getText();
                String address = lblStudentAddress.getText();

                String coursenNme = combo.getSelectionModel().getSelectedItem();
                String courseId = StudentModel.getCourseId(coursenNme);

                Date dob = new SimpleDateFormat("dd/MM/yyyy").parse(lblStudentDOB.getText());

                StudentDto studentDto = new StudentDto(studentId, adminId, parentId, name, address, dob);
                boolean isSaved = StudentModel.saveStudent(studentDto,courseId);

                if (isSaved) {
                    new Alert(Alert.AlertType.INFORMATION, "Student saved successfully!").show();
                    refreshPage();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save student!").show();
                }
            }
        } catch (ParseException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid date format! Use dd/MM/yyyy").show();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void studentUpdate(ActionEvent event) {
        StudentDto selectedStudent = studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update this student?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.YES) {
                try {
                    String updatedStudentId = lblStudentId.getText();
                    String updatedAdminId = lblAdminId.getText();
                    String updatedParentId = lblParentId.getText();
                    String updatedName = lblStudentName.getText();
                    String updatedAddress = lblStudentAddress.getText();
                    Date updatedDOB = new SimpleDateFormat("dd/MM/yyyy").parse(lblStudentDOB.getText());

                    String updatedCourseName = combo.getSelectionModel().getSelectedItem();
                    String updatedCourseId = StudentModel.getCourseId(updatedCourseName);

                    StudentDto updatedStudentDto = new StudentDto(updatedStudentId, updatedAdminId, updatedParentId, updatedName, updatedAddress, updatedDOB);

                    boolean isUpdated = StudentModel.updateStudentWithCourse(updatedStudentDto, updatedCourseId);
                    if (isUpdated) {
                        new Alert(Alert.AlertType.INFORMATION, "Student updated successfully.").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update student.").show();
                    }
                } catch (ParseException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid date format! Use dd/MM/yyyy").show();
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No student selected for update.").show();
        }
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
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        colParentId.setCellValueFactory(new PropertyValueFactory<>("parentId"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStudentAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colStudentDOB.setCellValueFactory(new PropertyValueFactory<>("dob"));

        try {
            loadCourseId();
            refreshTable();
            lblStudentId.setText(StudentModel.getNextStudentID());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        studentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectRow();
            }
        });
        initializeDateTime();
    }

    private void refreshTable() {
        studentTable.getItems().clear();
        try {
            List<StudentDto> students = StudentModel.getAllStudents();
            studentTable.getItems().addAll(students);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void selectRow() {
        StudentDto selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            lblStudentId.setText(selectedStudent.getStudentId());
            lblAdminId.setText(selectedStudent.getAdminId());
            lblParentId.setText(selectedStudent.getParentId());
            lblStudentName.setText(selectedStudent.getName());
            lblStudentAddress.setText(selectedStudent.getAddress());
            lblStudentDOB.setText(new SimpleDateFormat("dd/MM/yyyy").format(selectedStudent.getDob()));
        }
    }

    private boolean validateInputs() {
        String namePattern = "^[A-Za-z\\s'-]{2,50}$";
        String addressPattern = "^[A-Za-z0-9\\s,.-]{5,100}$";
        String dobPattern = "^\\d{2}/\\d{2}/\\d{4}$";

        if (!lblStudentName.getText().matches(namePattern)) {
            new Alert(Alert.AlertType.ERROR, "Invalid name format!").show();
            return false;
        }
        if (!lblStudentAddress.getText().matches(addressPattern)) {
            new Alert(Alert.AlertType.ERROR, "Invalid address format!").show();
            return false;
        }
        if (!lblStudentDOB.getText().matches(dobPattern)) {
            new Alert(Alert.AlertType.ERROR, "Invalid date format! Use dd/MM/yyyy").show();
            return false;
        }
        return true;
    }
}
