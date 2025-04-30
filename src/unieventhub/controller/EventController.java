package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import unieventhub.dao.*;
import unieventhub.model.*;
import unieventhub.util.EventCategory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Class: AddEventController
 * This JavaFX controller handles the form to add or update an event.
 * It supports features like room availability validation, seat limit check,
 * pre-filling event data during update, and uses Action interfaces to
 * decouple logic from data access implementations.
 */
public class EventController {

    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker datePicker;
    @FXML private TextField txtStartTime;
    @FXML private TextField txtEndTime;
    @FXML private ComboBox<EventCategory> comboCategory;
    @FXML private ComboBox<String> comboRoom;
    @FXML private TextField txtPrice;
    @FXML private ComboBox<String> comboRegType;
    @FXML private ComboBox<String> comboFood;
    @FXML private TextField txtSeatLimit;
    @FXML private ComboBox<String> comboBuilding;
    @FXML private Label lblPrice;
    @FXML private HBox priceLabelBox;
    @FXML private Button btnSubmit;

    private final EventAction eventAction = new EventDAO(); // Interface usage
    private final RoomAction roomAction = new RoomDAO();     // Interface usage
    private final BuildingAction buildingAction = new BuildingDAO(); // Interface usage
    private Map<String, Integer> buildingNameToId = new HashMap<>(); // Map for fast lookup
    private List<Building> buildings;

    private String organizerUsername;
    private Event eventToEdit; // Null unless we're updating an event
    
