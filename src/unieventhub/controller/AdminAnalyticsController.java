package unieventhub.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import unieventhub.dao.AdminAnalyticsDAO;

import java.util.Map;

public class AdminAnalyticsController {

    @FXML private BarChart<String, Number> barTopEvents;
    @FXML private BarChart<String, Number> barEventsPerOrganizer;
    @FXML private PieChart pieCategoryDistribution;

//    private final AdminAnalyticsDAO analyticsDAO = new AdminAnalyticsDAO();

    @FXML
    public void initialize() {
        loadTopRegisteredEvents();
        loadEventsPerOrganizer();
        loadRegistrationsPerCategory();
    }

    private void loadTopRegisteredEvents() {
        Map<String, Integer> topEvents = AdminAnalyticsDAO.getTop5RegisteredEvents();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Registrations");

        topEvents.forEach((label, count) -> 
            series.getData().add(new XYChart.Data<>(label, count))
        );

        barTopEvents.getData().clear();
        barTopEvents.getData().add(series);
    }

    private void loadEventsPerOrganizer() {
        Map<String, Integer> organizerEventCounts = AdminAnalyticsDAO.getEventCountPerOrganizer();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Events");

        organizerEventCounts.forEach((organizer, count) -> 
            series.getData().add(new XYChart.Data<>(organizer, count))
        );

        barEventsPerOrganizer.getData().clear();
        barEventsPerOrganizer.getData().add(series);
    }

    private void loadRegistrationsPerCategory() {
        Map<String, Integer> registrationsByCategory = AdminAnalyticsDAO.getRegistrationsPerCategory();
        pieCategoryDistribution.getData().clear();

        registrationsByCategory.forEach((category, count) -> {
            String label = category + " (" + count + ")";
            pieCategoryDistribution.getData().add(new PieChart.Data(label, count));
        });
    }
}
