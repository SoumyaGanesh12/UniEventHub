package unieventhub.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import unieventhub.dao.StudentDAO;
import unieventhub.model.Student;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class AdminListStudentsController {

    @FXML private TableView<Student> tblStudents;
    @FXML private TableColumn<Student, String> colNeuid;
    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, String> colContact;
    @FXML private TableColumn<Student, String> colCollege;
    @FXML private TableColumn<Student, String> colCourse;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {        
        colNeuid.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNeuid()));
        colFirstName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colContact.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getContactNumber()));
        colCollege.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCollege()));
        colCourse.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourse()));

        addTooltipWithEllipsis(colEmail, Student::getEmail);
        addTooltipWithEllipsis(colNeuid, Student::getNeuid);
        addTooltipWithEllipsis(colFirstName, Student::getFirstName);
        addTooltipWithEllipsis(colLastName, Student::getLastName);
        addTooltipWithEllipsis(colContact, Student::getContactNumber);
        addTooltipWithEllipsis(colCollege, Student::getCollege);
        addTooltipWithEllipsis(colCourse, Student::getCourse);


        loadStudents();
    }
    
    private void addTooltipWithEllipsis(TableColumn<Student, String> column, Function<Student, String> valueExtractor) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Label label = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String value = valueExtractor.apply(getTableView().getItems().get(getIndex()));
                    label.setText(value);
                    label.setTooltip(new Tooltip(value));
                    label.setStyle("-fx-text-overrun: ellipsis; -fx-max-width: 140; -fx-wrap-text: false;");
                    label.setPrefWidth(col.getWidth() - 10);
                    setGraphic(label);
                }
            }
        });
    }

    private void loadStudents() {
        studentList.setAll(StudentDAO.getAllStudents());
        tblStudents.setItems(studentList);
    }

    @FXML
    private void onEditClick() {
        Student selected = tblStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a student to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/AdminStudentForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            AdminStudentFormController controller = loader.getController();
            controller.setStudent(selected);
            stage.setTitle("Edit Student");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadStudents();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load student form.");
        }
    }

    @FXML
    private void onDeleteClick() {
        Student selected = tblStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a student to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected student?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                boolean deleted = StudentDAO.deleteStudent(selected.getUserId());
                if (deleted) {
                    studentList.remove(selected);
                } else {
                    showAlert("Failed to delete student.");
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
