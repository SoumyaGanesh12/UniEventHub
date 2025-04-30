package unieventhub.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import unieventhub.dao.RoomDAO;
import unieventhub.model.Building;
import unieventhub.model.Room;

public class AdminRoomManagerController {

    @FXML private TableView<Room> tblRooms;
    @FXML private TableColumn<Room, Integer> colRoomNumber;
    @FXML private TableColumn<Room, Integer> colCapacity;

    @FXML private TextField txtRoomNumber;
    @FXML private TextField txtCapacity;
    @FXML private Label lblBuildingName;

    private final ObservableList<Room> roomList = FXCollections.observableArrayList();
    private Building building;
    private int buildingId;

    public void setBuilding(Building building) {
        this.building = building;
        this.buildingId = building.getId();
        lblBuildingName.setText(building.getName());
        loadRooms();
    }

    private void loadRooms() {
        RoomDAO roomDAO = new RoomDAO();
        roomList.setAll(roomDAO.getRoomsByBuildingId(building.getId()));
        tblRooms.setItems(roomList);
    }

    @FXML
    private void initialize() {
        colRoomNumber.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getRoomNumber()).asObject());
        colCapacity.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()).asObject());
    }

    @FXML
    private void onAddRoom() {
        String roomNumberText = txtRoomNumber.getText().trim();
        String capacityText = txtCapacity.getText().trim();

        if (!isValidInteger(roomNumberText) || !isValidInteger(capacityText)) {
            showAlert("Validation Error", "Room number and capacity must be valid numbers.", Alert.AlertType.WARNING);
            return;
        }

        int roomNumber = Integer.parseInt(roomNumberText);
        int capacity = Integer.parseInt(capacityText);

        // Check if room already exists in this building
        RoomDAO roomDAO = new RoomDAO();
        if (roomDAO.getRoomIdByNumberAndBuilding(roomNumber, buildingId) != -1) {
            showAlert("Duplicate Room", "Room number already exists in this building.", Alert.AlertType.ERROR);
            return;
        }
        
        // Check if max rooms allowed is achieved
        if (roomList.size() >= building.getMaxRoomsAllowed()) {
            showAlert("Room Limit Reached", "Cannot add more rooms. Maximum allowed: " + building.getMaxRoomsAllowed(), Alert.AlertType.WARNING);
            return;
        }

        Room room = new Room(roomNumber, capacity, building.getId());
        boolean success = RoomDAO.addRoom(room);
        if (success) {
            loadRooms();
            clearFields();
        } else {
            showAlert("Error", "Could not add room. Make sure room number is unique.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onUpdateRoom() {
        Room selected = tblRooms.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a room to update.", Alert.AlertType.WARNING);
            return;
        }

        String roomNumberText = txtRoomNumber.getText().trim();
        String capacityText = txtCapacity.getText().trim();

        if (!isValidInteger(roomNumberText) || !isValidInteger(capacityText)) {
            showAlert("Validation Error", "Room number and capacity must be valid numbers.", Alert.AlertType.WARNING);
            return;
        }

        int roomNumber = Integer.parseInt(roomNumberText);
        int capacity = Integer.parseInt(capacityText);

        RoomDAO roomDAO = new RoomDAO();
        if (selected.getRoomNumber() != roomNumber && 
            roomDAO.getRoomIdByNumberAndBuilding(roomNumber, buildingId) != -1) {
            showAlert("Duplicate Room", "Room number already exists in this building.", Alert.AlertType.ERROR);
            return;
        }

        selected.setRoomNumber(roomNumber);
        selected.setCapacity(capacity);

        boolean success = RoomDAO.updateRoom(selected);
        if (success) {
            loadRooms();
            clearFields();
        } else {
            showAlert("Error", "Could not update room. Make sure room number is unique.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onDeleteRoom() {
        Room selected = tblRooms.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a room to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected room?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                boolean deleted = RoomDAO.deleteRoom(selected.getId());
                if (deleted) {
                    loadRooms();
                } else {
                    showAlert("Error", "Could not delete room.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void clearFields() {
        txtRoomNumber.clear();
        txtCapacity.clear();
    }

    private boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
