package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import unieventhub.dao.EventDAO;
import unieventhub.dao.RegistrationDAO;
import unieventhub.model.Event;
import unieventhub.util.LogOut; // make sure this matches your package


import java.util.List;
import java.util.Optional;

import javafx.util.Pair; 

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.time.format.DateTimeFormatter;
import java.util.List;

import unieventhub.util.CardPaymentDialog;
import unieventhub.util.EventCategory;
import unieventhub.util.StudentCardValidator;



public class StudentHomeController {
	@FXML private ComboBox<String> categoryFilter;
	@FXML private ComboBox<String> priceFilter;
	@FXML private DatePicker dateFilter;
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> nameCol;
    @FXML private TableColumn<Event, String> dateCol;
    @FXML private TableColumn<Event, String> startTimeCol;
    @FXML private TableColumn<Event, String> endTimeCol;
    @FXML private TableColumn<Event, String> roomIdCol;
    @FXML private TableColumn<Event, String> categoryCol;
    @FXML private TableColumn<Event, String> buildingNameCol;
    @FXML private TableColumn<Event, String> priceTypeCol;
    @FXML private TableColumn<Event, String> statusCol;
    @FXML private TableColumn<Event, String> priceCol; //  change from Double to String
    @FXML  private MenuButton profileMenu;
    @FXML private Label lblUserName;
    
    

    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    private int studentId;
    private String studentFirstName;

