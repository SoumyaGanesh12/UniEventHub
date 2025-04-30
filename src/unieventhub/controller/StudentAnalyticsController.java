package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import unieventhub.dao.RegistrationDAO;
import unieventhub.model.Event;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentAnalyticsController {

    @FXML private Label totalRegisteredLabel;
    @FXML private Label moneySpentLabel;
    @FXML private Label categoryBreakdownLabel;
    @FXML private PieChart categoryPieChart;

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private int studentId;
    private String studentName;

    public void setStudent(int id, String name) {
        this.studentId = id;
        this.studentName = name;

        List<Event> registeredEvents = registrationDAO.getRegisteredEventsByStudentId(studentId);

        totalRegisteredLabel.setText("Total Events Registered: " + registeredEvents.size());

        double totalMoney = registeredEvents.stream()
                .filter(e -> e.getPrice() > 0)
                .mapToDouble(Event::getPrice)
                .sum();
        moneySpentLabel.setText("Total Money Spent: $" + String.format("%.2f", totalMoney));

        Map<String, Long> categoryCounts = registeredEvents.stream()
                .collect(Collectors.groupingBy(e -> e.getCategory().name(), Collectors.counting()));

        StringBuilder breakdown = new StringBuilder();
        categoryCounts.forEach((cat, count) -> breakdown.append(cat).append(": ").append(count).append(", "));
        if (!categoryCounts.isEmpty()) breakdown.setLength(breakdown.length() - 2); // remove last comma

        categoryBreakdownLabel.setText("Category Breakdown: " + breakdown);

        //  Set Pie Chart Data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : categoryCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        categoryPieChart.setData(pieChartData);
        categoryPieChart.setTitle("Event Category Distribution");
        categoryPieChart.setLabelsVisible(true);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unieventhub/view/StudentHome.fxml"));
            Scene scene = new Scene(loader.load());

            StudentHomeController controller = loader.getController();
            controller.setStudent(studentId, studentName);
            controller.reloadEvents();

            Stage stage = (Stage) totalRegisteredLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Student Dashboard");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}