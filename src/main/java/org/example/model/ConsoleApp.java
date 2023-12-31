package org.example.model;

import org.example.service.UserService;

import java.util.Scanner;

public class ConsoleApp {
    private final UserService userService;
    private final Scanner scanner;

    public ConsoleApp(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to the game!");
        System.out.println("1. Log in");
        System.out.println("2. Register");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            default:
                System.out.println("Invalid choice. Exiting the program.");
        }
    }

    private void login() {
        System.out.print("Enter nickname: ");
        String nickname = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User loggedInUser = userService.loginUser(nickname, password);

        if (loggedInUser != null) {
            System.out.println("Login successful!");

            userService.displayUserBalance(loggedInUser);

        } else {
            System.out.println("Incorrect nickname or password. Exiting the program.");
        }
    }

    private void register() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter nickname: ");
        String nickname = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter birthday: ");
        String birthday = scanner.nextLine();

        User registeredUser = userService.registerUser(name, nickname, birthday, password);

        if (registeredUser != null) {
            System.out.println("Registration successful!");

            userService.displayUserBalance(registeredUser);

        } else {
            System.out.println("Failed to register. Exiting the program.");
        }
    }
}
