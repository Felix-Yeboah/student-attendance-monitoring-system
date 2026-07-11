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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Controls login and password reset interactions.
 */
public class LoginController {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserDao userDao = new UserDao();

    private Stage primaryStage;

    @FXML private VBox loginFormBox;
    @FXML private VBox resetPasswordBox;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label loginMessageLabel;

    @FXML private TextField resetUsernameField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label resetMessageLabel;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        clearMessage(loginMessageLabel);

        if (username.isBlank() || password.isBlank()) {
            showErrorMessage(loginMessageLabel, "Enter both username and password.");
            return;
        }

        Optional<User> authenticatedUser = userDao.authenticate(username, password);

        if (authenticatedUser.isEmpty()) {
            showErrorMessage(loginMessageLabel, "Invalid username or password.");
            return;
        }

        SessionManager.login(authenticatedUser.get());
        openMainApplication();
    }

    @FXML
    private void showResetForm() {
        clearMessage(loginMessageLabel);
        clearMessage(resetMessageLabel);

        resetUsernameField.setText(usernameField.getText().trim());

        loginFormBox.setVisible(false);
        loginFormBox.setManaged(false);

        resetPasswordBox.setVisible(true);
        resetPasswordBox.setManaged(true);
    }

    @FXML
    private void showLoginForm() {
        clearMessage(loginMessageLabel);
        clearMessage(resetMessageLabel);

        resetPasswordBox.setVisible(false);
        resetPasswordBox.setManaged(false);

        loginFormBox.setVisible(true);
        loginFormBox.setManaged(true);
    }

    @FXML
    private void handlePasswordReset() {
        String username = resetUsernameField.getText().trim();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        clearMessage(resetMessageLabel);

        if (username.isBlank() || currentPassword.isBlank()
                || newPassword.isBlank() || confirmPassword.isBlank()) {
            showErrorMessage(resetMessageLabel, "All password reset fields are required.");
            return;
        }

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            showErrorMessage(resetMessageLabel, "New password must be at least 8 characters.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showErrorMessage(resetMessageLabel, "New password and confirmation do not match.");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            showErrorMessage(resetMessageLabel, "New password must be different from the current password.");
            return;
        }

        boolean passwordChanged = userDao.resetPassword(username, currentPassword, newPassword);

        if (!passwordChanged) {
            showErrorMessage(resetMessageLabel, "Username or current password is incorrect.");
            return;
        }

        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();

        usernameField.setText(username);
        passwordField.clear();

        showLoginForm();
        showSuccessMessage(loginMessageLabel, "Password changed successfully. Please login again.");
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

    private void showErrorMessage(Label label, String message) {
        label.setStyle("-fx-text-fill: #dc2626;");
        label.setText(message);
    }

    private void showSuccessMessage(Label label, String message) {
        label.setStyle("-fx-text-fill: #16a34a;");
        label.setText(message);
    }

    private void clearMessage(Label label) {
        label.setText("");
        label.setStyle("");
    }
}