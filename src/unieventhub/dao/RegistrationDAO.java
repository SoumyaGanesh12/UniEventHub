package unieventhub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import unieventhub.model.User;
import unieventhub.util.DBConnection;
import unieventhub.util.EventCategory;
import unieventhub.model.Event;

public class RegistrationDAO {
	public List<User> getRegisteredStudents(int eventId) {
        List<User> students = new ArrayList<>();
        String sql = """
                SELECT u.id, u.username, u.email, u.firstName, u.lastName FROM registrations r
                JOIN users u ON r.studentId = u.id
                WHERE r.eventId = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                students.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }
	
	public boolean registerStudentToEvent(int studentId, int eventId) {
		String query = "INSERT INTO registrations (studentId, eventId) VALUES (?, ?)";

	    try (
	        Connection conn = DBConnection.getConnection();
	        PreparedStatement stmt = conn.prepareStatement(query)
	    ) {
	        stmt.setInt(1, studentId);
	        stmt.setInt(2, eventId);
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public boolean deregisterStudentFromEvent(int studentId, int eventId) {
	    String sql = "DELETE FROM registrations WHERE studentId = ? AND eventId = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, studentId);
	        stmt.setInt(2, eventId);
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public List<Event> getRegisteredEventsByStudentId(int studentId) {
	    List<Event> registeredEvents = new ArrayList<>();

	    String sql = """
	      SELECT e.id, e.name, e.date, e.startTime, e.endTime,
       e.roomId, e.category, e.price,
       ro.roomNumber AS roomName, 
       b.name AS buildingName
	    FROM registrations r
	    JOIN events e ON r.eventId = e.id
	    JOIN rooms ro ON e.roomId = ro.id
	    JOIN buildings b ON ro.buildingId = b.id
	    WHERE r.studentId = ?
	    """;

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, studentId);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            Event event = new Event();
	            event.setId(rs.getInt("id"));
	            event.setName(rs.getString("name"));
	            event.setDate(rs.getDate("date").toLocalDate());
	            event.setStartTime(rs.getTime("startTime").toLocalTime());
	            event.setEndTime(rs.getTime("endTime").toLocalTime());
	            event.setRoomId(rs.getInt("roomId"));
	            event.setCategory(EventCategory.valueOf(rs.getString("category")));
	            event.setPrice(rs.getDouble("price"));
	            event.setBuildingName(rs.getString("buildingName"));
	            event.setRoomName(rs.getString("roomName"));
	            event.setRegistered(true);  
	            registeredEvents.add(event);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return registeredEvents;
	}
	
	
	public int getRegistrationCountForEvent(int eventId) {
	    String sql = "SELECT COUNT(*) FROM registrations WHERE eventId = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, eventId);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}

	public boolean isAlreadyRegistered(int eventId, int studentId) {
	    String sql = "SELECT COUNT(*) FROM registrations WHERE eventId = ? AND studentId = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, eventId);
	        stmt.setInt(2, studentId);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public boolean registerStudent(int eventId, int studentId) {
	    String sql = "INSERT INTO registrations (eventId, studentId) VALUES (?, ?)";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, eventId);
	        stmt.setInt(2, studentId);
	        int rowsAffected = stmt.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public List<String> getRegisteredStudentEmailsForEvent(int eventId) {
	    List<String> emails = new ArrayList<>();
	    String sql = """
	        SELECT u.email 
	        FROM registrations r
	        JOIN users u ON r.studentId = u.id
	        WHERE r.eventId = ?
	    """;

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, eventId);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            emails.add(rs.getString("email"));
	        }
	    } catch (SQLException e) {
	        System.out.println("[RegistrationDAO] Error getting emails: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return emails;
	}

	
}