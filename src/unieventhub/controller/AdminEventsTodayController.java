// AdminEventsTodayController.java
package unieventhub.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import unieventhub.dao.EventDAO;
import unieventhub.model.Event;

public class AdminEventsTodayController {

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> colEventName;
    @FXML private TableColumn<Event, String> colOrganizer;
    @FXML private TableColumn<Event, Integer> colStudents;
    @FXML private TableColumn<Event, String> colLocation;

    private final EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        setupTable();
        loadTodaysEvents();
    }

    private void setupTable() {
        colEventName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStudents.setCellValueFactory(new PropertyValueFactory<>("registrationCount")); 

        colOrganizer.setCellValueFactory(event ->
                new SimpleStringProperty(event.getValue().getDescription())); // Using description temporarily to hold organizer club

        colLocation.setCellValueFactory(event -> {
            String room = event.getValue().getRoomName();
            String building = event.getValue().getBuildingName();
            return new SimpleStringProperty(room + ", " + building);
        });
    }

    private void loadTodaysEvents() {
        ObservableList<Event> list = FXCollections.observableArrayList(eventDAO.getTodaysEventsWithClubAndReg());
        eventTable.setItems(list);
    }
}
