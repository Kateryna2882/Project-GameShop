package org.example.repository.dao;

import org.example.model.Account;

public interface AccountRepository {
    double getBalance(int userId);

    void updateBalance(int userId, double newBalance);

    Account getByUserId(int userId);

    void update(Account account);
}
