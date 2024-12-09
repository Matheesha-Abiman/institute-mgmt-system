package com.example.institute1.controller;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import com.example.institute1.service.Service;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class WelcomeController extends Application {

    @FXML
    private Label lblPercentage;

    @FXML
    private Rectangle rctMain;

    @FXML
    private Rectangle rctSub;

    public void initialize()  {
        Service.LoadingThread task = new Service.LoadingThread();
        task.progressProperty().addListener((observable, oldValue, newValue) -> {
            String percentage = String.format("%.0f", newValue.doubleValue() * 100);
            lblPercentage.setText(percentage + " % ");
            rctSub.setWidth(rctMain.getWidth() * newValue.doubleValue());

            if (newValue.doubleValue() == 1.0) {
                Window window = rctMain.getScene().getWindow();
                Stage stage = (Stage) window;
                stage.close();

                try {
                    start(new Stage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        new Thread(task).start();
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Institute Management System");
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