    private User organizer;
    /**
     * Sets the logged-in organizer username.
     */

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
        this.organizerUsername = organizer.getUsername();  
    }

    /**
     * Initializes the form: loads dropdowns, disables past dates, sets logic for Free/Paid price fields.
     */
    @FXML
    public void initialize() {
        comboRegType.getItems().addAll("Free", "Paid");
        comboFood.getItems().addAll("Yes", "No");

        txtPrice.setVisible(false);
        txtPrice.setManaged(false);
        priceLabelBox.setVisible(false);
        priceLabelBox.setManaged(false);

        comboRegType.setOnAction(e -> {
            String regType = comboRegType.getValue();
            txtPrice.setVisible(true);
            txtPrice.setManaged(true);
            priceLabelBox.setVisible(true);
            priceLabelBox.setManaged(true);

            if ("Free".equals(regType)) {
                txtPrice.setText("0.0");
                txtPrice.setDisable(true);
            } else {
                txtPrice.setDisable(false);
                txtPrice.clear();
            }
        });
        comboCategory.getItems().setAll(EventCategory.values());


        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        buildings = buildingAction.getAllBuildings();
        for (Building b : buildings) {
            comboBuilding.getItems().add(b.getName());
            buildingNameToId.put(b.getName(), b.getId());
        }

        comboBuilding.setOnAction(e -> populateRooms(comboBuilding.getValue()));
    }

    /**
     * Called when user clicks "Submit". Validates all fields and either creates or updates an event.
     */
    
    @FXML
    private void onSubmit() {
        try {
            StringBuilder errorMessages = new StringBuilder();

            // Gather input values
            String name = txtName.getText().trim();
            LocalDate date = datePicker.getValue();
            String description = txtDescription.getText();
            String startStr = txtStartTime.getText().trim();
            String endStr = txtEndTime.getText().trim();
            EventCategory category = comboCategory.getValue();
            String selectedBuilding = comboBuilding.getValue();
            String selectedRoom = comboRoom.getValue();
            String regType = comboRegType.getValue();
            String priceStr = txtPrice.getText().trim();
            String seatLimitStr = txtSeatLimit.getText().trim();
            String food = comboFood.getValue();

            // VALIDATION: check each required field and accumulate messages
            if (name.isEmpty()) errorMessages.append("- Event Name is required.\n");
            if (date == null) errorMessages.append("- Date is required.\n");
            if (startStr.isEmpty()) errorMessages.append("- Start Time is required.\n");
            if (endStr.isEmpty()) errorMessages.append("- End Time is required.\n");
            if (category == null) errorMessages.append("- Category is required.\n");
            if (selectedBuilding == null) errorMessages.append("- Building is required.\n");
            if (selectedRoom == null) errorMessages.append("- Room is required.\n");
            if (regType == null) errorMessages.append("- Registration Type is required.\n");
            if (!"Free".equalsIgnoreCase(regType) && priceStr.isEmpty()) {
                errorMessages.append("- Price is required for paid events.\n");
            }
            if (seatLimitStr.isEmpty()) errorMessages.append("- Seat Limit is required.\n");

            if (!errorMessages.isEmpty()) {
                showAlert("Please fix the following:\n" + errorMessages);
                return;
            }

         // Numeric validation            
            int seats;      
            try {
                seats = Integer.parseInt(seatLimitStr);
                if (seats <= 0) {
                    showAlert("Seat limit must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid seat limit. Please enter a valid number.");
                return;
            }

            double price = 0.0;
            if (!"Free".equalsIgnoreCase(regType)) {
                try {
                    price = Double.parseDouble(priceStr);
                    if (price < 0) {
                        showAlert("Price cannot be negative.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid price. Please enter a valid number.");
                    return;
                }
            }

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime start;
            LocalTime end;
            
            try {
                start = LocalTime.parse(startStr, timeFormatter);
            } catch (DateTimeParseException e) {
                showAlert("Invalid start time format. Please use HH:mm (e.g., 09:30 or 14:05).");
                return;
            }

            try {
                end = LocalTime.parse(endStr, timeFormatter);
            } catch (DateTimeParseException e) {
                showAlert("Invalid end time format. Please use HH:mm (e.g., 09:30 or 14:05).");
                return;
            }

            if (!start.isBefore(end)) {
                showAlert("Start time must be before end time.");
                return;
            }
            
            if (date.equals(LocalDate.now())) {
                LocalTime now = LocalTime.now();
                if (start.isBefore(now)) {
                    showAlert("Start time must be in the future for today's date.");
                    return;
                }
            }


            int buildingId = buildingNameToId.get(selectedBuilding);
            int roomNumber = Integer.parseInt(selectedRoom);
            int roomId = roomAction.getRoomIdByNumberAndBuilding(roomNumber, buildingId);
            Room room = roomAction.getRoomById(roomId);

            if (seats > room.getCapacity()) {
                showAlert(Alert.AlertType.ERROR, "Seat limit exceeds room capacity of " + room.getCapacity());
                return;
            }

            Event event = new Event(name, description, date, start, end, category,
                    organizer.getId(), roomId, price, regType, food, seats);
            event.setOrganizer(organizer.getUsername()); // Set organizer username for display and UI logic
            if (eventToEdit != null) 
            	event.setId(eventToEdit.getId());

            boolean available = (eventToEdit != null)
                    ? ((EventDAO) eventAction).isRoomAvailable(roomId, date, start, end, event.getId())
                    : ((EventDAO) eventAction).isRoomAvailable(roomId, date, start, end);

            if (!available) {
                showAlert("Room not available. Try a different time or room.");
                return;
            }

            boolean success = (eventToEdit != null)
                    ? eventAction.updateEvent(event)
                    : eventAction.createEvent(event);

            if (success) {
                showAlert(eventToEdit != null ? "Event updated successfully." : "Event created successfully.");
                closeWindow();
            } else {
                showAlert("Failed to create event.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Invalid input. Please check the formats and try again.");
        }
    }

    /**
     * Closes the event popup form.
     */
    @FXML
    private void onCancel() {
        closeWindow();
    }

    /**
     * Helper to close the JavaFX popup.
     */
    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }

    /**
     * Helper to show info alerts.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);

        DialogPane pane = alert.getDialogPane();
        pane.setStyle("-fx-background-color: #e0e0e0;");
        pane.lookup(".content.label").setStyle("-fx-text-fill: #991f36; -fx-font-size: 14px; -fx-font-weight: bold;");
        pane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: #A7C7E7; -fx-text-fill: black; -fx-font-weight: bold;");

        alert.showAndWait();
    }

    /**
     * Helper to show error/warning alerts with specific type.
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle("Validation Error");

        DialogPane pane = alert.getDialogPane();
        pane.setStyle("-fx-background-color: #e0e0e0;");
        pane.lookup(".content.label").setStyle("-fx-text-fill: #991f36; -fx-font-size: 14px; -fx-font-weight: bold;");
        pane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: #A7C7E7; -fx-text-fill: black; -fx-font-weight: bold;");

        alert.showAndWait();
    }

    /**
     * Pre-fills the form for updating an event.
     * @param event Event to update
     */
    public void setEventToEdit(Event event) {
        this.eventToEdit = event;

        txtName.setText(event.getName());
        txtDescription.setText(event.getDescription());
        datePicker.setValue(event.getDate());
        txtStartTime.setText(event.getStartTime().toString());
        txtEndTime.setText(event.getEndTime().toString());
        comboCategory.setValue(event.getCategory());
        comboRegType.setValue(event.getRegistrationType());
        comboFood.setValue(event.getFoodAvailability());

        txtPrice.setVisible(true);
        txtPrice.setManaged(true);
        priceLabelBox.setVisible(true);
        priceLabelBox.setManaged(true);

        if ("Free".equals(event.getRegistrationType())) {
            txtPrice.setText("0.0");
            txtPrice.setDisable(true);
        } else {
            txtPrice.setText(String.valueOf(event.getPrice()));
            txtPrice.setDisable(false);
        }

        txtSeatLimit.setText(String.valueOf(event.getSeatLimit()));

        Room room = roomAction.getRoomById(event.getRoomId());
        int buildingId = room.getBuildingId();
        String buildingName = buildingAction.getBuildingNameById(buildingId);

        populateRooms(buildingName);
        comboBuilding.setValue(buildingName);
        comboRoom.setValue(String.valueOf(room.getRoomNumber()));

        btnSubmit.setText("Update");
    }

    /**
     * Populates the room ComboBox based on selected building.
     */
    private void populateRooms(String buildingName) {
        comboRoom.getItems().clear();
        int buildingId = buildingNameToId.getOrDefault(buildingName, -1);
        if (buildingId == -1) return;

        List<Room> rooms = roomAction.getRoomsByBuildingId(buildingId);
        for (Room room : rooms) {
            comboRoom.getItems().add(String.valueOf(room.getRoomNumber()));
        }
    }
}
