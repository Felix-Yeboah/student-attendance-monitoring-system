package com.ucc.attendance;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import com.ucc.attendance.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX entry point. It prepares the database, loads the FXML user interface,
 * applies the CSS stylesheet, and displays the primary application window.
 */
public class App extends Application {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseInitializer.initialize();

            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/com/ucc/attendance/fxml/main-view.fxml")
            );
            Pane root = loader.load();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            double preferredWidth = Math.min(1280, screenBounds.getWidth() * 0.92);
            double preferredHeight = Math.min(820, screenBounds.getHeight() * 0.88);

            Scene scene = new Scene(root, preferredWidth, preferredHeight);
            scene.getStylesheets().add(Objects.requireNonNull(
                    App.class.getResource("/com/ucc/attendance/css/app.css")
            ).toExternalForm());

            primaryStage.setTitle("Student Attendance Monitoring System");
            primaryStage.setMinWidth(980);
            primaryStage.setMinHeight(650);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException | RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "The application could not start.", exception);
            showStartupError(exception);
        }
    }

    private void showStartupError(Exception exception) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Startup Error");
        alert.setHeaderText("The application could not connect to MySQL or load its interface.");
        alert.setContentText("Check that MySQL is running and that database.properties has valid credentials.\n\n"
                + "Technical detail: " + exception.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
