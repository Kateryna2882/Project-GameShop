package org.example.service;

import org.example.model.Account;
import org.example.model.User;
import org.example.repository.dao.AccountRepository;
import org.example.repository.dao.PasswordEncoder;
import org.example.repository.dao.UserRepository;

import java.time.LocalDate;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    private String password;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void setPassword(String rawPassword) {
        this.password = passwordEncoder.encode(rawPassword);
    }

    public boolean checkPassword(String rawPassword) {
        return passwordEncoder.matches(rawPassword, password);
    }

    public UserService(UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;

    }

    public User registerUser(String name, String nickname, String birthday, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        User newUser = User.builder()
                .name(name)
                .nickname(nickname)
                .birthday(LocalDate.parse(birthday))
                .password(hashedPassword)
                .build();

        return userRepository.save(newUser);
    }

    public User loginUser(String nickname, String password) {
        User user = userRepository.findByNickname(nickname);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            Account userAccount = accountRepository.getByUserId(user.getId());
            user.setAccount(userAccount);
            return user;
        } else {
            return null; // User not found or incorrect password
        }
    }

    public void updateAccountForUser(int userId, double newAmount) {
        User user = userRepository.get(userId);

        if (user != null) {
            Account currentAccount = accountRepository.getByUserId(userId);

            if (currentAccount != null) {
                currentAccount.setAmount(newAmount);
                accountRepository.updateBalance(userId, newAmount);

                User updatedUser = user.withUpdatedAccount(currentAccount);

                System.out.println("Account updated successfully for user: " + updatedUser);
            } else {
                System.out.println("User does not have an account. Cannot update.");
            }
        } else {
            System.out.println("User not found. Cannot update account.");
        }
    }

    public void displayUserBalance(User user) {
        int userId = user.getId();
        double balance = accountRepository.getBalance(userId);
        System.out.println("User balance: " + balance);
    }

    public void printUserBalance(int userId) {
        double balance = accountRepository.getBalance(userId);
        System.out.println("User balance: " + balance);
    }
    public void deposit(int userId, double amountToAdd) {
        Account account = accountRepository.getByUserId(userId);
        account.deposit(amountToAdd);
        accountRepository.updateBalance(userId, account.getAmount());
    }

    public void withdraw(int userId, double amountToWithdraw) {
        Account account = accountRepository.getByUserId(userId);
        account.withdraw(amountToWithdraw);
        accountRepository.updateBalance(userId, account.getAmount());
    }
    public void removeUser(int userIdToRemove) {
        userRepository.remove(userIdToRemove);
        System.out.println("User with ID " + userIdToRemove + " has been removed.");
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public List<User> getUsersWithMinRating(int minRating) {
        return userRepository.findByRating(minRating);
    }
    public boolean changePassword(int userId, String newPassword) {

        User user = userRepository.getById(userId);

        // Перевірити, чи користувач існує
        if (user != null) {
            // Зашифрувати новий пароль
            String encryptedPassword = passwordEncoder.encode(newPassword);

            // Змінити пароль користувача
            return userRepository.changePassword(userId, encryptedPassword);
        }

        return false; // Помилка: користувач не знайдений
    }
}
