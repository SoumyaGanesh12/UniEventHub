package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import unieventhub.model.User;

public class OrganizerDashboardController {

    @FXML private StackPane mainContentPane;
    @FXML private MenuButton profileMenu;
    private OrganizerAnalyticsController analyticsController;

    private User organizer;

    public void initializeOrganizerDashboard (User organizer) {
        this.organizer = organizer;
        profileMenu.setText("Hi, " + organizer.getFirstName());
        loadMyEventsView(); // Default view
    }

    @FXML
    private void loadMyEventsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/MyEvents.fxml"));
            Parent root = loader.load();
            MyEventsController controller = loader.getController();
            controller.initializeOrganizerDashboard(organizer);
            mainContentPane.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadAnalyticsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/OrganizerAnalytics.fxml"));
            Parent root = loader.load();
            OrganizerAnalyticsController controller = loader.getController();
            controller.initializeAnalyticsDashboard(organizer);
            mainContentPane.getChildren().setAll(root);
         // Save controller reference so we can refresh charts later
            this.analyticsController = controller;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Logs out the current user and navigates back to login screen.
     */
    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) profileMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load login screen.");
        }
    }

    /**
     * Displays an error ale̥rt with maroon background and blue button.
     * Applies custom stylesheet and dialog-pane style class.
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);

        DialogPane pane = alert.getDialogPane();
        pane.setStyle("-fx-background-color: #e0e0e0;");

        // Style the content text
        pane.lookup(".content.label").setStyle("-fx-text-fill: #991f36; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Style the buttons (OK, Close, etc.)
        pane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: #A7C7E7; -fx-text-fill: black; -fx-font-weight: bold;");

        alert.showAndWait();
    }  

    /**
     * Displays a success message with maroon background and blue button.
     * Applies custom stylesheet and dialog-pane style class.
     */
    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(msg);

        DialogPane pane = alert.getDialogPane();
        pane.setStyle("-fx-background-color: #e0e0e0;");

        pane.lookup(".content.label").setStyle("-fx-text-fill: #991f36; -fx-font-size: 14px; -fx-font-weight: bold;");
        pane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: #A7C7E7; -fx-text-fill: black; -fx-font-weight: bold;");

        alert.showAndWait();
    }

}

