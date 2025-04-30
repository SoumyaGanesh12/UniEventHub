package unieventhub.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import unieventhub.dao.RegistrationDAO;
import unieventhub.model.Event;

import java.io.IOException;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ViewMyEventsController {

    @FXML private TableView<Event> myEventsTable;
    @FXML private TableColumn<Event, String> nameCol;
    @FXML private TableColumn<Event, String> dateCol;
    @FXML private TableColumn<Event, String> startTimeCol;
    @FXML private TableColumn<Event, String> endTimeCol;
    @FXML private TableColumn<Event, String> roomCol;
    @FXML private TableColumn<Event, String> buildingCol;
    @FXML private Label welcomeLabel;

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private int studentId;
    private String studentFirstName;

    public void setStudent(int id, String firstName) {
        this.studentId = id;
        this.studentFirstName = firstName;
        loadRegisteredEvents();
        
    }

    private void loadRegisteredEvents() {
        List<Event> events = registrationDAO.getRegisteredEventsByStudentId(studentId);
        ObservableList<Event> observableEvents = FXCollections.observableArrayList(events);
        myEventsTable.setItems(observableEvents);
    }

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        startTimeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartTime().toString()));
        endTimeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEndTime().toString()));
        roomCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoomName()));
        buildingCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBuildingName()));
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/StudentHome.fxml"));
            Scene scene = new Scene(loader.load());

            // Transfer student info and refresh events
            StudentHomeController controller = loader.getController();
            controller.setStudent(studentId, studentFirstName);
            controller.reloadEvents(); //  This is important

            Stage stage = (Stage) myEventsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Student Dashboard");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}