    public void initialize() {
        // Initialize columns
    	eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        startTimeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartTime().toString()));
        endTimeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEndTime().toString()));
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory().name()));
        buildingNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBuildingName()));
        priceTypeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrice() == 0.0 ? "Free" : "Paid"));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isRegistered() ? "Registered" : "Yet to be Registered"));
        priceCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("$%.2f", data.getValue().getPrice())));
        roomIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoomName()));

        // Populate filter options
        for (EventCategory category : EventCategory.values()) {
            categoryFilter.getItems().add(category.name());
        }
        categoryFilter.getItems().add(0, "All");
        priceFilter.getItems().addAll("All", "Free", "Paid");
        categoryFilter.setValue("All");
        priceFilter.setValue("All");

        // Add listeners
        categoryFilter.setOnAction(e -> applyFilters());
        priceFilter.setOnAction(e -> applyFilters());
        dateFilter.setOnAction(e -> applyFilters());
        //to disable past dates selection
        dateFilter.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(java.time.LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #999999;");
                }
            }
        });

    }
               
    
    private void applyFilters() {
    	List<Event> allEvents = eventDAO.getAllEvents().stream()
    		    .filter(event -> {
    		        LocalDate today = LocalDate.now();
    		        LocalTime now = LocalTime.now();
    		        return event.getDate().isAfter(today) ||
    		               (event.getDate().isEqual(today) && event.getEndTime().isAfter(now));
    		    })
    		    .toList();

        List<Event> registeredEvents = registrationDAO.getRegisteredEventsByStudentId(studentId);

        // Mark registration status
        for (Event event : allEvents) {
            boolean isRegistered = registeredEvents.stream()
                    .anyMatch(reg -> reg.getId() == event.getId());
            event.setRegistered(isRegistered);
        }

        // Apply filters
        String selectedCategory = categoryFilter.getValue() != null ? categoryFilter.getValue() : "All";
        String selectedPrice = priceFilter.getValue() != null ? priceFilter.getValue() : "All";
        java.time.LocalDate selectedDate = dateFilter.getValue();

        List<Event> filtered = allEvents.stream().filter(event -> {
            boolean matchesCategory = selectedCategory.equals("All") || event.getCategory().name().equals(selectedCategory);
            boolean matchesPrice = selectedPrice.equals("All") ||
                    (selectedPrice.equals("Free") && event.getPrice() == 0.0) ||
                    (selectedPrice.equals("Paid") && event.getPrice() > 0.0);
            boolean matchesDate = selectedDate == null || event.getDate().isEqual(selectedDate);

            return matchesCategory && matchesPrice && matchesDate;
        }).toList();

        eventTable.setItems(FXCollections.observableArrayList(filtered));
    }

    public void setStudent(int id, String firstName) {
        this.studentId = id;
        this.studentFirstName = firstName;
        if (lblUserName != null) {
            lblUserName.setText("Hi, " + firstName);
        }
        loadEvents();
    }

    private void loadEvents() {
        // 1. Get all events
    	List<Event> allEvents = eventDAO.getAllEvents().stream()
    		    .filter(event -> {
    		        LocalDate today = LocalDate.now();
    		        LocalTime now = LocalTime.now();
    		        return event.getDate().isAfter(today) ||
    		               (event.getDate().isEqual(today) && event.getEndTime().isAfter(now));
    		    })
    		    .toList();

        // 2. Get the events this student has registered for (from DB)
        List<Event> registeredEvents = registrationDAO.getRegisteredEventsByStudentId(studentId);

        // 3. Mark which ones are registered
        for (Event event : allEvents) {
            boolean isRegistered = registeredEvents.stream()
                    .anyMatch(reg -> reg.getId() == event.getId());
            event.setRegistered(isRegistered);
        }

        // 4. Load into table
        ObservableList<Event> observableEvents = FXCollections.observableArrayList(allEvents);
        eventTable.setItems(observableEvents);
    }
    public void reloadEvents() {
        loadEvents();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleRegisterSelectedEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();

        if (selectedEvent == null) {
            showAlert("No Selection", "Please select an event to register.");
            return;
        }

        if (selectedEvent.isRegistered()) {
            showAlert("Already Registered", "You have already registered for this event.");
            return;
        }
        
     // Check if event is full
        int currentRegistrations = registrationDAO.getRegistrationCountForEvent(selectedEvent.getId());
        int seatLimit = selectedEvent.getSeatLimit();
        if (seatLimit > 0 && currentRegistrations >= seatLimit) {
            showAlert("Seats Full", "This event is fully booked. You cannot register.");
            return;
        }


        //  Check for time conflict with already registered events
        List<Event> registeredEvents = registrationDAO.getRegisteredEventsByStudentId(studentId);
        for (Event regEvent : registeredEvents) {
            if (regEvent.getDate().equals(selectedEvent.getDate())) {
                boolean isOverlap = selectedEvent.getStartTime().isBefore(regEvent.getEndTime())
                        && regEvent.getStartTime().isBefore(selectedEvent.getEndTime());
                if (isOverlap) {
                    showAlert("Time Conflict", " You are already registered for another event at this time.");
                    return;
                }
            }
        }

        //  Proceed if no conflict
        if (selectedEvent.getPrice() == 0.0) {
            boolean success = registrationDAO.registerStudentToEvent(studentId, selectedEvent.getId());
            if (success) {
                selectedEvent.setRegistered(true);
                eventTable.refresh();  // Refresh UI
                showAlert("Success", "You have been registered successfully.");
            } else {
                showAlert("Error", "Registration failed. Please try again.");
            }
        } else {
            Optional<Pair<String, String>> result = CardPaymentDialog.show();

            if (result.isPresent()) {
                boolean success = registrationDAO.registerStudentToEvent(studentId, selectedEvent.getId());
                if (success) {
                    selectedEvent.setRegistered(true);
                    eventTable.refresh();
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/PaymentSuccess.fxml"));
                        Scene scene = new Scene(loader.load());
                        Stage stage = new Stage();
                        stage.setTitle("Payment Confirmation");
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    showAlert("Registration Error", 
                        "Payment succeeded, but registration failed.\nPlease try again.");
                }
            } else {
            	try {
            	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/PaymentError.fxml"));
            	    Scene scene = new Scene(loader.load());
            	    Stage stage = new Stage();
            	    stage.setTitle("Payment Error");
            	    stage.setScene(scene);
            	    stage.show();
            	} catch (IOException e) {
            	    e.printStackTrace();
            	}
            }
        }
    } 
    
    @FXML
    private void handleViewMyEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/ViewMyEvents.fxml"));
            Scene scene = new Scene(loader.load());

            ViewMyEventsController controller = loader.getController();
            controller.setStudent(studentId, studentFirstName);

            Stage stage = (Stage) eventTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("My Registered Events");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open My Events page.");
        }
      
    }
    
    @FXML
    private void handleDeregisterSelectedEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();

        if (selectedEvent == null) {
            showAlert("No Selection", "Please select an event to de-register.");
            return;
        }

        if (!selectedEvent.isRegistered()) {
            showAlert("Not Registered", " You are not registered for this event.");
            return;
        }

        boolean success = registrationDAO.deregisterStudentFromEvent(studentId, selectedEvent.getId());
        if (success) {
            selectedEvent.setRegistered(false);
            eventTable.refresh();

            if (selectedEvent.getPrice() == 0.0) {
                showAlert("De-Registered", " You have been de-registered from the free event.\nYou may register again anytime.");
            } else {
                showAlert("De-Registered", "You have been de-registered.\nYour refund will be reflected in your account within 5 business days.");
            }
        } else {
            showAlert("Error", " De-registration failed. Please try again.");
        }
    }
    @FXML
    private void handleViewAnalytics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/StudentAnalytics.fxml"));
            Scene scene = new Scene(loader.load());

            StudentAnalyticsController controller = loader.getController();
            controller.setStudent(studentId, studentFirstName);

            Stage stage = (Stage) eventTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Event Analytics");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open Analytics page.");
        }
    }
    @FXML
    private void onLogout() {
        Stage currentStage = (Stage) profileMenu.getScene().getWindow(); 
        LogOut.logout(currentStage);
    }
    
    
}
    
    

   