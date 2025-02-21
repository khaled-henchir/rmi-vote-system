import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.rmi.*;
import java.rmi.server.*;
import java.sql.DriverManager;

public class Auth extends UnicastRemoteObject implements AuthInterface {
    
    private Connection conn;
    private String currentUsername;
    
    public Auth(Connection connection) throws RemoteException {
        super();
		this.conn = connection;
		initializeUsersTable();
    }
	private void initializeUsersTable() {
		    if (conn == null) {
        System.out.println("Database connection is null!");
    } else {
        
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(255) NOT NULL UNIQUE, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "has_voted BOOLEAN DEFAULT FALSE"
                    + ")";
            
            try (PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
                stmt.executeUpdate();
                System.out.println("Users table initialized.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }

    public boolean register(String username, String password) {
        if (conn == null) {
            System.err.println("Database connection is not established.");
            return false;
        }

        try {
            String sql = "INSERT INTO Users (username, password, has_voted) VALUES (?, ?, 0)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        try {
            String sql = "SELECT password FROM Users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                            if (rs.getString("password").equals(password)) {
                            currentUsername = username;  
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean hasUserVoted() throws RemoteException {
        String query = "SELECT has_voted FROM users WHERE username = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, currentUsername);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("has_voted") == 1;  
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error checking vote status.", e);
        }
        return false; 
    }

    @Override
    public boolean markUserAsVoted() throws RemoteException {
        String query = "UPDATE users SET has_voted = 1 WHERE username = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, currentUsername);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error marking user as voted.", e);
        }
    }
	
	public String getCurrentUser() {
        return currentUsername;
    }

    public void logout() {

    }
}
