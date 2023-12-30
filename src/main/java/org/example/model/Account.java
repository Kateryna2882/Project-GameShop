package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for representing user account.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    /**
     * Unique identifier of the user.
     */
    private int userId;

    /**
     * Amount of money in the user's account.
     */
    private double amount;

    /**
     * Increases the amount of money in the user's account.
     *
     * @param amountToAdd Amount to add.
     */
    public void deposit(double amountToAdd) {

        this.amount += amountToAdd;
    }

    /**
     * Decreases the amount of money in the user's account.
     *
     * @param amountToWithdraw Amount to withdraw.
     * @throws IllegalArgumentException if the amount to withdraw is greater than the available balance.
     */
    public void withdraw(double amountToWithdraw) {
        if (amountToWithdraw > this.amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        this.amount -= amountToWithdraw;
    }


}
