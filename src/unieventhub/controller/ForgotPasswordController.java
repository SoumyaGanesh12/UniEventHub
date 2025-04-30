package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import unieventhub.dao.UserDAO;

import java.io.IOException;

public class ForgotPasswordController {

    @FXML private TextField txtUname;
    @FXML private PasswordField txtPswd;
    @FXML private PasswordField txtCfPswd;
    @FXML private Label lblErrorFDPwd;
    @FXML private Label lblErrorFDCnfPwd;

    @FXML
    private void onSubmitClick() {
        String username = txtUname.getText().trim();
        String password = txtPswd.getText();
        String confirm = txtCfPswd.getText();

        // Reset error labels
        lblErrorFDPwd.setVisible(false);
        lblErrorFDCnfPwd.setVisible(false);

        boolean valid = true;

        // Password length validation
        if (password.length() < 6) {
            lblErrorFDPwd.setText("Password must be at least 6 characters!");
            lblErrorFDPwd.setVisible(true);
            valid = false;
        }

        // Confirm password validation
        if (!password.equals(confirm)) {
            lblErrorFDCnfPwd.setText("Passwords do not match.");
            lblErrorFDCnfPwd.setVisible(true);
            valid = false;
        }

        if (!valid) return;

        // Proceed to update
        boolean updated = UserDAO.updatePassword(username, password);
        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully.");
            goToLogin();
        } else {
            lblErrorFDCnfPwd.setText("Username not found or update failed.");
            lblErrorFDCnfPwd.setVisible(true);
        }
    }
    
    @FXML
    public void onLoginClick() {
        goToLogin();
    }

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) txtUname.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
