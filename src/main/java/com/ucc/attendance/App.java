package com.ucc.attendance;

import com.ucc.attendance.controller.LoginController;
import com.ucc.attendance.dao.UserDao;
import com.ucc.attendance.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX entry point. It prepares the database, loads the login interface,
 * applies the CSS stylesheet, and displays the primary application window.
 */
public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseInitializer.initialize();
            new UserDao().ensureDefaultUsers();

            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/com/ucc/attendance/fxml/login-view.fxml")
            );

            Pane root = loader.load();

            LoginController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            double preferredWidth = Math.min(1180, screenBounds.getWidth() * 0.88);
            double preferredHeight = Math.min(760, screenBounds.getHeight() * 0.84);

            Scene scene = new Scene(root, preferredWidth, preferredHeight);
            scene.getStylesheets().add(Objects.requireNonNull(
                    App.class.getResource("/com/ucc/attendance/css/app.css")
            ).toExternalForm());

            primaryStage.setTitle("Student Attendance Monitoring System - Login");
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(580);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Application startup failed.", exception);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Startup Error");
            alert.setHeaderText("The application could not connect to MySQL or load its interface.");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}