package org.example.service;

import org.example.model.Account;
import org.example.model.Game;
import org.example.repository.dao.AccountRepository;
import org.example.repository.dao.GameRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GameService {
    private final GameRepository gameRepository;
    private final AccountRepository accountRepository;
    private final GamePriceCalculator priceCalculator;

    public GameService(GameRepository gameRepository, AccountRepository accountRepository, GamePriceCalculator priceCalculator) {
        this.gameRepository = gameRepository;
        this.accountRepository = accountRepository;
        this.priceCalculator = priceCalculator;
    }

    public List<Game> getAllWithDiscount(float discount) {
        return gameRepository
                .findAll()
                .stream()
                .peek(game -> game.setPrice(priceCalculator.calculateDiscountedPrice((int) game.getPrice(), discount)))
                .collect(Collectors.toList());
    }

    public Game findById(int id) {
        return gameRepository.get(id);
    }

    public boolean buyGame(int userId, int gameId) {
        Game game = gameRepository.get(gameId);
        Account userAccount = accountRepository.getByUserId(userId);

        if (canUserAffordGame(userAccount, game) && !userAlreadyHasGame(userId, gameId)) {
            processGamePurchase(userAccount, game, userId);
            return true;
        } else {
            return false;
        }
    }

    public void addNewGame(String name, String releaseDate, int rating, double cost, String description) {
        Game newGame = new Game();
        newGame.setName(name);
        newGame.setReleaseDate(LocalDate.parse(releaseDate));
        newGame.setRating(rating);
        newGame.setCost(cost);
        newGame.setDescription(description);

        gameRepository.save(newGame);

        System.out.println("New game added: " + newGame.getName());
    }

    private boolean canUserAffordGame(Account userAccount, Game game) {
        return userAccount.getAmount() >= game.getPrice();
    }

    private void processGamePurchase(Account userAccount, Game game, int userId) {
        userAccount.setAmount(userAccount.getAmount() - game.getPrice());
        accountRepository.update(userAccount);
        gameRepository.addGameToUser(userId, game.getId());
    }


    private boolean userAlreadyHasGame(int userId, int gameId) {
        List<Game> userGames = gameRepository.getGamesByUserId(userId);
        return userGames.stream().anyMatch(game -> game.getId() == gameId);
    }

    public void removeGame(int gameId) {

        boolean isRemoved = gameRepository.remove(gameId);

        if (isRemoved) {

            System.out.println("Game with ID " + gameId + " has been removed.");
        } else {

            System.out.println("Unable to remove game with ID " + gameId + ". Game not found.");
        }
    }

    public void updateGameInformation(int gameId, String newName) {

        Game existingGame = gameRepository.get(gameId);

        if (existingGame != null) {

            existingGame.setName(newName);

            Game updatedGame = gameRepository.update(existingGame);

            if (updatedGame != null) {

                System.out.println("Game information has been updated for game with ID " + gameId);
            } else {

                System.out.println("Unable to update game information for game with ID " + gameId);
            }
        } else {

            System.out.println("Game with ID " + gameId + " not found.");
        }
    }

    public List<Game> findGamesByRatingCriteria(int minRating) {

        return gameRepository.findByCriteria(minRating);
    }

    public List<Game> findGamesReleasedAfter(String releaseDate) {
        return gameRepository.findReleasedAfter(releaseDate);
    }

    public List<Game> findGamesByName(String gameName) {
        return gameRepository.findByName(gameName);
    }
}
