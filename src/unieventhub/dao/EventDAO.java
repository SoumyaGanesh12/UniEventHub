package unieventhub.dao;

import unieventhub.model.Event;
import unieventhub.util.DBConnection;
import unieventhub.util.EventCategory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class: EventDAO
 * DAO (Data Access Object) for handling database operations related to Events.
 */
public class EventDAO implements EventAction {

    /**
     * Method: isRoomAvailable
     * Checks if a room is available for a new or updated event.
     * Handles conflict checking by comparing date and time overlaps.
     *
     * @param roomId ID of the room
     * @param date Event date
     * @param startTime Start time of the event
     * @param endTime End time of the event
     * @param excludeEventId (Optional) if passed, excludes this event from conflict check (used in update)
     * @return true if room is available, false if there's a conflict
     */
    @Override
	public boolean isRoomAvailable(int roomId, LocalDate date, LocalTime startTime, LocalTime endTime, Integer excludeEventId) {
        String query = """
                SELECT * FROM events 
                WHERE roomId = ? AND date = ? AND (
                    (startTime < ? AND endTime > ?) OR
                    (startTime < ? AND endTime > ?) OR
                    (startTime >= ? AND endTime <= ?)
                )
                """;

        if (excludeEventId != null) {
            query += " AND id != ?";
        }

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, roomId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(endTime));
            stmt.setTime(4, Time.valueOf(startTime));
            stmt.setTime(5, Time.valueOf(endTime));
            stmt.setTime(6, Time.valueOf(startTime));
            stmt.setTime(7, Time.valueOf(startTime));
            stmt.setTime(8, Time.valueOf(endTime));

            if (excludeEventId != null) {
                stmt.setInt(9, excludeEventId);
            }

            ResultSet rs = stmt.executeQuery();
            return !rs.next(); // true if no overlapping event
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Overloaded Method: isRoomAvailable
     * Simpler version for new event creation (no excludeEventId).
     */
    @Override
    public boolean isRoomAvailable(int roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return isRoomAvailable(roomId, date, startTime, endTime, null);
    }

