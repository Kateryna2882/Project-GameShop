CREATE TABLE Users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       nickname VARCHAR(255) NOT NULL,
                       birthday DATE NOT NULL,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE Games (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       release_date DATE NOT NULL,
                       rating INT NOT NULL,
                       cost INT NOT NULL,
                       description TEXT
);

CREATE TABLE UserGames (
                           user_id INT,
                           game_id INT,
                           FOREIGN KEY (user_id) REFERENCES Users(id),
                           FOREIGN KEY (game_id) REFERENCES Games(id),
                           PRIMARY KEY (user_id, game_id)
);
