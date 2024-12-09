package com.example.institute1.controller;

import com.example.institute1.dto.LectureDto;
import com.example.institute1.model.LectureModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class LectureController implements Initializable {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<?, ?> colLectureEmail;

    @FXML
    private TableColumn<?, ?> colLectureId;

    @FXML
    private TableColumn<?, ?> colLectureName;

    @FXML
    private TableColumn<?, ?> colModuleID;

    @FXML
    private TextField lblLectureEmail;

    @FXML
    private Label lblLectureId;

    @FXML
    private Label lblDateTime;

    @FXML
    private TextField lblLectureName;

    @FXML
    private TextField lblModuleId;

    @FXML
    private TableView<LectureDto> lectureTable;

    @FXML
    void lectureSave(ActionEvent event) {
        try {
            if (validateLectureInputs()) {
                LectureDto lectureDto = new LectureDto(
                        lblLectureId.getText(),
                        lblModuleId.getText(),
                        lblLectureName.getText(),
                        lblLectureEmail.getText()
                );

                if (LectureModel.saveLecture(lectureDto)) {
                    new Alert(Alert.AlertType.INFORMATION, "Lecture Saved").show();
                    refreshTable();
                    lblLectureId.setText(LectureModel.getNextLectureID());
                    lectureReset(null);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Lecture save failed").show();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void lectureUpdate(ActionEvent event) {
        LectureDto selectedLecture = lectureTable.getSelectionModel().getSelectedItem();
        if (selectedLecture != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update this Lecture?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                LectureDto updatedLectureDto = new LectureDto(
                        lblLectureId.getText(),
                        lblModuleId.getText(),
                        lblLectureName.getText(),
                        lblLectureEmail.getText()
                );

                try {
                    if (LectureModel.updateLecture(updatedLectureDto)) {
                        new Alert(Alert.AlertType.INFORMATION, "Lecture updated successfully.").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Lecture update failed.").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Database error occurred while updating.").show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No Lecture selected for update.").show();
        }
    }

    @FXML
    void lectureDelete(ActionEvent event) {
        LectureDto selectedLecture = lectureTable.getSelectionModel().getSelectedItem();
        if (selectedLecture != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this Lecture?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                try {
                    if (LectureModel.deleteLecture(selectedLecture.getLectureId())) {
                        new Alert(Alert.AlertType.INFORMATION, "Lecture deleted successfully.").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete Lecture.").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Database error occurred while deleting.").show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No Lecture selected for deletion.").show();
        }
    }

    @FXML
    void lectureReset(ActionEvent event) {
        lblLectureId.setText("");
        lblModuleId.clear();
        lblLectureName.clear();
        lblLectureEmail.clear();
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
        colLectureId.setCellValueFactory(new PropertyValueFactory<>("lectureId"));
        colModuleID.setCellValueFactory(new PropertyValueFactory<>("moduleId"));
        colLectureName.setCellValueFactory(new PropertyValueFactory<>("lectureName"));
        colLectureEmail.setCellValueFactory(new PropertyValueFactory<>("lectureEmail"));

        try {
            refreshTable();
            lblLectureId.setText(LectureModel.getNextLectureID());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        lectureTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectRow();
            }
        });
        initializeDateTime();
    }

    private void selectRow() {
        LectureDto lectureDto = lectureTable.getSelectionModel().getSelectedItem();
        if (lectureDto != null) {
            lblLectureId.setText(lectureDto.getLectureId());
            lblModuleId.setText(lectureDto.getModuleId());
            lblLectureName.setText(lectureDto.getLectureName());
            lblLectureEmail.setText(lectureDto.getLectureEmail());
        }
    }

    private void refreshTable() {
        try {
            List<LectureDto> lectures = LectureModel.getAllLectures();
            lectureTable.setItems(FXCollections.observableArrayList(lectures));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean validateLectureInputs() {
        String idPattern = "^[A-Za-z0-9]{2,10}$";
        String moduleIdPattern = "^[A-Za-z0-9]{2,10}$";
        String namePattern = "^(Mr\\.|Mrs\\.|Miss\\.|Ms\\.|Dr\\.)?\\s?[A-Za-z\\s'-]{2,50}$";
        String emailPattern = "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,3}$";

        String lectureId = lblLectureId.getText();
        String moduleId = lblModuleId.getText();
        String lectureName = lblLectureName.getText();
        String lectureEmail = lblLectureEmail.getText();

        boolean isValidLectureId = lectureId.matches(idPattern);
        boolean isValidModuleId = moduleId.matches(moduleIdPattern);
        boolean isValidLectureName = lectureName.matches(namePattern);
        boolean isValidLectureEmail = lectureEmail.matches(emailPattern);

        lblLectureId.setStyle(isValidLectureId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblModuleId.setStyle(isValidModuleId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblLectureName.setStyle(isValidLectureName ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblLectureEmail.setStyle(isValidLectureEmail ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");

        if (!isValidLectureId) new Alert(Alert.AlertType.WARNING, "Invalid Lecture ID.").show();
        if (!isValidModuleId) new Alert(Alert.AlertType.WARNING, "Invalid Module ID.").show();
        if (!isValidLectureName) new Alert(Alert.AlertType.WARNING, "Invalid name.").show();
        if (!isValidLectureEmail) new Alert(Alert.AlertType.WARNING, "Invalid email format.").show();

        return isValidLectureId && isValidModuleId && isValidLectureName && isValidLectureEmail;
    }
}
