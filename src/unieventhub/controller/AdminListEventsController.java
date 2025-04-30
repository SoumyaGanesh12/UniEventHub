package unieventhub.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import unieventhub.dao.EventDAO;
import unieventhub.model.Event;

import java.util.List;

public class AdminListEventsController {

    @FXML private TableView<Event> tblEvents;
    @FXML private TableColumn<Event, String> colName;
//    @FXML private TableColumn<Event, String> colDescription;
    @FXML private TableColumn<Event, String> colDate;
    @FXML private TableColumn<Event, String> colStartTime;
    @FXML private TableColumn<Event, String> colEndTime;
    @FXML private TableColumn<Event, String> colLocation;
    @FXML private TableColumn<Event, Double> colPrice;
    @FXML private TableColumn<Event, String> colFoodAvailability;
    @FXML private TableColumn<Event, String> colRegSummary;
    @FXML private TableColumn<Event, String> colOrganizerClub;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private final ObservableList<Event> eventList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
//        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colOrganizerClub.setCellValueFactory(new PropertyValueFactory<>("organizerClub"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colLocation.setCellValueFactory(cellData -> {
            String location = cellData.getValue().getRoomName() + ", " + cellData.getValue().getBuildingName();
            return new javafx.beans.property.SimpleStringProperty(location);
        });
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colFoodAvailability.setCellValueFactory(new PropertyValueFactory<>("foodAvailability"));
        colRegSummary.setCellValueFactory(cellData -> {
            int reg = cellData.getValue().getRegistrationCount();
            int limit = cellData.getValue().getSeatLimit();
            return new javafx.beans.property.SimpleStringProperty(reg + "/" + limit);
        });

        loadEvents();
    }

    private void loadEvents() {
//        EventDAO dao = new EventDAO();
        List<Event> events = EventDAO.getAllEventsWithDetails();
        eventList.setAll(events);
        tblEvents.setItems(eventList);
    }

    @FXML
    private void onAddEvent() {
        openEventForm(null);
    }

    @FXML
    private void onUpdateEvent() {
        Event selected = tblEvents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an event to update.", Alert.AlertType.WARNING);
            return;
        }
        openEventForm(selected);
    }

    @FXML
    private void onDeleteEvent() {
        Event selected = tblEvents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an event to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this event?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                EventDAO dao = new EventDAO();
                boolean deleted = dao.deleteEventById(selected.getId());
                if (deleted) {
                    loadEvents();
                } else {
                    showAlert("Error", "Could not delete event.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void openEventForm(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/AdminEventForm.fxml"));
            Scene scene = new Scene(loader.load());

            AdminEventFormController controller = loader.getController();
            controller.setRefreshCallback(this::loadEvents);
            if (event != null) {
                controller.setEvent(event);
            }

            Stage stage = new Stage();
            stage.setTitle(event == null ? "Add Event" : "Update Event");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open event form.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
