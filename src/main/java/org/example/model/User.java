package org.example.model;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@Setter
public class User {
    private int id;
    private String name;
    private String nickname;
    private LocalDate birthday;
    private String password;
    private Account account;
    private String rating;

    public User withUpdatedAccount(Account newAccount) {
        return new User(id, name, nickname, birthday, password, newAccount, rating);
    }
}
