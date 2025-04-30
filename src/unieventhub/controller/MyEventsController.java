package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import unieventhub.dao.*;
import unieventhub.model.Event;
import unieventhub.model.User;
import unieventhub.util.EventCategory;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import unieventhub.dao.EventDAO;


public class MyEventsController{

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> colName;
    @FXML private TableColumn<Event, String> colDescription;
    @FXML private TableColumn<Event, LocalDate> colDate;
    @FXML private TableColumn<Event, LocalTime> colStartTime;
    @FXML private TableColumn<Event, LocalTime> colEndTime;
    @FXML private TableColumn<Event, String> colCategory;
    @FXML private TableColumn<Event, String> colLocation;
    @FXML private TableColumn<Event, String> colRegistrationType;
    @FXML private TableColumn<Event, Double> colPrice;
    @FXML private TableColumn<Event, String> colFoodAvailability;
    @FXML private TableColumn<Event, Integer> colSeatLimit;
    @FXML private TableColumn<Event, Void> colRegistrations;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> priceFilter;
    @FXML private DatePicker dateFilter;

    private EventAction eventDAO = new EventDAO();  // Or inject it if you use dependency injection
    private User organizer;
    private String organizerUsername;

    public void initializeOrganizerDashboard(User organizer) {
        this.organizer = organizer;
        organizerUsername = organizer.getUsername();

       
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
colDescription.setCellFactory(column -> new TableCell<>() {
    private final Label label = new Label();

    @Override
    protected void updateItem(String description, boolean empty) {
        super.updateItem(description, empty);

        if (empty || description == null || description.trim().isEmpty()) {
            setText(null);
            setGraphic(null);
        } else {
            String shortText = description.length() > 30 ? description.substring(0, 30) + "..." : description;
            label.setText(shortText);

            Tooltip tooltip = new Tooltip(description);
            Tooltip.install(label, tooltip);

            setGraphic(label);
        }
    }
});

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colLocation.setCellValueFactory(cellData -> new SimpleStringProperty(
        	    cellData.getValue().getBuildingName() + ", Room " + cellData.getValue().getRoomName()
        	));
        	colRegistrationType.setCellValueFactory(new PropertyValueFactory<>("registrationType"));
        	colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        	colFoodAvailability.setCellValueFactory(new PropertyValueFactory<>("foodAvailability"));
        	colSeatLimit.setCellValueFactory(new PropertyValueFactory<>("seatLimit"));
        	colRegistrations.setCellFactory(col -> new TableCell<>() {
        	    private final Hyperlink link = new Hyperlink("View");

        	    {
        	        link.setOnAction(e -> {
        	            Event event = getTableView().getItems().get(getIndex());
        	            showRegistrationsPopup(event);
        	        });
        	    }
        	    @Override
        	    protected void updateItem(Void item, boolean empty) {
        	        super.updateItem(item, empty);
        	        setGraphic(empty ? null : link);
        	    }
        	});
        	
        	eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        	categoryFilter.getItems().add("All");
        	for (EventCategory cat : EventCategory.values()) {
        	    categoryFilter.getItems().add(cat.name());
        	}
        	categoryFilter.setValue("All");

        	priceFilter.getItems().addAll("All", "Free", "Paid");
        	priceFilter.setValue("All");

        	dateFilter.setDayCellFactory(picker -> new DateCell() {
        	    @Override
        	    public void updateItem(LocalDate date, boolean empty) {
        	        super.updateItem(date, empty);
        	        if (date.isBefore(LocalDate.now())) {
        	            setDisable(true);
        	            setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #999999;");
        	        }
        	    }
        	});

        	categoryFilter.setOnAction(e -> applyFilters());
        	priceFilter.setOnAction(e -> applyFilters());
        	dateFilter.setOnAction(e -> applyFilters());

        loadEvents();
    }
    
    private void applyFilters() {
        var allEvents = eventDAO.getEventsByOrganizer(organizerUsername);

        String selectedCategory = categoryFilter.getValue() != null ? categoryFilter.getValue() : "All";
        String selectedPrice = priceFilter.getValue() != null ? priceFilter.getValue() : "All";
        LocalDate selectedDate = dateFilter.getValue();

        var filtered = allEvents.stream().filter(event -> {
            boolean matchesCategory = selectedCategory.equals("All") || event.getCategory().name().equals(selectedCategory);
            boolean matchesPrice = selectedPrice.equals("All") ||
                    (selectedPrice.equals("Free") && event.getPrice() == 0.0) ||
                    (selectedPrice.equals("Paid") && event.getPrice() > 0.0);
            boolean matchesDate = selectedDate == null || event.getDate().isEqual(selectedDate);

            return matchesCategory && matchesPrice && matchesDate;
        }).toList();

        eventTable.setItems(FXCollections.observableArrayList(filtered));
    }


    private void loadEvents() {
        var events = eventDAO.getEventsByOrganizer(organizerUsername);
        System.out.println("Loaded " + events.size() + " events for: " + organizer.getUsername());
        for (Event e : events) {
            System.out.println("  -> " + e.getName() + " on " + e.getDate() + " in room ID " + e.getRoomId());
        }
        eventTable.setItems(FXCollections.observableArrayList(
        		eventDAO.getEventsByOrganizer(organizerUsername)
        	));

    }


    @FXML
    private void onAddEvent() {
        loadEventForm(null, "Add Event");
    }

    @FXML
    private void onUpdateEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Event Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an event to update.");
            alert.showAndWait();
            return;
        }
        //cant update past events
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (selectedEvent.getDate().isBefore(today) ||
           (selectedEvent.getDate().isEqual(today) && selectedEvent.getEndTime().isBefore(now))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Past Event");
            alert.setHeaderText(null);
            alert.setContentText("Past events cannot be updated.");
            alert.showAndWait();
            return;
        }

        loadEventForm(selectedEvent, "Update Event");

    }

    @FXML
    public void onDeleteEvent(ActionEvent event) {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Event Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an event to delete.");
            alert.showAndWait();
            return;
        }

        // Confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this event?");
        confirmAlert.setContentText("Event: " + selectedEvent.getName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Proceed with deletion
            boolean deleted = eventDAO.deleteEventById(selectedEvent.getId());
            if (deleted) {
                eventTable.getItems().remove(selectedEvent);
                System.out.println("Event deleted: " + selectedEvent.getName());
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Deletion Failed");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Could not delete the event. Please try again.");
                errorAlert.showAndWait();
            }
        }
    }


    private void loadEventForm(Event eventToEdit, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/AddEvent.fxml"));
            Parent root = loader.load();
            EventController controller = loader.getController();
            controller.setOrganizer(organizer);

            if (eventToEdit != null) controller.setEventToEdit(eventToEdit);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


private void showRegistrationsPopup(Event event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/RegistrationsPopup.fxml"));
        Parent root = loader.load();

        RegistrationsPopupController controller = loader.getController();
        controller.setEvent(event);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Registrations for " + event.getName());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}

