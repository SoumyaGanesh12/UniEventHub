package unieventhub.dao;

import unieventhub.model.Student;
import unieventhub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public static boolean insertStudent(Student student) {
        String insertUserSql = "INSERT INTO users (firstName, lastName, email, username, password, role) VALUES (?, ?, ?, ?, ?, ?)";
        String insertStudentSql = "INSERT INTO students (userId, neuId, college, course, contactNumber) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {

            // Insert into users table
            userStmt.setString(1, student.getFirstName());
            userStmt.setString(2, student.getLastName());
            userStmt.setString(3, student.getEmail());
            userStmt.setString(4, student.getUsername());
            userStmt.setString(5, student.getPassword());
            userStmt.setString(6, student.getRole());

            int rows = userStmt.executeUpdate();
            if (rows == 0) return false;

            // Get generated user_id
            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                student.setUserId(userId);
            } else {
                return false;
            }

            // Insert into students table
            try (PreparedStatement studentStmt = conn.prepareStatement(insertStudentSql)) {
                studentStmt.setInt(1, student.getUserId());
                studentStmt.setString(2, student.getNeuid());
                studentStmt.setString(3, student.getCollege());
                studentStmt.setString(4, student.getCourse());
                studentStmt.setString(5, student.getContactNumber());

                return studentStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = """
            SELECT u.id, u.firstName, u.lastName, u.email, u.username, u.password, u.role,
                   s.neuId, s.college, s.course, s.contactNumber
            FROM users u
            JOIN students s ON u.id = s.userId
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student s = new Student();
                s.setUserId(rs.getInt("id"));
                s.setFirstName(rs.getString("firstName"));
                s.setLastName(rs.getString("lastName"));
                s.setEmail(rs.getString("email"));
                s.setUsername(rs.getString("username"));
                s.setPassword(rs.getString("password"));
                s.setRole(rs.getString("role"));
                s.setNeuid(rs.getString("neuId"));
                s.setCollege(rs.getString("college"));
                s.setCourse(rs.getString("course"));
                s.setContactNumber(rs.getString("contactNumber"));
                students.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static boolean updateStudent(Student s) {
        String userUpdate = "UPDATE users SET username=?, firstName=?, lastName=?, email=?, password=?, role=? WHERE id=?";
        String studentUpdate = "UPDATE students SET neuId=?, college=?, course=?, contactNumber=? WHERE userId=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userUpdate);
             PreparedStatement stuStmt = conn.prepareStatement(studentUpdate)) {

            userStmt.setString(1, s.getUsername());
            userStmt.setString(2, s.getFirstName());
            userStmt.setString(3, s.getLastName());
            userStmt.setString(4, s.getEmail());
            userStmt.setString(5, s.getPassword());
            userStmt.setString(6, s.getRole());
            userStmt.setInt(7, s.getUserId());
            userStmt.executeUpdate();

            stuStmt.setString(1, s.getNeuid());
            stuStmt.setString(2, s.getCollege());
            stuStmt.setString(3, s.getCourse());
            stuStmt.setString(4, s.getContactNumber());
            stuStmt.setInt(5, s.getUserId());
            stuStmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteStudent(int userId) {
        String delete = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(delete)) {

            stmt.setInt(1, userId); 
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isNeuidExists(String neuId) {
        String sql = "SELECT COUNT(*) FROM students WHERE neuId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, neuId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
