package unieventhub.dao;

import unieventhub.model.Room;
import unieventhub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class: RoomDAO
 * DAO (Data Access Object) class responsible for database operations related to the Room model.
 */
public class RoomDAO implements RoomAction {

    /**
     * Method: getRoomsByBuildingId
     * Returns all rooms belonging to a specific building.
     *
     * @param buildingId ID of the building
     * @return List of Room objects in that building
     */
    @Override
    public List<Room> getRoomsByBuildingId(int buildingId) {
        List<Room> rooms = new ArrayList<>();

        String query = "SELECT * FROM rooms WHERE buildingId = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, buildingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomNumber(rs.getInt("roomNumber"));
                room.setCapacity(rs.getInt("capacity"));
                room.setBuildingId(rs.getInt("buildingId"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    /**
     * Method: getRoomIdByNumberAndBuilding
     * Returns the room ID given a room number and building ID.
     *
     * @param roomNumber Room number to match
     * @param buildingId ID of the building
     * @return Room ID (int) or -1 if not found
     */
    @Override
    public int getRoomIdByNumberAndBuilding(int roomNumber, int buildingId) {
        String query = "SELECT id FROM rooms WHERE roomNumber = ? AND buildingId = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, roomNumber);
            stmt.setInt(2, buildingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if room not found
    }

    /**
     * Method: getRoomById
     * Fetches the Room object given a room ID.
     *
     * @param roomId ID of the room
     * @return Room object or null if not found
     */
    @Override
    public Room getRoomById(int roomId) {
        String query = "SELECT * FROM rooms WHERE id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomNumber(rs.getInt("roomNumber"));
                room.setCapacity(rs.getInt("capacity"));
                room.setBuildingId(rs.getInt("buildingId"));
                return room;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if no room found
    }
    
    public static boolean addRoom(Room room) {
        String query = "INSERT INTO rooms (roomNumber, capacity, buildingId) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, room.getRoomNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.setInt(3, room.getBuildingId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateRoom(Room room) {
        String query = "UPDATE rooms SET roomNumber = ?, capacity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, room.getRoomNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.setInt(3, room.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteRoom(int roomId) {
        String query = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static int getRoomCountByBuildingId(int buildingId) {
        String query = "SELECT COUNT(*) FROM rooms WHERE buildingId = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, buildingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomNumber(rs.getInt("roomNumber"));
                room.setCapacity(rs.getInt("capacity"));
                room.setBuildingId(rs.getInt("buildingId"));
                rooms.add(room);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }


    
}
