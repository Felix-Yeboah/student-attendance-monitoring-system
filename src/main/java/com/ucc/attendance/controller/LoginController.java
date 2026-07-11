package com.ucc.attendance.controller;

import com.ucc.attendance.App;
import com.ucc.attendance.dao.UserDao;
import com.ucc.attendance.model.User;
import com.ucc.attendance.security.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Controls the login screen and starts a user session after successful login.
 */
public class LoginController {

    private final UserDao userDao = new UserDao();

    private Stage primaryStage;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessageLabel;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            loginMessageLabel.setText("Enter both username and password.");
            return;
        }

        Optional<User> authenticatedUser = userDao.authenticate(username, password);

        if (authenticatedUser.isEmpty()) {
            loginMessageLabel.setText("Invalid username or password.");
            return;
        }

        SessionManager.login(authenticatedUser.get());
        openMainApplication();
    }

    private void openMainApplication() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/com/ucc/attendance/fxml/main-view.fxml")
            );

            Pane root = loader.load();

            Scene scene = new Scene(
                    root,
                    primaryStage.getScene().getWidth(),
                    primaryStage.getScene().getHeight()
            );

            scene.getStylesheets().add(Objects.requireNonNull(
                    App.class.getResource("/com/ucc/attendance/css/app.css")
            ).toExternalForm());

            primaryStage.setTitle("Student Attendance Monitoring System");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();

        } catch (IOException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("The main application could not be opened.");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }
    }
}