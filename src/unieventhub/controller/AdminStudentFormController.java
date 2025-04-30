package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import unieventhub.dao.StudentDAO;
import unieventhub.model.Student;
import unieventhub.util.CollegeCourseMap;

public class AdminStudentFormController {

    @FXML private Label lblNeuid;
    @FXML private Label lblFirstName;
    @FXML private Label lblLastName;
    @FXML private Label lblEmail;
    @FXML private Label lblContact;
    @FXML private Label lblCollege;
    @FXML private Label lblCourse;

    @FXML private TextField txtNeuid;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtContact;
    @FXML private ComboBox<String> comboCollege;
    @FXML private ComboBox<String> comboCourse;

    private Student student;

    public void setStudent(Student student) {
        this.student = student;
        if (student != null) {
            txtNeuid.setText(student.getNeuid());
            txtFirstName.setText(student.getFirstName());
            txtLastName.setText(student.getLastName());
            txtEmail.setText(student.getEmail());
            txtContact.setText(student.getContactNumber());
            comboCollege.setValue(student.getCollege());
            comboCourse.setValue(student.getCourse());
        }
    }

    @FXML
    public void initialize() {
        comboCollege.getItems().addAll(CollegeCourseMap.collegeCourses.keySet());
        comboCollege.setOnAction(e -> {
            String selectedCollege = comboCollege.getValue();
            comboCourse.getItems().clear();
            if (selectedCollege != null) {
                comboCourse.getItems().addAll(CollegeCourseMap.collegeCourses.get(selectedCollege));
            }
        });
    }

    @FXML
    private void onSaveClick() {
    	String neuId = txtNeuid.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContact.getText().trim();
        String college = comboCollege.getValue();
        String course = comboCourse.getValue();

        if (neuId.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || contact.isEmpty() || college == null || course == null) {
            showAlert("Validation Error", "Please fill all fields.", Alert.AlertType.WARNING);
            return;
        }

        if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
            showAlert("Validation Error", "First and Last names must contain only letters.", Alert.AlertType.WARNING);
            return;
        }

        if (!neuId.matches("\\d{9}")) {
            showAlert("Validation Error", "NEUID must be 9 digits.", Alert.AlertType.WARNING);
            return;
        }

        if (!contact.matches("\\d{10}")) {
            showAlert("Validation Error", "Contact Number must be 10 digits.", Alert.AlertType.WARNING);
            return;
        }

        // Pre-check for duplicates
        if (!student.getNeuid().equals(neuId) && StudentDAO.isNeuidExists(neuId)) {
            showAlert("Duplicate NEUID", "A student with this NEUID already exists.", Alert.AlertType.ERROR);
            return;
        }

        if (!student.getEmail().equals(email) && StudentDAO.isEmailExists(email)) {
            showAlert("Duplicate Email", "A student with this email already exists.", Alert.AlertType.ERROR);
            return;
        }

        student.setNeuid(neuId);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setContactNumber(contact);
        student.setCollege(college);
        student.setCourse(course);

        boolean success = StudentDAO.updateStudent(student);

        if (success) {
            closeWindow();
        } else {
            showAlert("Error", "Could not update student.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCancelClick() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtNeuid.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
