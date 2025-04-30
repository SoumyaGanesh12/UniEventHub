package unieventhub.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import unieventhub.dao.BuildingDAO;
import unieventhub.dao.RoomDAO;
import unieventhub.model.Building;
import unieventhub.model.Room;

import java.io.IOException;
import java.util.List;

public class AdminListBuildingsController {

    @FXML private TableView<Building> tblBuildings;
    @FXML private TableColumn<Building, Integer> colId;
    @FXML private TableColumn<Building, String> colName;
    @FXML private TableColumn<Building, Integer> colRoomCount;
    @FXML private TableColumn<Building, Integer> colMaxRooms;
    @FXML private TableColumn<Building, Void> colActions;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final ObservableList<Building> buildingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colMaxRooms.setCellValueFactory(new PropertyValueFactory<>("maxRoomsAllowed"));
        colRoomCount.setCellValueFactory(data -> {
            int count = RoomDAO.getRoomCountByBuildingId(data.getValue().getId());
            return new ReadOnlyObjectWrapper<>(count);
        });

        addViewRoomsButtonToTable();
        loadBuildings();
    }

    private void loadBuildings() {
        buildingList.setAll(BuildingDAO.getAllBuildingsWithRoomCount());
        tblBuildings.setItems(buildingList);
    }

    private void addViewRoomsButtonToTable() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink("View Rooms");

            {
                link.setOnAction(e -> openRoomModal(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= tblBuildings.getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(link);
                }
            }
        });
    }

    private void openRoomModal(Building building) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/AdminRoomManager.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Rooms in " + building.getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            AdminRoomManagerController controller = loader.getController();
            controller.setBuilding(building);
            stage.showAndWait();
            loadBuildings();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading room manager.");
        }
    }

    @FXML
    private void onAddClick() {
        openBuildingForm(null);
    }

    @FXML
    private void onEditClick() {
        Building selected = tblBuildings.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a building to edit.");
            return;
        }
        openBuildingForm(selected);
    }

    private void openBuildingForm(Building building) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/AdminBuildingForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            AdminBuildingFormController controller = loader.getController();
            controller.setBuilding(building);
            controller.setRefreshCallback(this::loadBuildings);
            stage.setTitle(building == null ? "Add Building" : "Edit Building");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading building form.");
        }
    }

    @FXML
    private void onDeleteClick() {
        Building selected = tblBuildings.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a building to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this building?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                boolean deleted = BuildingDAO.deleteBuilding(selected.getId());
                if (deleted) {
                    buildingList.remove(selected);
                } else {
                    showAlert("Could not delete building.");
                }
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
