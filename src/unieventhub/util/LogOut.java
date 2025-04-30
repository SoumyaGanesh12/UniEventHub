package unieventhub.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LogOut {
    public static void logout(Stage currentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(LogOut.class.getResource("/unieventhub/view/login.fxml"));
            Parent root = loader.load();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}