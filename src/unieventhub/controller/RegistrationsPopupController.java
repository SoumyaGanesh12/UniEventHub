package unieventhub.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import unieventhub.dao.RegistrationDAO;
import unieventhub.model.Event;
import unieventhub.model.Registration;
import unieventhub.model.User;
import unieventhub.util.EmailSender;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class RegistrationsPopupController {

    @FXML private Label lblEventName;
    @FXML private TableColumn<Registration, String> colUsername;
    @FXML private TableColumn<Registration, String> colEmail;
    @FXML private Label lblTotal;
    @FXML private Button btnClose;
    @FXML private TableView<Registration> registrationsTable;
    @FXML private Button btnSendReminders;
    @FXML private Label lblReminderMessage;


    private Event currentEvent; // Needed to personalize reminder email

    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    @FXML
    private void onSendReminders(ActionEvent actionEvent) {
        if (currentEvent == null) {
            System.out.println("currentEvent is null — setEvent() was not called!");
            return;
        }

        String subject = "Reminder: " + currentEvent.getName();
        String bodyTemplate = """
                Dear Student,

                This is a reminder that you're registered for the event:

                Date: %s
                Time: %s to %s
                Location: %s, Room %s

                Please be on time.

                Regards,
                UniEventHub Organizer
                """;

        String body = String.format(
            bodyTemplate,
            currentEvent.getDate(),
            currentEvent.getStartTime(),
            currentEvent.getEndTime(),
            currentEvent.getBuildingName(),
            currentEvent.getRoomName()
        );

        for (Registration reg : registrationsTable.getItems()) {
            String email = reg.getStudentEmail();
            if (email == null || email.trim().isEmpty()) {
                System.out.println("Skipped: Missing email for student ID " + reg.getStudentId());
                continue;
            }
            EmailSender.sendEmail(email, subject, body);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Emails Sent");
        alert.setHeaderText(null);
        alert.setContentText("Reminders sent to all registered students.");
        alert.showAndWait();
    }

    /**
     * Initializes the popup with event data and registrations list.
     */
    public void setEvent(Event event) {
        this.currentEvent = event; // Important: set currentEvent so we can use it in reminder emails

        lblEventName.setText("Registrations for " + event.getName());
     // Disable send reminders for past events
        boolean isPast = event.getDate().isBefore(LocalDate.now()) ||
                (event.getDate().isEqual(LocalDate.now()) && event.getEndTime().isBefore(LocalTime.now()));

        btnSendReminders.setDisable(isPast);
        lblReminderMessage.setVisible(isPast);

        colUsername.setCellValueFactory(cellData -> {
            String firstName = Optional.ofNullable(cellData.getValue().getStudentFirstName()).orElse("");
            String lastName = Optional.ofNullable(cellData.getValue().getStudentLastName()).orElse("");
            String fullName = (firstName + " " + lastName).trim();
            if (fullName.isEmpty()) fullName = "(Name Unavailable)";
            return new SimpleStringProperty(fullName);
        });



        colEmail.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));

        // Fetch registered users from DAO
        List<User> studentUsers = registrationDAO.getRegisteredStudents(event.getId());

        // Convert to List<Registration> with email and username
        List<Registration> registrations = studentUsers.stream().map(user -> {
            Registration reg = new Registration();
            reg.setStudentId(user.getId());
           // reg.setStudentUsername(user.getUsername());
            reg.setStudentFirstName(user.getFirstName());
            reg.setStudentLastName(user.getLastName());
            reg.setStudentEmail(user.getEmail());
            return reg;
        }).toList();

        // Set in TableView
        registrationsTable.setItems(FXCollections.observableArrayList(registrations));
        lblTotal.setText("Total Registrations: " + registrations.size());
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage) btnClose.getScene().getWindow()).close();
    }
}
