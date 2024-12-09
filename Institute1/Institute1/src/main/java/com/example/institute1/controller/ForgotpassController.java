package com.example.institute1.controller;

import com.example.institute1.db.DBConnection;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.ResourceBundle;

public class ForgotpassController implements Initializable {

    @FXML
    private JFXTextField emailTf;

    @FXML
    private JFXButton forgotBtn;

    @FXML
    private Label lblDateTime;

    private static String generatedOTP;
    private static String inputEmail;

    @FXML
    void clickForgotBtn(ActionEvent event) {
        String email = emailTf.getText().trim();

        if (email.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter your email").show();
            return;
        }

        if (!isEmailRegistered(email)) {
            new Alert(Alert.AlertType.ERROR, "The email is not registered in the system.").show();
            return;
        }

        inputEmail = email;
        generatedOTP = generateOTP();

        Task<Void> sendEmailTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                sendEmail(email, generatedOTP);
                return null;
            }

            @Override
            protected void succeeded() {
                new Alert(Alert.AlertType.INFORMATION, "OTP sent to your email").show();
                navigateToResetPasswordScreen();
            }

            @Override
            protected void failed() {
                new Alert(Alert.AlertType.ERROR, "Failed to send OTP. Please try again.").show();
            }
        };

        Thread thread = new Thread(sendEmailTask);
        thread.setDaemon(true);
        thread.start();
    }

    private static String generateOTP() {
        SecureRandom random = new SecureRandom();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private void sendEmail(String to, String otp) throws MessagingException {

        // Replace "Your email" and "email key" with your actual email and app-specific password.
        final String FROM = "Your email";
        final String PASSWORD = "email key";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Password Reset OTP");

        // Styled email body
        final String body = "<html>" +
                "<body>" +
                "<p><span style='color:green; font-size:18px; font-weight:bold;'>OTP: " + otp + "</span></p>" +
                "<p><span style='color:red; font-size:16px;'>Please do not share this OTP with others.</span></p>" +
                "</body>" +
                "</html>";
        message.setContent(body, "text/html");
        Transport.send(message);
    }

    private boolean isEmailRegistered(String email) {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            String query = "SELECT email FROM Admin WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database connection error.").show();
            return false;
        }
    }

    private void navigateToResetPasswordScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/resetpass.fxml"));
            Parent root = loader.load();
            ResetpassController controller = loader.getController();
            controller.setEmailAndOTP(inputEmail, generatedOTP);

            Stage stage = (Stage) forgotBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load the Reset Password screen.").show();
        }
    }

    private void initializeDateTime() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            lblDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd || HH:mm:ss")));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeDateTime();
    }

    public void enterEmail(ActionEvent actionEvent) {

    }
}
