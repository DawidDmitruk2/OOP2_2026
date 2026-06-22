package pl.umcs.oop.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Database {

    private static final String DB = "jdbc:sqlite:users.db";

    public Database() {
    }

    public boolean authenticate(String login, String password) {
        String zapytanie = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB);
             PreparedStatement statement = conn.prepareStatement(zapytanie)) {

            statement.setString(1, login);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLeaderboard(String winner, String loser) {
        String zapytanieWinner = "UPDATE users SET points = points + 1 WHERE login = ?";
        String zapytanieLoser = "UPDATE users SET points = points - 1 WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(DB)) {

            try (PreparedStatement statement = conn.prepareStatement(zapytanieWinner)) {
                statement.setString(1, winner);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = conn.prepareStatement(zapytanieLoser)) {
                statement.setString(1, loser);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, Integer> getLeaderboard() {
        String zapytanie = "SELECT login, points FROM users ORDER BY points DESC";
        Map<String, Integer> leaderboard = new LinkedHashMap<>();

        try (Connection conn = DriverManager.getConnection(DB);
             PreparedStatement statement = conn.prepareStatement(zapytanie)) {

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                leaderboard.put(rs.getString("login"), rs.getInt("points"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return leaderboard;
    }

}