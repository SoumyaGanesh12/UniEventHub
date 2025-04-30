package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import unieventhub.dao.OrganizerDAO;
import unieventhub.dao.StudentDAO;
import unieventhub.dao.UserDAO;
import unieventhub.model.Organizer;
import unieventhub.model.Student;
import unieventhub.model.User;
import unieventhub.util.CollegeCourseMap;

import java.io.IOException;
import java.util.List;

public class SignupController {

    @FXML private TextField txtFn;
    @FXML private TextField txtLn;
    @FXML private ComboBox<String> comboRole;
    @FXML private TextField txtEmailId;
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPwd;
    @FXML private TextField txtNeuid;
    @FXML private ComboBox<String> comboCollege;
    @FXML private ComboBox<String> comboCourse;
    @FXML private TextField txtContactNumber;
    @FXML private TextField txtClub;
    @FXML private Label lblNeuid;
    @FXML private Label lblCollege;
    @FXML private Label lblCourse;
    @FXML private Label lblContactNumber;
    @FXML private Label lblClub;
    @FXML private Label lblErrorFn;
    @FXML private Label lblErrorLn;
    @FXML private Label lblErrorMail;
    @FXML private Label lblErrorUser;
    @FXML private Label lblErrorPwd;
    @FXML private Label lblErrorNeuid;
    @FXML private Label lblErrorPhone;
    @FXML private Label lblErrorClub;
    @FXML private Label lblErrorCollege;
    @FXML private Label lblErrorCourse;

    @FXML
    public void initialize() {
        comboRole.getItems().addAll("Student", "Organizer");
        comboRole.setValue("Student");

        comboCollege.getItems().addAll(CollegeCourseMap.collegeCourses.keySet());
        comboCollege.setOnAction(e -> populateCourses());

        comboRole.setOnAction(e -> toggleRoleSpecificFields());

        // Set up field visibility correctly at the start
        toggleRoleSpecificFields();
    }

    private void toggleRoleSpecificFields() {
        String role = comboRole.getValue();

        boolean isStudent = "Student".equalsIgnoreCase(role);

        // Fields
        txtNeuid.setVisible(isStudent);
        comboCollege.setVisible(isStudent);
        comboCourse.setVisible(isStudent);
        txtClub.setVisible(!isStudent);
        txtContactNumber.setVisible(true); // Visible for both

        // Labels
        lblNeuid.setVisible(isStudent);
        lblCollege.setVisible(isStudent);
        lblCourse.setVisible(isStudent);
        lblClub.setVisible(!isStudent);
        lblContactNumber.setVisible(true);
        
        // Hide all organizer-related error labels when not organizer
    	lblErrorClub.setVisible(!isStudent);
    	
    	// Hide all student-related error labels when not student
        if (!isStudent) {
            lblErrorNeuid.setVisible(false);
            lblErrorCollege.setVisible(false);
            lblErrorCourse.setVisible(false);
        }
        
    }

    private void populateCourses() {
        String selectedCollege = comboCollege.getValue();
        if (selectedCollege != null) {
            comboCourse.setDisable(false);
            comboCourse.getItems().clear();
            List<String> courses = CollegeCourseMap.collegeCourses.get(selectedCollege);
            if (courses != null) {
                comboCourse.getItems().addAll(courses);
            }
        }
    }
    
