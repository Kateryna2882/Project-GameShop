package org.example.repository.dao;

import org.example.exception.DatabaseAccessException;
import org.example.exception.DatabaseConnector;
import org.example.model.Account;
import org.example.repository.dao.AccountRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRepositoryImpl implements AccountRepository {
    private final Connection connection;

    private static final String SELECT_BALANCE = "SELECT amount FROM accounts WHERE user_id = ?";
    private static final String UPDATE_BALANCE = "UPDATE accounts SET amount = ? WHERE user_id = ?";

    public AccountRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnector.connect();
    }

    @Override
    public double getBalance(int userId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BALANCE)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("amount");
            } else {
                throw new DatabaseAccessException("Account not found for user with id: " + userId);
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to get balance for user with id: " + userId, e);
        }
    }

    @Override
    public void updateBalance(int userId, double newBalance) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BALANCE)) {
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to update balance for user with id: " + userId, e);
        }
    }

    @Override
    public Account getByUserId(int userId) {
        String SELECT_ACCOUNT_BY_USER_ID = "SELECT * FROM accounts WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_USER_ID)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int accountId = resultSet.getInt("id");
                double balance = resultSet.getDouble("amount");

                return new Account(accountId, balance);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to get account for user with id: " + userId, e);
        }
    }

    @Override
    public void update(Account account) {
    }
}
