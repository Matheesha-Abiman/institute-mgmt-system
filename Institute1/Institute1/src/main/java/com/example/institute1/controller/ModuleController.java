package com.example.institute1.controller;

import com.example.institute1.dto.ModuleDto;
import com.example.institute1.model.ModuleModel;
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

public class ModuleController implements Initializable {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<ModuleDto, String> colCourseId;

    @FXML
    private TableColumn<ModuleDto, String> colModuleId;

    @FXML
    private TableColumn<ModuleDto, String> colModuleName;

    @FXML
    private TextField lblCourseId;

    @FXML
    private Label lblDateTime;

    @FXML
    private Label lblModuleId;

    @FXML
    private TextField lblModuleName;

    @FXML
    private TableView<ModuleDto> moduleTable;

    @FXML
    void moduleSave(ActionEvent event) {
        try {
            if (validateModuleInputs()) {
                ModuleDto moduleDto = new ModuleDto(
                        lblModuleId.getText(),
                        lblCourseId.getText(),
                        lblModuleName.getText()
                );

                if (ModuleModel.saveModule(moduleDto)) {
                    new Alert(Alert.AlertType.INFORMATION, "Module Saved").show();
                    refreshTable();
                    lblModuleId.setText(ModuleModel.getNextModuleID());
                    ModuleReset(null);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Module save failed").show();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void moduleUpdate(ActionEvent event) {
        ModuleDto selectedModule = moduleTable.getSelectionModel().getSelectedItem();
        if (selectedModule != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update this module?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                ModuleDto updatedModuleDto = new ModuleDto(
                        lblModuleId.getText(),
                        lblCourseId.getText(),
                        lblModuleName.getText()
                );

                try {
                    if (ModuleModel.updateModule(updatedModuleDto)) {
                        new Alert(Alert.AlertType.INFORMATION, "Module updated successfully").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Module update failed").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "No Module selected for update").show();
        }
    }

    @FXML
    void moduleDelete(ActionEvent event) {
        ModuleDto selectedModule = moduleTable.getSelectionModel().getSelectedItem();
        if (selectedModule != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this module?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                try {
                    if (ModuleModel.deleteModule(selectedModule.getModuleId())) {
                        new Alert(Alert.AlertType.INFORMATION, "Module deleted successfully.").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete Module.").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No module selected for deletion.").show();
        }
    }

    @FXML
    void ModuleReset(ActionEvent event) {
        lblModuleId.setText("");
        lblCourseId.clear();
        lblModuleName.clear();
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
        colModuleId.setCellValueFactory(new PropertyValueFactory<>("moduleId"));
        colCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colModuleName.setCellValueFactory(new PropertyValueFactory<>("moduleName"));

        try {
            refreshTable();
            lblModuleId.setText(ModuleModel.getNextModuleID());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        moduleTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectRow();
            }
        });

        initializeDateTime();
    }

    private void selectRow() {
        ModuleDto selectedModule = moduleTable.getSelectionModel().getSelectedItem();
        if (selectedModule != null) {
            lblModuleId.setText(selectedModule.getModuleId());
            lblCourseId.setText(selectedModule.getCourseId());
            lblModuleName.setText(selectedModule.getModuleName());
        }
    }

    private void refreshTable() {
        try {
            List<ModuleDto> modules = ModuleModel.getAllModules();
            moduleTable.setItems(FXCollections.observableArrayList(modules));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean validateModuleInputs() {
        String idPattern = "^[A-Za-z0-9]{2,10}$"; // Pattern for module ID
        String namePattern = "^[A-Za-z\\s'-]{2,50}$"; // Pattern for module name
        String courseIdPattern = "^[A-Za-z0-9]{2,10}$"; // Pattern for course ID

        String moduleId = lblModuleId.getText();
        String courseId = lblCourseId.getText();
        String moduleName = lblModuleName.getText();

        boolean isValidModuleId = moduleId.matches(idPattern);
        boolean isValidCourseId = courseId.matches(courseIdPattern);
        boolean isValidModuleName = moduleName.matches(namePattern);

        lblModuleId.setStyle(isValidModuleId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblCourseId.setStyle(isValidCourseId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblModuleName.setStyle(isValidModuleName ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");

        if (!isValidModuleId) new Alert(Alert.AlertType.WARNING, "Invalid Module ID.").show();
        if (!isValidCourseId) new Alert(Alert.AlertType.WARNING, "Invalid Course ID.").show();
        if (!isValidModuleName) new Alert(Alert.AlertType.WARNING, "Invalid Module Name.").show();

        return isValidModuleId && isValidCourseId && isValidModuleName;
    }
}
