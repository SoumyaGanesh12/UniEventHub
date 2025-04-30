package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import unieventhub.dao.OrganizerDAO;
import unieventhub.model.Organizer;

public class AdminOrganizerFormController {

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtContact;
    @FXML private TextField txtClub;

    private Organizer organizer;
    private Runnable refreshCallback;

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
        if (organizer != null) {
            txtFirstName.setText(organizer.getFirstName());
            txtLastName.setText(organizer.getLastName());
            txtEmail.setText(organizer.getEmail());
            txtContact.setText(organizer.getContactNumber());
            txtClub.setText(organizer.getClub());
        }
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void onSaveClick() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContact.getText().trim();
        String club = txtClub.getText().trim();

        // Empty check
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || contact.isEmpty() || club.isEmpty()) {
            showAlert("Validation Error", "Please fill all fields.", Alert.AlertType.WARNING);
            return;
        }

        // Validation
        if (!firstName.matches("[a-zA-Z]+")) {
            showAlert("Invalid First Name", "First name should contain only letters.", Alert.AlertType.WARNING);
            return;
        }

        if (!lastName.matches("[a-zA-Z]+")) {
            showAlert("Invalid Last Name", "Last name should contain only letters.", Alert.AlertType.WARNING);
            return;
        }

        if (!contact.matches("\\d{10}")) {
            showAlert("Invalid Contact Number", "Contact number must be exactly 10 digits.", Alert.AlertType.WARNING);
            return;
        }

        if (!club.matches("[a-zA-Z0-9 -&@*#!]+")) {
            showAlert("Invalid Club Name", "Club name can only contain letters, digits, and special characters - & @ * # !", Alert.AlertType.WARNING);
            return;
        }

        if (!email.equals(organizer.getEmail()) && OrganizerDAO.isEmailExists(email)) {
            showAlert("Duplicate Email", "A user with this email already exists.", Alert.AlertType.ERROR);
            return;
        }

        // Save updates
        organizer.setFirstName(firstName);
        organizer.setLastName(lastName);
        organizer.setEmail(email);
        organizer.setContactNumber(contact);
        organizer.setClub(club);

        boolean success = OrganizerDAO.updateOrganizer(organizer);
        if (success) {
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            closeWindow();
        } else {
            showAlert("Error", "Could not update organizer.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCancelClick() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtFirstName.getScene().getWindow();
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