    /**
     * Method: createEvent
     * Adds a new event to the database.
     * Enforces organizer-only access using internal validation.
     *
     * @param event Event object to insert
     * @return true if successful, false otherwise
     */
    @Override
    public boolean createEvent(Event event) {
        if (!isValidOrganizer(event.getOrganizer())) {
            System.err.println("Only users with role 'organizer' can create events.");
            return false;
        }

        String query = """
                INSERT INTO events (
                    name, description, date, startTime, endTime, category,
                    organizerId, roomId, price, registrationType,
                    foodAvailability, seatLimit
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
        	stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, Date.valueOf(event.getDate()));
            stmt.setTime(4, Time.valueOf(event.getStartTime()));
            stmt.setTime(5, Time.valueOf(event.getEndTime()));
            stmt.setString(6, event.getCategory().name());
            stmt.setInt(7, event.getOrganizerId());
            stmt.setInt(8, event.getRoomId());
            stmt.setDouble(9, event.getPrice());
            stmt.setString(10, event.getRegistrationType());
            stmt.setString(11, event.getFoodAvailability());
            stmt.setInt(12, event.getSeatLimit());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method: updateEvent
     * Updates existing event data in the DB.
     *
     * @param event Event object with updated details (must include event ID)
     * @return true if update was successful, false otherwise
     */
    @Override
    public boolean updateEvent(Event event) {
    	String query = """
    		    UPDATE events SET
                name = ?, description = ?, date = ?, startTime = ?, endTime = ?, category = ?,
                organizerId = ?, roomId = ?, price = ?, registrationType = ?,
                foodAvailability = ?, seatLimit = ?
            WHERE id = ?
            """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
        	 stmt.setString(1, event.getName());
             stmt.setString(2, event.getDescription());
             stmt.setDate(3, Date.valueOf(event.getDate()));
             stmt.setTime(4, Time.valueOf(event.getStartTime()));
             stmt.setTime(5, Time.valueOf(event.getEndTime()));
             stmt.setString(6, event.getCategory().name());
             stmt.setInt(7, event.getOrganizerId());
             stmt.setInt(8, event.getRoomId());
             stmt.setDouble(9, event.getPrice());
             stmt.setString(10, event.getRegistrationType());
             stmt.setString(11, event.getFoodAvailability());
             stmt.setInt(12, event.getSeatLimit());
             stmt.setInt(13, event.getId());
             return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method: getEventsByOrganizer
     * Fetches all events created by a given organizer.
     * Demonstrates use of **List**, joins, and aliasing.
     *
     * @param organizerUsername username of the organizer
     * @return List of Event objects with additional UI details
     */
@Override
public List<Event> getEventsByOrganizer(String organizerUsername) {
    List<Event> events = new ArrayList<>();
    String sql = """
            SELECT e.*, b.name AS buildingName, r.roomNumber AS roomNumber 
            FROM events e
            JOIN rooms r ON e.roomId = r.id
            JOIN buildings b ON r.buildingId = b.id
            JOIN users u ON e.organizerId = u.id
            WHERE u.username = ?
            """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, organizerUsername);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Event event = new Event(
            		 rs.getString("name"),
                     rs.getString("description"),
                     rs.getDate("date").toLocalDate(),
                     rs.getTime("startTime").toLocalTime(),
                     rs.getTime("endTime").toLocalTime(),
                     EventCategory.valueOf(rs.getString("category")),
                     rs.getInt("organizerId"),
                     rs.getInt("roomId"),
                     rs.getDouble("price"),
                     rs.getString("registrationType"),
                     rs.getString("foodAvailability"),
                     rs.getInt("seatLimit")
            );
            event.setId(rs.getInt("id"));
            event.setBuildingName(rs.getString("buildingName"));
            event.setRoomName(rs.getString("roomNumber"));
            event.setOrganizer(organizerUsername); // Set organizer username for display
            events.add(event);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return events;
}

    /**
     * Method: deleteEventById
     * Deletes an event from the DB using its ID.
     *
     * @param eventId ID of the event to be deleted
     * @return true if deleted successfully, false otherwise
     */
    @Override
    public boolean deleteEventById(int eventId) {
        String query = "DELETE FROM events WHERE id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, eventId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method: isValidOrganizer
     * Verifies if a username belongs to a valid organizer.
     *
     * @param username user’s login name
     * @return true if user has role 'organizer', false otherwise
     */
    private boolean isValidOrganizer(String username) {
        String query = "SELECT role FROM users WHERE username = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                System.out.println("DEBUG ROLE CHECK: " + username + " => " + role);
                return "organizer".equalsIgnoreCase(role);
            }            
            return false;
            

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Fetches all events scheduled for today.
     * @return List of Event objects
     */    
    public List<Event> getTodaysEventsWithClubAndReg() {
        List<Event> events = new ArrayList<>();

        String query = """
            SELECT 
                e.name AS event_name,
                e.roomId,
                r.roomNumber,
                b.name AS building_name,
                o.club AS organizer_club,
                COUNT(reg.id) AS registration_count
            FROM events e
            JOIN rooms r ON e.roomId = r.id
            JOIN buildings b ON r.buildingId = b.id
            JOIN organizers o ON e.organizerId = o.userId
            LEFT JOIN registrations reg ON reg.eventId = e.id
            WHERE e.date = CURDATE()
            GROUP BY e.id
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Event event = new Event();
                event.setName(rs.getString("event_name"));
                event.setRoomId(rs.getInt("roomId"));
                event.setRoomName("Room " + rs.getString("roomNumber"));
                event.setBuildingName(rs.getString("building_name"));
                event.setDescription(rs.getString("organizer_club")); // used only to pass club name temporarily
                event.setRegistrationCount(rs.getInt("registration_count")); 

                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }
   
   public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();

