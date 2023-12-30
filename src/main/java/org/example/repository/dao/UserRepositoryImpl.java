package org.example.repository.dao;

import org.example.exception.DatabaseAccessException;
import org.example.exception.EntityNotFoundException;
import org.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public  class UserRepositoryImpl implements UserRepository {
    private final Connection connection;

    private static final String SELECT_USER_BY_ID = "SELECT * FROM public.users WHERE id = ?";
    private static final String SELECT_USER_BY_NICKNAME = "SELECT * FROM public.users WHERE nickname = ?";
    private static final String INSERT_USER = "INSERT INTO public.users(name, nickname, birthday, password) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_PASSWORD = "UPDATE public.users SET password = ? WHERE id = ?";
    private static final String SELECT_USERS_BY_RATING = "SELECT * FROM public.users WHERE rating >= ?";
    private static final String CHECK_USER_EXISTENCE = "SELECT COUNT(*) FROM public.users WHERE nickname = ? AND password = ?";
    private static final String DELETE_USER_BY_ID = "DELETE FROM public.users WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM public.users";

    public UserRepositoryImpl(Connection connection) {

        this.connection = connection;
    }

    @Override
    public User save(User user) {
        return createUser(user);
    }

    @Override
    public User get(int id) {
        return getById(id);
    }

    @Override
    public boolean remove(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to remove user with id: " + id, e);
        }
    }

    @Override
    public User findByNickname(String nickname) {

        return getByNickname(nickname);
    }

    @Override
    public List<User> findAll() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }

            return users;
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to retrieve all users", e);
        }
    }

    @Override
    public List<User> findByRating(int minRating) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERS_BY_RATING)) {
            preparedStatement.setInt(1, minRating);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }

            return users;
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to retrieve users by rating criteria", e);
        }
    }

    @Override
    public boolean changePassword(int userId, String newPassword) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_PASSWORD)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to change password for user with id: " + userId, e);
        }
    }

    @Override
    public boolean existsByNicknameAndPassword(String nickname, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USER_EXISTENCE)) {
            preparedStatement.setString(1, nickname);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to check user existence for nickname: " + nickname, e);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .nickname(resultSet.getString("nickname"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .password(resultSet.getString("password"))
                .build();
    }

    private User createUser(User user) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getNickname());
            preparedStatement.setDate(3, Date.valueOf(user.getBirthday()));
            preparedStatement.setString(4, user.getPassword());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseAccessException("Creating user failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            try {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    return user;
                } else {
                    throw new DatabaseAccessException("Creating user failed, no ID obtained.");
                }
            } finally {
                generatedKeys.close();
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to create user", e);
        }
    }

    @Override
    public User getById(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapUser(resultSet);
            } else {
                throw new EntityNotFoundException("Користувача з ідентифікатором " + id + " не знайдено.");
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Не вдалося отримати користувача за ідентифікатором: " + id, e);
        }
    }

    @Override
    public User getByNickname(String nickname) {
        return null;
    }
}