    private boolean validateInputs() {
        boolean valid = true;

        // First Name
        if (!txtFn.getText().matches("[A-Za-z]+")) {
            lblErrorFn.setText("Only letters are allowed!");
            lblErrorFn.setVisible(true);
            valid = false;
        } else lblErrorFn.setVisible(false);

        // Last Name
        if (!txtLn.getText().matches("[A-Za-z]+")) {
            lblErrorLn.setText("Only letters are allowed!");
            lblErrorLn.setVisible(true);
            valid = false;
        } else lblErrorLn.setVisible(false);
        
        // Email
        if (!txtEmailId.getText().matches("^\\S+@\\S+\\.\\S+$")) {
        	lblErrorMail.setText("Invalid email address!");
        	lblErrorMail.setVisible(true);
            valid = false;
        } else lblErrorMail.setVisible(false);

        // Username
        if (!txtUser.getText().matches(".{6,}")) {
            lblErrorUser.setText("Username must be at least 6 characters!");
            lblErrorUser.setVisible(true);
            valid = false;
        } else lblErrorUser.setVisible(false);

        // Password
        if (!txtPwd.getText().matches(".{6,}")) {
            lblErrorPwd.setText("Password must be at least 6 characters!");
            lblErrorPwd.setVisible(true);
            valid = false;
        } else lblErrorPwd.setVisible(false);

        // If role is student
        if ("Student".equals(comboRole.getValue())) {
        	// Hide all organizer-related error labels when not organizer
        	lblErrorClub.setVisible(false);
        	
        	// NEU ID
            if (!txtNeuid.getText().matches("\\d{9}")) {
                lblErrorNeuid.setText("NEU ID must be 9 digits!");
                lblErrorNeuid.setVisible(true);
                valid = false;
            } else lblErrorNeuid.setVisible(false);
            
            // College
            if (comboCollege.getValue() == null || comboCollege.getValue().isEmpty()) {
                lblErrorCollege.setText("Please select a college!");
                lblErrorCollege.setVisible(true);
                valid = false;
            } else lblErrorCollege.setVisible(false);

            // Course
            if (comboCourse.getValue() == null || comboCourse.getValue().isEmpty()) {
                lblErrorCourse.setText("Please select a course!");
                lblErrorCourse.setVisible(true);
                valid = false;
            } else lblErrorCourse.setVisible(false);
        }
        
        // If role is organizer, check for Club
        if ("Organizer".equals(comboRole.getValue())) {
        	// Hide all student-related error labels when not student
        	lblErrorNeuid.setVisible(false);
            lblErrorCollege.setVisible(false);
            lblErrorCourse.setVisible(false);
            
            if (!txtClub.getText().matches("[a-zA-Z0-9 -&@*#!]+")) {
            	lblErrorClub.setText("Only letters, digits and special characters - & @ * # ! are allowed!");
            	lblErrorClub.setVisible(true);
                valid = false;
            } else lblErrorClub.setVisible(false);
        }

        // Phone number
        if (!txtContactNumber.getText().matches("\\d{10}")) {
            lblErrorPhone.setText("Phone number must be 10 digits!");
            lblErrorPhone.setVisible(true);
            valid = false;
        } else lblErrorPhone.setVisible(false);

        return valid;
    }

    
    @FXML
    private void onSubmitClick() {
        String firstName = txtFn.getText();
        String lastName = txtLn.getText();
        String role = comboRole.getValue();
        String email = txtEmailId.getText();
        String username = txtUser.getText();
        String password = txtPwd.getText();
        
        // Input Validation
        if (!validateInputs()) {
            return;
        }
        
        // Check for unique email
        if (UserDAO.isEmailTaken(txtEmailId.getText())) {
            lblErrorMail.setText("Email already registered!");
            lblErrorMail.setVisible(true);
            return;
        } else {
            lblErrorMail.setVisible(false);
        }
        
        // Check for unique username
        if (UserDAO.isUsernameTaken(txtUser.getText())) {
            lblErrorUser.setText("Username already exists!");
            lblErrorUser.setVisible(true);
            return;
        } else {
            lblErrorUser.setVisible(false);
        }

        boolean success = false;

        if ("Student".equalsIgnoreCase(role)) {
            String neuid = txtNeuid.getText();
            String college = comboCollege.getValue();
            String course = comboCourse.getValue();
            String contact = txtContactNumber.getText();

            Student student = new Student(firstName, lastName, email, username, password, role,
                    neuid, college, course, contact);
            //Check for duplicate NEU ID
            if (StudentDAO.isNeuidExists(txtNeuid.getText())) {
                lblErrorNeuid.setText("NEU ID already registered!");
                lblErrorNeuid.setVisible(true);
                return;
            } else {
                lblErrorNeuid.setVisible(false);
            }

            success = StudentDAO.insertStudent(student);

        } else if ("Organizer".equalsIgnoreCase(role)) {
            String club = txtClub.getText();
            String contact = txtContactNumber.getText();

            Organizer organizer = new Organizer(firstName, lastName, email, username, password, role,
                    club, contact);
            success = OrganizerDAO.insertOrganizer(organizer);
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
            goToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Registration failed.");
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
            Stage stage = (Stage) txtFn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
