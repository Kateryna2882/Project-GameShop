package org.example.test;

import org.example.model.GameStoreApp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStoreAppTest {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/game_store_test";
    private static final String DB_USER = "your_test_username";
    private static final String DB_PASSWORD = "your_test_password";

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    @BeforeAll
    static void setUp() throws SQLException {
        System.setOut(new PrintStream(outContent));

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS Users (" +
                        "id SERIAL PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "nickname VARCHAR(255) NOT NULL," +
                        "birthday DATE NOT NULL," +
                        "password VARCHAR(255) NOT NULL" +
                        ")");
            }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS Games (" +
                        "id SERIAL PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "release_date DATE NOT NULL," +
                        "rating INT NOT NULL," +
                        "price INT NOT NULL," +
                        "description TEXT" +
                        ")");
            }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS UserGames (" +
                        "user_id INT," +
                        "game_id INT," +
                        "FOREIGN KEY (user_id) REFERENCES Users(id)," +
                        "FOREIGN KEY (game_id) REFERENCES Games(id)," +
                        "PRIMARY KEY (user_id, game_id)" +
                        ")");
            }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS Accounts (" +
                        "id SERIAL PRIMARY KEY," +
                        "user_id INT NOT NULL," +
                        "amount INT NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES Users(id)" +
                        ")");
            }
        }
    }

    @AfterAll
    static void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testAddUser() {
        String input = "4\nJohn\njohn@example.com\npassword\n6\n";
        provideInput(input);
        GameStoreApp.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("User added successfully!"));
    }

    @Test
    void testAddGame() {
        String input = "4\nTest Game\nType\n5\n20\nDescription\n6\n";
        provideInput(input);
        GameStoreApp.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Game added successfully!"));
    }

    @Test
    void testAddAccount() {
        String input = "3\n1\n100\n4\n";
        provideInput(input);
        GameStoreApp.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Account added successfully!"));
    }

    private void provideInput(String data) {
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }
}
