package com.example.institute1.controller;

import com.example.institute1.dto.StudentCourseDto;
import com.example.institute1.model.StudentCourseModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StudentCourseController implements Initializable {

    @FXML
    private TableColumn<StudentCourseDto, String> colCourseId;

    @FXML
    private TableColumn<StudentCourseDto, String> colStudentId;

    @FXML
    private Label lblDateTime;

    @FXML
    private AnchorPane studentCoursePanel;

    @FXML
    private TableView<StudentCourseDto> tableStudentCourse;

    @FXML
    private PieChart pieChart;

    private ObservableList<PieChart.Data> pieChartData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));

        pieChartData = FXCollections.observableArrayList();
        pieChart.setData(pieChartData);

        refreshTable();
        refreshPieChart();
        initializeDateTime();
    }

    private void refreshTable() {
        try {
            List<StudentCourseDto> sCourses = StudentCourseModel.getAllStudentCourses();
            tableStudentCourse.setItems(FXCollections.observableArrayList(sCourses));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void refreshPieChart() {
        try {
            List<StudentCourseDto> sCourses = StudentCourseModel.getAllStudentCourses();
            Map<String, Integer> courseCountMap = new HashMap<>();

            // Count the number of students per course
            for (StudentCourseDto sCourse : sCourses) {
                courseCountMap.put(sCourse.getCourseId(), courseCountMap.getOrDefault(sCourse.getCourseId(), 0) + 1);
            }

            // Update PieChart data
            pieChartData.clear();
            for (Map.Entry<String, Integer> entry : courseCountMap.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            // Bind names and values dynamically for PieChart
            pieChartData.forEach(data ->
                    data.nameProperty().bind(
                            javafx.beans.binding.Bindings.concat(
                                    data.getName(), ": ", data.pieValueProperty().intValue(), " students"
                            )
                    )
            );
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
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
}
