package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import unieventhub.model.User;

import java.io.IOException;

public class AdminDashboardController {

    @FXML private MenuButton profileMenu;
    @FXML private Label lblUserName;
    @FXML private AnchorPane contentArea;
    @FXML private Hyperlink hypTdEvents;

    private User admin;

    public void setAdmin(User admin) {
        this.admin = admin;
        lblUserName.setText("Hi, " + admin.getFirstName());
        loadCenterView("/unieventhub/view/AdminEventsToday.fxml");
    }

    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) profileMenu.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load login screen.");
        }
    }
    
    @FXML
    private void onTodayEventsClick() {
        loadCenterView("/unieventhub/view/AdminEventsToday.fxml");
    }

    @FXML private void onEventsClick() { loadCenterView("/unieventhub/view/AdminListEvents.fxml"); }
    @FXML private void onStudentsClick() { loadCenterView("/unieventhub/view/AdminListStudents.fxml"); }
    @FXML private void onOrganizersClick() { loadCenterView("/unieventhub/view/AdminListOrganizers.fxml"); }
    @FXML private void onBuildingsClick() { loadCenterView("/unieventhub/view/AdminListBuildings.fxml"); }
    @FXML private void onAnalyticsClick() { loadCenterView("/unieventhub/view/AdminAnalytics.fxml"); }

//    private void loadCenterView(String fxmlPath) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
//            Parent root = loader.load();
//            contentArea.getChildren().clear();
//            contentArea.getChildren().add(root);
//        } catch (IOException e) {
//            e.printStackTrace();
//            showError("Failed to load view: " + fxmlPath);
//        }
//    }
    

    // Responsive design 
    private void loadCenterView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            ScrollPane scrollPane = new ScrollPane(view);
            scrollPane.setFitToWidth(true); // Ensures child uses available width
            scrollPane.setFitToHeight(true);
            scrollPane.setStyle("-fx-background-color: transparent;");

            contentArea.getChildren().setAll(scrollPane);

            AnchorPane.setTopAnchor(scrollPane, 0.0);
            AnchorPane.setRightAnchor(scrollPane, 0.0);
            AnchorPane.setBottomAnchor(scrollPane, 0.0);
            AnchorPane.setLeftAnchor(scrollPane, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load view: " + fxmlPath);
        }
    }


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
