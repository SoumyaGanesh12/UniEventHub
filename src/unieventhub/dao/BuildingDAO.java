package unieventhub.dao;

import unieventhub.model.Building;
import unieventhub.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class: BuildingDAO
 * DAO (Data Access Object) class that handles all DB operations related to Buildings.
 * This class encapsulates database interactions and helps in separating persistence logic.
 */
public class BuildingDAO implements BuildingAction{

    /**
     * Method: getAllBuildings
     * Fetches all buildings from the 'buildings' table in the database.
     *
     * @return List of Building objects (List<Building>)
     */
    @Override
	public List<Building> getAllBuildings() {
        List<Building> buildings = new ArrayList<>(); // Used to hold all buildings fetched from DB

        String query = "SELECT * FROM buildings";

        try (
            Connection conn = DBConnection.getConnection(); // Get DB connection
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Building building = new Building();
                building.setId(rs.getInt("id"));
                building.setName(rs.getString("name"));
                building.setMaxRoomsAllowed(rs.getInt("maxRoomsAllowed"));
                buildings.add(building); // Add each building to the list
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print exception if any
        }

        return buildings;
    }

    /**
     * Method: getBuildingNameById
     * Returns the name of the building given its ID.
     *
     * @param buildingId (int) - ID of the building
     * @return Name of the building (String), or null if not found
     */
    @Override
    public String getBuildingNameById(int buildingId) {
        String query = "SELECT name FROM buildings WHERE id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, buildingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name"); // Return name if found
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if not found or error occurred
    }
    
    public static boolean addBuilding(Building building) {
        String query = "INSERT INTO buildings (name, maxRoomsAllowed) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, building.getName());
            stmt.setInt(2, building.getMaxRoomsAllowed());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateBuilding(Building building) {
        String query = "UPDATE buildings SET name = ?, maxRoomsAllowed = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, building.getName());
            stmt.setInt(2, building.getMaxRoomsAllowed());
            stmt.setInt(3, building.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteBuilding(int id) {
        String query = "DELETE FROM buildings WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isBuildingNameExists(String name, Integer excludeId) {
        String query = "SELECT COUNT(*) FROM buildings WHERE name = ?" +
                       (excludeId != null ? " AND id != ?" : "");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            if (excludeId != null) {
                stmt.setInt(2, excludeId);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public static List<Building> getAllBuildingsWithRoomCount() {
        List<Building> buildings = new ArrayList<>();
        String query = "SELECT b.*, COUNT(r.id) as roomCount FROM buildings b " +
                       "LEFT JOIN rooms r ON b.id = r.buildingId GROUP BY b.id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Building b = new Building();
                b.setId(rs.getInt("id"));
                b.setName(rs.getString("name"));
                b.setMaxRoomsAllowed(rs.getInt("maxRoomsAllowed"));
                // Optionally set room count if your model supports it
                buildings.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return buildings;
    }

}
