package org.example.repository.dao;

import org.example.exception.DatabaseAccessException;
import org.example.model.Game;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameRepositoryImpl implements GameRepository {
    private final Connection connection;

    private static final String SELECT_BY_ID = "SELECT * FROM public.games WHERE id = ?";
    private static final String SQL_INSERT_GAME =
            "INSERT INTO public.games(name, type, rating, price) VALUES(?, ?, ?, ?) RETURNING id";
    private static final String REMOVE = "DELETE FROM public.games WHERE id = ?";
    private static final String UPDATE =
            "UPDATE public.games SET name=?, type=?, rating=?, price=? WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM public.games";
    private static final String SELECT_BY_RATING_CRITERIA = "SELECT * FROM public.games WHERE rating >= ?";

    public GameRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Game get(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID)) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractGame(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to get game with id: " + id, e);
        }
    }

    @Override
    public Game save(Game game) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_GAME, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, game.getName());
            preparedStatement.setString(2, game.getType());
            preparedStatement.setInt(3, game.getRating());
            preparedStatement.setInt(4, (int) game.getPrice());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    game.setId(generatedKeys.getInt(1));
                    return game;
                } else {
                    throw new DatabaseAccessException("Failed to get generated key for the new game");
                }
            } else {
                throw new DatabaseAccessException("Failed to save game, no rows affected");
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to save game", e);
        }
    }

    @Override
    public boolean remove(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(REMOVE)) {
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to remove game with id: " + id, e);
        }
    }

    @Override
    public Game update(Game game) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setString(1, game.getName());
            preparedStatement.setString(2, game.getType());
            preparedStatement.setDouble(3, game.getRating()); // Use setDouble for double values
            preparedStatement.setDouble(4, game.getPrice());  // Use setDouble for double values
            preparedStatement.setInt(5, game.getId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return game;
            } else {
                throw new DatabaseAccessException("Failed to update game with id: " + game.getId() + ", no rows affected");
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to update game with id: " + game.getId(), e);
        }
    }

    @Override
    public List<Game> findAll() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Game> games = new LinkedList<>();
            while (resultSet.next()) {
                games.add(extractGame(resultSet));
            }

            return games;
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to retrieve all games", e);
        }
    }

    @Override
    public List<Game> findByCriteria(int minRating) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_RATING_CRITERIA)) {
            preparedStatement.setInt(1, minRating);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Game> games = new LinkedList<>();
            while (resultSet.next()) {
                games.add(extractGame(resultSet));
            }

            return games;
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to retrieve games by rating criteria", e);
        }
    }

    @Override
    public List<Game> findReleasedAfter(String releaseDate) {
        // Implement this method based on your needs
        return null;
    }

    @Override
    public List<Game> findByName(String name) {
        // Implement this method based on your needs
        return null;
    }

    @Override
    public List<Game> getGamesByUserId(int userId) {
        String SELECT_GAMES_BY_USER_ID = "SELECT g.* FROM games g JOIN user_games ug ON g.id = ug.game_id WHERE ug.user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAMES_BY_USER_ID)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Game> userGames = new ArrayList<>();
            while (resultSet.next()) {
                userGames.add(extractGame(resultSet));
            }

            return userGames;
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to get games for user with id: " + userId, e);
        }
    }

    private Game extractGame(ResultSet resultSet) throws SQLException {
        return Game.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .type(resultSet.getString("type"))
                .rating(resultSet.getInt("rating"))
                .price(resultSet.getInt("price"))
                .build();
    }
    @Override
    public void addGameToUser(int userId, int gameId) {
        String INSERT_USER_GAME = "INSERT INTO user_games(user_id, game_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_GAME)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, gameId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to add game to user with id: " + userId, e);
        }
    }
}
