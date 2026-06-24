package pl.umcs.oop.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Klasa obsługująca komunikację z bazą danych SQLite.
 * Zarządza uwierzytelnianiem użytkowników i tabelą wyników (leaderboard).
 */
public class Database {

    // Adres połączenia z lokalną bazą danych SQLite (plik users.db)
    private static final String DB = "jdbc:sqlite:users.db";

    public Database() {
    }

    /**
     * Sprawdza poprawność danych logowania użytkownika.
     * Wykonuje zapytanie SELECT z parametrami login i hasło;
     * zwraca true jeśli znaleziono pasujący rekord w tabeli users.
     */
    public boolean authenticate(String login, String password) {
        String zapytanie = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB);
             PreparedStatement statement = conn.prepareStatement(zapytanie)) {

            statement.setString(1, login);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            return rs.next(); // true jeśli istnieje przynajmniej jeden pasujący wiersz

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Aktualizuje punkty graczy po zakończeniu pojedynku.
     * Zwycięzca otrzymuje +1 punkt, przegrany traci 1 punkt.
     * Oba UPDATE-y wykonywane są w ramach tego samego połączenia.
     */
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


    /**
     * Pobiera aktualną tabelę wyników z bazy danych.
     * Zwraca LinkedHashMap zachowującą kolejność wierszy (malejąco po punktach),
     * gdzie kluczem jest login gracza, a wartością jego liczba punktów.
     */
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
