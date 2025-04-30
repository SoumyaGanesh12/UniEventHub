package unieventhub.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import unieventhub.dao.OrganizerDAO;
import unieventhub.model.Organizer;

import java.io.IOException;

public class AdminListOrganizersController {

    @FXML private TableView<Organizer> tblOrganizers;
    @FXML private TableColumn<Organizer, String> colFirstName;
    @FXML private TableColumn<Organizer, String> colLastName;
    @FXML private TableColumn<Organizer, String> colEmail;
    @FXML private TableColumn<Organizer, String> colContact;
    @FXML private TableColumn<Organizer, String> colClubName;

    private final ObservableList<Organizer> organizerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colFirstName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colContact.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getContactNumber()));
        colClubName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getClub()));
        
        applyEllipsisAndTooltip(colFirstName);
        applyEllipsisAndTooltip(colLastName);
        applyEllipsisAndTooltip(colEmail);
        applyEllipsisAndTooltip(colContact);
        applyEllipsisAndTooltip(colClubName);

        loadOrganizers();
    }
    
    private void applyEllipsisAndTooltip(TableColumn<Organizer, String> column) {
        column.setCellFactory(tc -> new TableCell<>() {
            private final Label label = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    label.setTooltip(new Tooltip(item));
                    label.setMaxWidth(Double.MAX_VALUE);
                    label.setStyle("-fx-text-overrun: ellipsis;");
                    setGraphic(label);
                }
            }
        });
    }

    private void loadOrganizers() {
        organizerList.setAll(OrganizerDAO.getAllOrganizers());
        tblOrganizers.setItems(organizerList);
    }

    @FXML
    private void onEditClick() {
        Organizer selected = tblOrganizers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an organizer to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/AdminOrganizerForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            AdminOrganizerFormController controller = loader.getController();
            controller.setOrganizer(selected);
            controller.setRefreshCallback(this::loadOrganizers);

            stage.setTitle("Edit Organizer");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load organizer form.");
        }
    }

    @FXML
    private void onDeleteClick() {
        Organizer selected = tblOrganizers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an organizer to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected organizer?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                boolean deleted = OrganizerDAO.deleteOrganizer(selected.getUserId());
                if (deleted) {
                    organizerList.remove(selected);
                } else {
                    showAlert("Failed to delete organizer.");
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
