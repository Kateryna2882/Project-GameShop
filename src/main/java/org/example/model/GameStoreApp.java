package org.example.model;
import java.sql.*;
import java.util.Scanner;

public class GameStoreApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/game_store";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            initializeDatabase(connection);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. User Management");
                System.out.println("2. Game Management");
                System.out.println("3. Account Management");
                System.out.println("4. Exit");

                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Clear the buffer

                switch (choice) {
                    case 1:
                        manageUsers(connection, scanner);
                        break;
                    case 2:
                        manageGames(connection, scanner);
                        break;
                    case 3:
                        manageAccounts(connection, scanner);
                        break;
                    case 4:
                        System.out.println("Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Users (" +
                            "id SERIAL PRIMARY KEY," +
                            "name VARCHAR(255) NOT NULL," +
                            "nickname VARCHAR(255) NOT NULL," +
                            "birthday DATE NOT NULL," +
                            "password VARCHAR(255) NOT NULL" +
                            ")"
            );

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Games (" +
                            "id SERIAL PRIMARY KEY," +
                            "name VARCHAR(255) NOT NULL," +
                            "release_date DATE NOT NULL," +
                            "rating INT NOT NULL," +
                            "price INT NOT NULL," +
                            "description TEXT" +
                            ")"
            );

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS UserGames (" +
                            "user_id INT," +
                            "game_id INT," +
                            "FOREIGN KEY (user_id) REFERENCES Users(id)," +
                            "FOREIGN KEY (game_id) REFERENCES Games(id)," +
                            "PRIMARY KEY (user_id, game_id)" +
                            ")"
            );

            System.out.println("Database initialization complete.");
        }
    }

    private static void displayAllUsers(Connection connection) {
        String query = "SELECT * FROM Users";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("List of all users:");

            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");

                System.out.println("User ID: " + userId);
                System.out.println("Username: " + username);
                System.out.println("Email: " + email);
                System.out.println("---------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayAllAccounts(Connection connection) {
        String query = "SELECT * FROM accounts";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Список всіх рахунків:");

            while (resultSet.next()) {
                int accountId = resultSet.getInt("id");
                int userId = resultSet.getInt("user_id");
                int amount = resultSet.getInt("amount");

                System.out.println("Account ID: " + accountId);
                System.out.println("User ID: " + userId);
                System.out.println("Amount: " + amount);
                System.out.println("---------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void displayAllGames(Connection connection) {
        String query = "SELECT * FROM Games";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("Game ID: " + resultSet.getInt("id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Release Date: " + resultSet.getDate("release_date"));
                System.out.println("Rating: " + resultSet.getInt("rating"));
                System.out.println("Price: " + resultSet.getInt("price"));
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println("-----------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void findAccountByUserId(Connection connection, Scanner scanner) {
        System.out.print("Введіть ID користувача для пошуку рахунку: ");
        int userId = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера

        String query = "SELECT * FROM accounts WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Рахунок знайдено:");
                    System.out.println("Account ID: " + resultSet.getInt("id"));
                    System.out.println("User ID: " + resultSet.getInt("user_id"));
                    System.out.println("Amount: " + resultSet.getInt("amount"));
                } else {
                    System.out.println("Рахунок не знайдено для користувача з ID: " + userId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addAccount(Connection connection, Scanner scanner) {
        System.out.print("Введіть ID користувача для створення нового рахунку: ");
        int userId = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера

        System.out.print("Введіть початкову суму на рахунку: ");
        int initialAmount = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера

        String query = "INSERT INTO accounts (user_id, amount) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, initialAmount);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Рахунок створено успішно!");
            } else {
                System.out.println("Не вдалося створити рахунок.");
            }

            // Отримання згенерованого ID рахунку (якщо потрібно)
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int accountId = generatedKeys.getInt(1);
                    System.out.println("ID нового рахунку: " + accountId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void findUserById(Connection connection, Scanner scanner) {
        System.out
                .print("Enter the user ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        String query = "SELECT * FROM Users WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("User found:");
                    System.out.println("User ID: " + resultSet.getInt("id"));
                    System.out.println("Username: " + resultSet.getString("username"));
                    System.out.println("Email: " + resultSet.getString("email"));
                } else {
                    System.out.println("User not found with ID: " + userId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void findUserByNickname(Connection connection, Scanner scanner) {
        System.out.print("Enter the user nickname: ");
        String userNickname = scanner.nextLine();

        String query = "SELECT * FROM Users WHERE username = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userNickname);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("User found:");
                    System.out.println("User ID: " + resultSet.getInt("id"));
                    System.out.println("Username: " + resultSet.getString("username"));
                    System.out.println("Email: " + resultSet.getString("email"));
                } else {
                    System.out.println("User not found with nickname: " + userNickname);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void findGameById(Connection connection, Scanner scanner) {
        System.out.print("Enter the game ID: ");
        int gameId = scanner.nextInt();
        scanner.nextLine();  // Clear the buffer

        String query = "SELECT * FROM Games WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Game found:");
                    System.out.println("Game ID: " + resultSet.getInt("id"));
                    System.out.println("Name: " + resultSet.getString("name"));
                    System.out.println("Release Date: " + resultSet.getDate("release_date"));
                    System.out.println("Rating: " + resultSet.getInt("rating"));
                    System.out.println("Price: " + resultSet.getInt("price"));
                    System.out.println("Description: " + resultSet.getString("description"));
                } else {
                    System.out.println("Game not found with ID: " + gameId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void findGameByName(Connection connection, Scanner scanner) {
        System.out.print("Enter the game name: ");
        String gameName = scanner.nextLine();

        String query = "SELECT * FROM Games WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, gameName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Game found:");
                    System.out.println("Game ID: " + resultSet.getInt("id"));
                    System.out.println("Name: " + resultSet.getString("name"));
                    System.out.println("Release Date: " + resultSet.getDate("release_date"));
                    System.out.println("Rating: " + resultSet.getInt("rating"));
                    System.out.println("Price: " + resultSet.getInt("price"));
                    System.out.println("Description: " + resultSet.getString("description"));
                } else {
                    System.out.println("Game not found with name: " + gameName);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void addGame(Connection connection, Scanner scanner) {
        System.out.print("Enter the game name: ");
        String name = scanner.nextLine();

        System.out.print("Enter the game type: ");
        String type = scanner.nextLine();

        System.out.print("Enter the game rating: ");
        int rating = scanner.nextInt();
        scanner.nextLine();  // Clear the buffer

        System.out.print("Enter the game price: ");
        int price = scanner.nextInt();
        scanner.nextLine();  // Clear the buffer

        String query = "INSERT INTO games (name, type, rating, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type);
            preparedStatement.setInt(3, rating);
            preparedStatement.setInt(4, price);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Game added successfully!");

                // Retrieve the generated ID of the game
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int gameId = generatedKeys.getInt(1);
                        System.out.println("ID of the new game: " + gameId);
                    }
                }
            } else {
                System.out.println("Failed to add the game.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void deleteGame(Connection connection, Scanner scanner) {
        System.out.print("Enter the game ID to delete: ");
        int gameId = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character

        String query = "DELETE FROM games WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Game deleted successfully!");
            } else {
                System.out.println("No game found with ID: " + gameId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addUser(Connection connection, Scanner scanner) {
        System.out.print("Enter user name: ");
        String username = scanner.nextLine();

        System.out.print("Enter user email: ");
        String email = scanner.nextLine();

        System.out.print("Enter user password: ");
        String password = scanner.nextLine();

        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User added successfully!");

                // Retrieve the generated ID of the user
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        System.out.println("ID of the new user: " + userId);
                    }
                }
            } else {
                System.out.println("Failed to add user.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteUser(Connection connection, Scanner scanner) {
        System.out.print("Enter the user ID to delete: ");
        int userId = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character

        String query = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("No user found with ID: " + userId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void manageUsers(Connection connection, Scanner scanner) {
        while (true) {
            System.out.println("1. Переглянути користувачів");
            System.out.println("2. Знайти користувача за ID");
            System.out.println("3. Знайти користувача за нікнеймом");
            System.out.println("4. Додати нового користувача");
            System.out.println("5. Видалити користувача");
            System.out.println("6. Повернутися до головного меню");

            System.out.print("Оберіть опцію: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Clear the buffer

            switch (choice) {
                case 1:
                    displayAllUsers(connection);
                    break;
                case 2:
                    findUserById(connection, scanner);
                    break;
                case 3:
                    findUserByNickname(connection, scanner);
                    break;
                case 4:
                    addUser(connection, scanner);
                    break;
                case 5:
                    deleteUser(connection, scanner);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private static void manageGames(Connection connection, Scanner scanner) {
        while (true) {
            System.out.println("1. Переглянути ігри");
            System.out.println("2. Знайти гру за ID");
            System.out.println("3. Знайти гру за назвою");
            System.out.println("4. Додати нову гру");
            System.out.println("5. Видалити гру");
            System.out.println("6. Повернутися до головного меню");

            System.out.print("Оберіть опцію: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Clear the buffer

            switch (choice) {
                case 1:
                    displayAllGames(connection);
                    break;
                case 2:
                    findGameById(connection, scanner);
                    break;
                case 3:
                    findGameByName(connection, scanner);
                    break;
                case 4:
                    addGame(connection, scanner);
                    break;
                case 5:
                    deleteGame(connection, scanner);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private static void manageAccounts(Connection connection, Scanner scanner) {
        while (true) {
            System.out.println("1. Переглянути рахунки");
            System.out.println("2. Знайти рахунок за ID користувача");
            System.out.println("3. Додати новий рахунок");
            System.out.println("4. Повернутися до головного меню");

            System.out.print("Оберіть опцію: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Clear the buffer

            switch (choice) {
                case 1:
                    displayAllAccounts(connection);
                    break;
                case 2:
                    findAccountByUserId(connection, scanner);
                    break;
                case 3:
                    addAccount(connection, scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }
}
