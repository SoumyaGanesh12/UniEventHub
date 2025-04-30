package unieventhub.dao;

import unieventhub.util.DBConnection;

import java.sql.*;
import java.util.*;

public class AdminAnalyticsDAO {

    public static Map<String, Integer> getTop5RegisteredEvents() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = """
            SELECT e.name, e.date, COUNT(r.id) AS registrations
            FROM events e
            LEFT JOIN registrations r ON e.id = r.eventId
            GROUP BY e.id
            ORDER BY registrations DESC
            LIMIT 5
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String label = rs.getString("name") + " - " + rs.getDate("date");
                int count = rs.getInt("registrations");
                data.put(label, count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static Map<String, Integer> getEventCountPerOrganizer() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = """
            SELECT u.username, COUNT(e.id) AS event_count
            FROM users u
            JOIN events e ON u.id = e.organizerId
            GROUP BY u.id
            ORDER BY event_count DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String organizer = rs.getString("username");
                int count = rs.getInt("event_count");
                data.put(organizer, count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static Map<String, Integer> getRegistrationsPerCategory() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = """
            SELECT e.category, COUNT(r.id) AS registrations
            FROM events e
            LEFT JOIN registrations r ON e.id = r.eventId
            GROUP BY e.category
            ORDER BY registrations DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                int count = rs.getInt("registrations");
                data.put(category, count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
