package pl.umcs.oop;


import pl.umcs.oop.auth.Account;
import pl.umcs.oop.auth.AccountManager;
import pl.umcs.oop.database.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseConnection.connect("test.db");
        Statement stmt = DatabaseConnection.getConnection().createStatement();
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS account (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT
            )
        """);

        stmt.execute("""
            INSERT OR IGNORE INTO account (username, password)
            VALUES ("user1", "pass1"),
                   ("user2", "pass2"),
                   ("user3", "pass3");
        """);

        AccountManager manager = new AccountManager();
        manager.register("user5", "zaq1@WSX");

        ResultSet rs = stmt.executeQuery("""
            SELECT * FROM account;
        """);

        while (rs.next()) {
            System.out.println(rs.getString("username") + ": " + rs.getString("password") + " " + rs.getInt("id"));
        }

        System.out.println(manager.authenticate("user4", "pass4"));
        Account user3 = manager.getAccount(3);
        System.out.println(user3);
    }
}