        String query = """
            SELECT e.*, r.roomNumber AS room_name, b.name AS building_name
            FROM events e
            JOIN rooms r ON e.roomId = r.id
            JOIN buildings b ON r.buildingId = b.id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Event event = new Event(
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("startTime").toLocalTime(),
                    rs.getTime("endTime").toLocalTime(),
                    EventCategory.valueOf(rs.getString("category")), 
                    rs.getInt("organizerId"),
                    rs.getInt("roomId"),
                    rs.getDouble("price"),
                    rs.getString("registrationType"),
                    rs.getString("foodAvailability"),
                    rs.getInt("seatLimit")
                );
                event.setId(rs.getInt("id"));
                event.setRoomName(rs.getString("room_name"));
                event.setBuildingName(rs.getString("building_name"));
                events.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }
    
    public boolean createEventByAdmin(Event event) {
        String query = """
                INSERT INTO events (
                    name, description, date, startTime, endTime, category,
                    organizerId, roomId, price, registrationType,
                    foodAvailability, seatLimit
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, Date.valueOf(event.getDate()));
            stmt.setTime(4, Time.valueOf(event.getStartTime()));
            stmt.setTime(5, Time.valueOf(event.getEndTime()));
            stmt.setString(6, event.getCategory().name());
            stmt.setInt(7, event.getOrganizerId());
            stmt.setInt(8, event.getRoomId());
            stmt.setDouble(9, event.getPrice());
            stmt.setString(10, event.getRegistrationType());
            stmt.setString(11, event.getFoodAvailability());
            stmt.setInt(12, event.getSeatLimit());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Event> getAllEventsWithDetailsAndCounts() {
        List<Event> events = new ArrayList<>();

        String query = """
            SELECT 
                e.*, 
                r.roomNumber, 
                b.name AS building_name, 
                o.club AS organizer_club,
                u.username AS organizer_username,
                COUNT(reg.id) AS registration_count
            FROM events e
            JOIN rooms r ON e.roomId = r.id
            JOIN buildings b ON r.buildingId = b.id
            JOIN organizers o ON e.organizerId = o.userId
            JOIN users u ON u.id = o.userId
            LEFT JOIN registrations reg ON reg.eventId = e.id
            GROUP BY e.id
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setName(rs.getString("name"));
                event.setDescription(rs.getString("description"));
                event.setDate(rs.getDate("date").toLocalDate());
                event.setStartTime(rs.getTime("startTime").toLocalTime());
                event.setEndTime(rs.getTime("endTime").toLocalTime());
                event.setCategory(EventCategory.valueOf(rs.getString("category")));
                event.setOrganizerId(rs.getInt("organizerId"));
                event.setRoomId(rs.getInt("roomId"));
                event.setPrice(rs.getDouble("price"));
                event.setRegistrationType(rs.getString("registrationType"));
                event.setFoodAvailability(rs.getString("foodAvailability"));
                event.setSeatLimit(rs.getInt("seatLimit"));
                event.setRoomName(rs.getString("roomNumber"));
                event.setBuildingName(rs.getString("building_name"));
                event.setOrganizerClub(rs.getString("organizer_club"));
                event.setOrganizer(rs.getString("organizer_username"));
                event.setRegistrationCount(rs.getInt("registration_count"));

                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }
    
    public static List<Event> getAllEventsWithDetails() {
        List<Event> events = new ArrayList<>();
        String sql = """
            SELECT e.*, b.name AS buildingName, r.roomNumber, o.club AS organizerClub, u.username AS organizerUsername,
                   (SELECT COUNT(*) FROM registrations reg WHERE reg.eventId = e.id) AS registrationCount
            FROM events e
            JOIN rooms r ON e.roomId = r.id
            JOIN buildings b ON r.buildingId = b.id
            JOIN organizers o ON e.organizerId = o.userId
            JOIN users u ON o.userId = u.id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Event event = new Event(
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("startTime").toLocalTime(),
                    rs.getTime("endTime").toLocalTime(),
                    EventCategory.valueOf(rs.getString("category")),
                    rs.getInt("organizerId"),
                    rs.getInt("roomId"),
                    rs.getDouble("price"),
                    rs.getString("registrationType"),
                    rs.getString("foodAvailability"),
                    rs.getInt("seatLimit"),
                    rs.getInt("registrationCount")
                );
                event.setId(rs.getInt("id"));
                event.setRoomName("Room " + rs.getInt("roomNumber"));
                event.setBuildingName(rs.getString("buildingName"));
                event.setOrganizerClub(rs.getString("organizerClub"));
                event.setOrganizer(rs.getString("organizerUsername"));
                events.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }
 
}
