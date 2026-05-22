package pl.umcs.oop.auth;

import pl.umcs.oop.database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountManager {
    public void register(String username, String password) {
        String sql = "INSERT OR IGNORE INTO account (username, password) VALUES (?, ?);";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean authenticate(String username, String password) {
        String sql = "SELECT username, password FROM account WHERE username = ?;";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String dbPassword = rs.getString("password");
                return dbPassword.equals(password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public Account getAccount(int id) {
        String sql = "SELECT username FROM account WHERE id = ?;";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                return new Account(id, username);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}