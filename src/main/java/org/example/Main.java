package org.example;

import org.example.model.ConsoleApp;
import org.example.model.User;
import org.example.repository.dao.*;
import org.example.service.GamePriceCalculator;
import org.example.service.GameService;
import org.example.service.UserService;
import org.example.repository.dao.PasswordEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        // Підключення до бази даних
        Connection connection =
                DriverManager.getConnection
                        ("jdbc:postgresql://localhost:5432/project_gamestore",
                                "admin", "qweqwepoipoi2321");

        // Створення репозиторіїв з підключенням до бази даних
        UserRepository userRepository = new UserRepositoryImpl(connection);
        AccountRepository accountRepository = new AccountRepositoryImpl();
        PasswordEncoder passwordEncoder = new PasswordEncoderImpl();

        GameRepository gameRepository = new GameRepositoryImpl(connection);
        GamePriceCalculator gamePriceCalculator = new org.example.GamePriceCalculatorImplementation();

        // Створення сервісів з репозиторіями та іншими залежностями
        GameService gameService = new GameService(gameRepository, accountRepository, gamePriceCalculator);
        UserService userService = new UserService(userRepository, accountRepository, passwordEncoder);

        // Вказати ідентифікатор користувача та новий пароль
        int userIdToChangePassword = 1; // Приклад значення
        String newPassword = "new_password"; // Приклад значення

        // Змінити пароль користувача
        boolean passwordChanged = userService.changePassword(userIdToChangePassword, newPassword);

        // Перевірити, чи вдалося змінити пароль
        if (passwordChanged) {
            System.out.println("Password changed successfully for user with ID: " + userIdToChangePassword);
        } else {
            System.out.println("Failed to change password for user with ID: " + userIdToChangePassword);
        }

        // Вказати нікнейм та пароль для перевірки
        String nicknameToCheck = "johnny"; // Приклад значення
        String passwordToCheck = "password"; // Приклад значення

        // Перевірити існування користувача за нікнеймом та паролем
        boolean userExists = userRepository.existsByNicknameAndPassword(nicknameToCheck, passwordToCheck);

        // Перевірити результат
        if (userExists) {
            System.out.println("User with nickname '" + nicknameToCheck + "' and specified password exists.");
        } else {
            System.out.println("User with nickname '" + nicknameToCheck + "' and specified password does not exist.");
        }

        int minRating = 50;

        // Отримати користувачів з мінімальним рейтингом
        List<User> usersWithMinRating = userService.getUsersWithMinRating(minRating);

        // Вивести інформацію про користувачів
        for (User user : usersWithMinRating) {
            System.out.println("User ID: " + user.getId() + ", Nickname: " + user.getNickname() + ", Rating: "
                    + user.getRating());
        }

        // Реєстрація нового користувача
        User registeredUser = userService.registerUser("John Doe", "johnny", "1990-01-01", "password");

        // Оновлення балансу рахунку для зареєстрованого користувача (наприклад, внесення $100)
        userService.updateAccountForUser(registeredUser.getId(), 100.0);

        // Вивід оновленого балансу користувача
        userService.printUserBalance(registeredUser.getId());

        // Створення консольного додатка та запуск програми
        ConsoleApp consoleApp = new ConsoleApp(userService);
        consoleApp.start();
    }
}
