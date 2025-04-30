package unieventhub.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static String url;
    private static String username;
    private static String password;

    static {
        try (
        	InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("unieventhub/config/db.properties");
        ) {
            Properties prop = new Properties();
            prop.load(input);

            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Error loading DB config", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
