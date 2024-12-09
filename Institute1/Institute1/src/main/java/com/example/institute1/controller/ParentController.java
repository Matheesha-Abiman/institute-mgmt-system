package com.example.institute1.controller;

import com.example.institute1.dto.ParentDto;
import com.example.institute1.model.ParentModel;
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

public class ParentController implements Initializable {

    @FXML
    private Button btnDelete, btnReset, btnSave, btnUpdate;

    @FXML
    private TableColumn<ParentDto, String> colAdminId, colParentEmail, colParentId, colParentName;

    @FXML
    private TextField lblAdminId, lblParentEmail, lblParentName;

    @FXML
    private Label lblParentId, lblError;

    @FXML
    private TableView<ParentDto> parentTable;

    @FXML
    private Label lblDateTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colParentId.setCellValueFactory(new PropertyValueFactory<>("parentId"));
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        colParentName.setCellValueFactory(new PropertyValueFactory<>("parentName"));
        colParentEmail.setCellValueFactory(new PropertyValueFactory<>("parentEmail"));

        try {
            refreshTable();
            lblParentId.setText(ParentModel.getNextParentID());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        parentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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

    public void refreshTable() {
        try {
            List<ParentDto> parents = ParentModel.getAllParents();
            parentTable.setItems(FXCollections.observableArrayList(parents));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void parentSave(ActionEvent event) {
        try {
            if (validateParentInputs()) {
                ParentDto parentDto = new ParentDto(
                        lblParentId.getText(),
                        lblAdminId.getText(),
                        lblParentName.getText(),
                        lblParentEmail.getText()
                );

                if (ParentModel.saveParent(parentDto)) {
                    new Alert(Alert.AlertType.INFORMATION, "Parent saved successfully").show();
                    refreshTable();
                    lblParentId.setText(ParentModel.getNextParentID());
                    parentReset(null);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Parent save failed").show();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void parentUpdate(ActionEvent event) {
        ParentDto selectedParent = parentTable.getSelectionModel().getSelectedItem();
        if (selectedParent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update this parent?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                ParentDto updatedParentDto = new ParentDto(
                        lblParentId.getText(),
                        lblAdminId.getText(),
                        lblParentName.getText(),
                        lblParentEmail.getText()
                );

                try {
                    if (ParentModel.updateParent(updatedParentDto)) {
                        new Alert(Alert.AlertType.INFORMATION, "Parent updated successfully").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Parent update failed").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "No parent selected for update").show();
        }
    }

    @FXML
    void parentDelete(ActionEvent event) {
        ParentDto selectedParent = parentTable.getSelectionModel().getSelectedItem();
        if (selectedParent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this parent?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
                try {
                    if (ParentModel.deleteParent(selectedParent.getParentId())) {
                        new Alert(Alert.AlertType.INFORMATION, "Parent deleted successfully.").show();
                        refreshTable();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete Parent.").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "No parent selected for deletion.").show();
        }
    }

    @FXML
    void parentReset(ActionEvent event) {
        lblParentId.setText("");
        lblAdminId.clear();
        lblParentName.clear();
        lblParentEmail.clear();
    }

    @FXML
    void selectRow() {
        ParentDto selectedParent = parentTable.getSelectionModel().getSelectedItem();
        if (selectedParent != null) {
            lblParentId.setText(selectedParent.getParentId());
            lblAdminId.setText(selectedParent.getAdminId());
            lblParentName.setText(selectedParent.getParentName());
            lblParentEmail.setText(selectedParent.getParentEmail());
        }
    }

    private boolean validateParentInputs() {
        String idPattern = "^[A-Za-z0-9]{2,10}$";
        String namePattern = "^(Mr\\.|Mrs\\.|Miss\\.|Ms\\.|Dr\\.)?\\s?[A-Za-z\\s'-]{2,50}$";
        String emailPattern = "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,3}$";

        boolean isValidParentId = lblParentId.getText().matches(idPattern);
        boolean isValidAdminId = lblAdminId.getText().matches(idPattern);
        boolean isValidParentName = lblParentName.getText().matches(namePattern);
        boolean isValidParentEmail = lblParentEmail.getText().matches(emailPattern);

        lblParentId.setStyle(isValidParentId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblAdminId.setStyle(isValidAdminId ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblParentName.setStyle(isValidParentName ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");
        lblParentEmail.setStyle(isValidParentEmail ? "-fx-border-color: #7367F0;" : "-fx-border-color: red;");

        if (!isValidParentId) new Alert(Alert.AlertType.WARNING, "Invalid Parent ID.").show();
        if (!isValidAdminId) new Alert(Alert.AlertType.WARNING, "Invalid Admin ID.").show();
        if (!isValidParentName) new Alert(Alert.AlertType.WARNING, "Invalid name.").show();
        if (!isValidParentEmail) new Alert(Alert.AlertType.WARNING, "Invalid email format.").show();

        return isValidParentId && isValidAdminId && isValidParentName && isValidParentEmail;
    }
}
