package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import unieventhub.dao.*;
import unieventhub.model.*;
import unieventhub.util.EventCategory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class AdminEventFormController {

    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker datePicker;
    @FXML private TextField txtStartTime;
    @FXML private TextField txtEndTime;
    @FXML private ComboBox<EventCategory> comboCategory;
    @FXML private ComboBox<Integer> comboRoom;
    @FXML private TextField txtPrice;
    @FXML private ComboBox<String> comboRegType;
    @FXML private ComboBox<String> comboFood;
    @FXML private TextField txtSeatLimit;
    @FXML private ComboBox<String> comboBuilding;
    @FXML private TextField txtOrganizerClub;
    @FXML private HBox priceLabelBox;
    @FXML private Button btnSubmit;

    private final EventDAO eventDAO = new EventDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final BuildingDAO buildingDAO = new BuildingDAO();
    private final OrganizerDAO organizerDAO = new OrganizerDAO();

    private Event eventToEdit;
    private Runnable refreshCallback;
    private final Map<String, Integer> buildingMap = new HashMap<>();

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public void setEvent(Event event) {
        this.eventToEdit = event;
        populateFields();
    }

    @FXML
    public void initialize() {
        comboRegType.getItems().addAll("Free", "Paid");
        comboFood.getItems().addAll("Yes", "No");
        comboCategory.getItems().setAll(EventCategory.values());

        txtPrice.setVisible(false);
        txtPrice.setManaged(false);
        priceLabelBox.setVisible(false);
        priceLabelBox.setManaged(false);

        comboRegType.setOnAction(e -> {
            boolean isPaid = "Paid".equalsIgnoreCase(comboRegType.getValue());
            txtPrice.setDisable(!isPaid);
            txtPrice.setVisible(true);
            txtPrice.setManaged(true);
            priceLabelBox.setVisible(true);
            priceLabelBox.setManaged(true);
            txtPrice.setText(isPaid ? "" : "0.0");
        });

        comboRoom.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer roomNumber) {
                return (roomNumber != null) ? "Room " + roomNumber : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string.replace("Room ", "").trim());
            }
        });

        for (Building b : buildingDAO.getAllBuildings()) {
            comboBuilding.getItems().add(b.getName());
            buildingMap.put(b.getName(), b.getId());
        }

        comboBuilding.setOnAction(e -> populateRooms());
    }

    private void populateFields() {
        txtName.setText(eventToEdit.getName());
        txtDescription.setText(eventToEdit.getDescription());
        datePicker.setValue(eventToEdit.getDate());
        txtStartTime.setText(eventToEdit.getStartTime().toString());
        txtEndTime.setText(eventToEdit.getEndTime().toString());
        comboCategory.setValue(eventToEdit.getCategory());
        comboRegType.setValue(eventToEdit.getRegistrationType());
        comboFood.setValue(eventToEdit.getFoodAvailability());
        txtSeatLimit.setText(String.valueOf(eventToEdit.getSeatLimit()));
        txtPrice.setText(String.valueOf(eventToEdit.getPrice()));
        txtOrganizerClub.setText(eventToEdit.getOrganizerClub());

        String building = eventToEdit.getBuildingName();
        comboBuilding.setValue(building);
        populateRooms();

        Room room = roomDAO.getRoomById(eventToEdit.getRoomId());
        if (room != null) {
            comboRoom.setValue(room.getRoomNumber());
        }
    }

    private void populateRooms() {
        comboRoom.getItems().clear();
        Integer buildingId = buildingMap.get(comboBuilding.getValue());
        if (buildingId != null) {
            for (Room room : roomDAO.getRoomsByBuildingId(buildingId)) {
                comboRoom.getItems().add(room.getRoomNumber());
            }
        }
    }

    @FXML
    private void onSubmit() {
        try {
            String name = txtName.getText().trim();
            String description = txtDescription.getText().trim();
            LocalDate date = datePicker.getValue();
            String startStr = txtStartTime.getText().trim();
            String endStr = txtEndTime.getText().trim();
            EventCategory category = comboCategory.getValue();
            String regType = comboRegType.getValue();
            String food = comboFood.getValue();
            String priceStr = txtPrice.getText().trim();
            String seatStr = txtSeatLimit.getText().trim();
            String clubName = txtOrganizerClub.getText().trim();

            if (name.isEmpty() || date == null ||
                startStr.isEmpty() || endStr.isEmpty() || category == null ||
                regType == null || food == null || seatStr.isEmpty() ||
                clubName.isEmpty() || comboBuilding.getValue() == null || comboRoom.getValue() == null) {
            	showAlert(Alert.AlertType.ERROR, "All fields marked * are required.");
                return;
            }

            LocalTime startTime = LocalTime.parse(startStr);
            LocalTime endTime = LocalTime.parse(endStr);
            if (!startTime.isBefore(endTime)) {
            	showAlert(Alert.AlertType.WARNING, "Start time must be before end time.");
                return;
            }

            int seatLimit = Integer.parseInt(seatStr);
            double price = ("Free".equalsIgnoreCase(regType)) ? 0.0 : Double.parseDouble(priceStr);

            int buildingId = buildingMap.get(comboBuilding.getValue());
            int roomNumber = comboRoom.getValue();
            int roomId = roomDAO.getRoomIdByNumberAndBuilding(roomNumber, buildingId);
            int roomCapacity = roomDAO.getRoomById(roomId).getCapacity();

            if (seatLimit > roomCapacity) {
            	showAlert(Alert.AlertType.ERROR, "Seat limit exceeds room capacity.");
                return;
            }

            Integer organizerId = organizerDAO.getOrganizerIdByClub(clubName);
            if (organizerId == null) {
            	showAlert(Alert.AlertType.ERROR, "Invalid club name. No such organizer found.");
                return;
            }

            Event event = new Event(name, description, date, startTime, endTime, category,
                    organizerId, roomId, price, regType, food, seatLimit);
            event.setOrganizerClub(clubName);
            if (eventToEdit != null) {
                event.setId(eventToEdit.getId());
            }

            boolean roomAvailable = eventToEdit != null
                    ? eventDAO.isRoomAvailable(roomId, date, startTime, endTime, event.getId())
                    : eventDAO.isRoomAvailable(roomId, date, startTime, endTime);

            if (!roomAvailable) {
            	showAlert(Alert.AlertType.WARNING, "Room is not available at the selected time.");
                return;
            }

            boolean success = (eventToEdit == null)
                    ? eventDAO.createEventByAdmin(event)
                    : eventDAO.updateEvent(event);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, eventToEdit != null ? "Event updated successfully." : "Event created successfully.");

                if (refreshCallback != null) refreshCallback.run();
                close();
            } else {
            	showAlert(Alert.AlertType.ERROR, "Failed to save event.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.WARNING, "Please correct invalid inputs.");
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.showAndWait();
    }

}
