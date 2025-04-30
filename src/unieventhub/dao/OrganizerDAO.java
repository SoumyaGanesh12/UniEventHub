package unieventhub.dao;

import unieventhub.model.Organizer;
import unieventhub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrganizerDAO {

    public static boolean insertOrganizer(Organizer organizer) {
    	  String insertUserSQL = "INSERT INTO users (username, firstName, lastName, email, password, role) VALUES (?, ?, ?, ?, ?, ?)";
        String insertOrganizerSQL = "INSERT INTO organizers (userId, club, contactNumber) VALUES (?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement userStmt = conn.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)
        ) {
        	userStmt.setString(1, organizer.getUsername());
        	userStmt.setString(2, organizer.getFirstName());
        	userStmt.setString(3, organizer.getLastName());
        	userStmt.setString(4, organizer.getEmail());
        	userStmt.setString(5, organizer.getPassword());
        	userStmt.setString(6, organizer.getRole());

            int affectedRows = userStmt.executeUpdate();
            if (affectedRows == 0) return false;

            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                organizer.setUserId(userId);

                try (PreparedStatement orgStmt = conn.prepareStatement(insertOrganizerSQL)) {
                    orgStmt.setInt(1, userId);
                    orgStmt.setString(2, organizer.getClub());
                    orgStmt.setString(3, organizer.getContactNumber());
                    return orgStmt.executeUpdate() > 0;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Organizer> getAllOrganizers() {
        List<Organizer> organizers = new ArrayList<>();
        String sql = "SELECT u.id, u.firstName, u.lastName, u.email, u.username, u.password, u.role, o.club, o.contactNumber " +
                     "FROM users u JOIN organizers o ON u.id = o.userId";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Organizer o = new Organizer();
                o.setUserId(rs.getInt("id"));
                o.setFirstName(rs.getString("firstName"));
                o.setLastName(rs.getString("lastName"));
                o.setEmail(rs.getString("email"));
                o.setUsername(rs.getString("username"));
                o.setPassword(rs.getString("password"));
                o.setRole(rs.getString("role"));
                o.setClub(rs.getString("club"));
                o.setContactNumber(rs.getString("contactNumber"));
                organizers.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return organizers;
    }

    public static boolean updateOrganizer(Organizer o) {
        String updateUser = "UPDATE users SET firstName=?, lastName=?, email=? WHERE id=?";
        String updateOrganizer = "UPDATE organizers SET club=?, contactNumber=? WHERE userId=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(updateUser);
             PreparedStatement orgStmt = conn.prepareStatement(updateOrganizer)) {

            userStmt.setString(1, o.getFirstName());
            userStmt.setString(2, o.getLastName());
            userStmt.setString(3, o.getEmail());
            userStmt.setInt(4, o.getUserId());
            userStmt.executeUpdate();

            orgStmt.setString(1, o.getClub());
            orgStmt.setString(2, o.getContactNumber());
            orgStmt.setInt(3, o.getUserId());
            orgStmt.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteOrganizer(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Integer getOrganizerIdByClub(String clubName) {
        String sql = "SELECT userId FROM organizers WHERE club = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, clubName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("userId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
