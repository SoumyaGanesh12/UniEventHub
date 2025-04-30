package unieventhub.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import unieventhub.dao.EventDAO;
import unieventhub.dao.RegistrationDAO;
import unieventhub.model.Event;
import unieventhub.model.User;

public class OrganizerAnalyticsController {

    @FXML private BarChart<String, Number> analyticsChart;  // Left side chart
    @FXML private PieChart categoryPieChart;                // Right side chart

    private User organizer;

    /**
     * Called from the Organizer Dashboard after login.
     * Sets the organizer and loads the charts.
     */
    public void initializeAnalyticsDashboard(User organizer) {
        this.organizer = organizer;
        loadBarChart();  // Load event-wise registration counts
        loadPieChart();  // Load category-wise registration distribution
    }

    /**
     * Loads the Bar Chart showing number of registrations for each event.
     * Only considers events created by the logged-in organizer.
     */
    private void loadBarChart() {
        analyticsChart.getData().clear();  // Clear any existing data

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Registrations");

        EventDAO eventDAO = new EventDAO();
        RegistrationDAO registrationDAO = new RegistrationDAO();

        List<Event> events = eventDAO.getEventsByOrganizer(organizer.getUsername());
        boolean hasData = false;
        for (Event event : events) {
            int count = registrationDAO.getRegisteredStudents(event.getId()).size();
            if (count > 0) hasData = true;

            String label = event.getName() + " - " + event.getDate();
            series.getData().add(new XYChart.Data<>(label, count));
        }

        if (hasData) {
            analyticsChart.getData().add(series);
        } else {
            // Show a dummy label with 0 value
            series.getData().add(new XYChart.Data<>("No registrations yet", 0));
            analyticsChart.getData().add(series);
        }
    }

    /**
     * Loads the Pie Chart showing total registrations grouped by event category.
     * Only considers events created by the logged-in organizer.
     */
    private void loadPieChart() {
        categoryPieChart.getData().clear();  // Clear old slices

        EventDAO eventDAO = new EventDAO();
        RegistrationDAO registrationDAO = new RegistrationDAO();

        List<Event> events = eventDAO.getEventsByOrganizer(organizer.getUsername());

        // Map to store registration counts per category
        Map<String, Integer> categoryCountMap = new HashMap<>();

        for (Event event : events) {
            int regCount = registrationDAO.getRegisteredStudents(event.getId()).size();
            if (regCount > 0) {
                String category = event.getCategory().name();
                categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + regCount);
            }
        }
        
        int totalRegistrations = categoryCountMap.values().stream().mapToInt(Integer::intValue).sum();

        if (totalRegistrations == 0) {
            // Add a dummy slice
            categoryPieChart.getData().add(new PieChart.Data("No registrations", 1));
            return;
        }
        
     // Color palette
        String[] colors = {
            "-fx-pie-color: #4caf50;",  // green
            "-fx-pie-color: #2196f3;",  // blue
            "-fx-pie-color: #ff9800;",  // orange
            "-fx-pie-color: #e91e63;",  // pink
            "-fx-pie-color: #9c27b0;",  // purple
            "-fx-pie-color: #f44336;"   // red
        };

        int colorIndex = 0;

        // Create a pie slice for each category
        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            String category = entry.getKey();
            int count = entry.getValue();
            double percent = (count * 100.0) / totalRegistrations;

            // Format slice label with percentage
            String label = String.format("%s (%.1f%%)", category, percent);
            PieChart.Data slice = new PieChart.Data(label, count);

            categoryPieChart.getData().add(slice);
            categoryPieChart.setLegendVisible(false);

            // Style the slice with a color
         // Wait for slice node to be available before styling
            final String colorStyle = colors[colorIndex % colors.length];
            slice.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle(colorStyle);
                }
            });
            colorIndex++;
        }
    }

    public void refreshCharts() {
        loadBarChart();
        loadPieChart();
    }

}



