package org.example.repository.dao;

import org.example.model.Game;

import java.util.List;

public interface GameRepository {
    Game get(int id);
    List<Game> findAll();
    void addGameToUser(int userId, int gameId);

    Game save(Game game);

    boolean remove(int id);

    Game update(Game game); // Повертає оновлену гру


    List<Game> findByCriteria(int minRating);

    List<Game> findReleasedAfter(String releaseDate);

    List<Game> findByName(String name);


    List<Game> getGamesByUserId(int userId);

}
