package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import unieventhub.dao.BuildingDAO;
import unieventhub.model.Building;

public class AdminBuildingFormController {

    @FXML private TextField txtName;
    @FXML private TextField txtMaxRooms;

    private Building building;
    private Runnable refreshCallback;

    public void setBuilding(Building building) {
        this.building = building;
        if (building != null) {
            txtName.setText(building.getName());
            txtMaxRooms.setText(String.valueOf(building.getMaxRoomsAllowed()));
        }
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void onSaveClick() {
        String name = txtName.getText().trim();
        String maxRoomsStr = txtMaxRooms.getText().trim();

        if (name.isEmpty() || maxRoomsStr.isEmpty()) {
            showAlert("Validation Error", "Please fill all fields.", Alert.AlertType.WARNING);
            return;
        }

        int maxRooms;
        try {
            maxRooms = Integer.parseInt(maxRoomsStr);
            if (maxRooms <= 0) {
                showAlert("Validation Error", "Max rooms must be a positive number.", Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Max rooms must be numeric.", Alert.AlertType.WARNING);
            return;
        }
        
        // Check for duplicate building name
        Integer excludeId = (building != null) ? building.getId() : null;
        if (BuildingDAO.isBuildingNameExists(name, excludeId)) {
            showAlert("Duplicate Building Name", "A building with this name already exists.", Alert.AlertType.ERROR);
            return;
        }

        if (building == null) {
            building = new Building();
        }
        building.setName(name);
        building.setMaxRoomsAllowed(maxRooms);

        boolean success = (building.getId() == 0) ? BuildingDAO.addBuilding(building) : BuildingDAO.updateBuilding(building);
        if (success) {
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            closeWindow();
        } else {
            showAlert("Error", "Failed to save building.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCancelClick() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
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
