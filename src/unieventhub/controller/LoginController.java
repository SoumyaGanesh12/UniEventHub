package unieventhub.controller;
 
import unieventhub.dao.UserDAO;
import unieventhub.model.User;
 
import java.io.IOException;
import java.net.URL;
 
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
 
public class LoginController {
 
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Hyperlink hyplinkFpwd;
    @FXML private Button btnSignup;
 
    private final UserDAO userDAO = new UserDAO();
 
    @FXML
    public void onLoginClick() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
 
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Please enter username and password.");
            return;
        }
 
        User user = userDAO.getUserByUsernameAndPassword(username, password);
        if (user != null) {
            System.out.println("Login successful for: " + user.getUsername());
 
            try {
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                Scene scene;
 
                if (user.getRole().equalsIgnoreCase("Organizer")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/OrganizerDashboard.fxml"));
                    scene = new Scene(loader.load());
                    unieventhub.controller.OrganizerDashboardController controller = loader.getController();
                    controller.initializeOrganizerDashboard(user);
 
                    stage.setTitle("Organizer Dashboard");
 
                } else if (user.getRole().equalsIgnoreCase("Admin")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/AdminDashboard.fxml"));
                    scene = new Scene(loader.load());
                    unieventhub.controller.AdminDashboardController controller = loader.getController();
                    controller.setAdmin(user);
                    stage.setTitle("Admin Dashboard");
 
                }
                else if (user.getRole().equalsIgnoreCase("Student")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/StudentHome.fxml"));
                    scene = new Scene(loader.load());
                    unieventhub.controller.StudentHomeController controller = loader.getController();
                    controller.setStudent(user.getId(), user.getFirstName());
                    stage.setTitle("Student Dashboard");
 
                }
                
                
                else {
                	
                    showAlert("Unauthorized Access", "Only admins and organizers can login.");
                    return;
                }
 
                stage.setScene(scene);
 
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Navigation Error", "Failed to load the dashboard.");
            }
 
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }
 
    @FXML
    public void onSignUpClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/Signup.fxml"));
            Scene scene = new Scene(loader.load());
 
			Stage stage = (Stage) btnSignup.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sign Up");
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void onForgotPasswordClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/ForgotPassword.fxml"));
            Scene scene = new Scene(loader.load());
 
            Stage stage = (Stage) hyplinkFpwd.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Reset Password");
 
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Unable to open the forgot password screen.");
        }
    }
 
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
 