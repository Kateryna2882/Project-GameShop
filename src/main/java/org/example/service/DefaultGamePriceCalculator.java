package org.example.service;

public class DefaultGamePriceCalculator implements GamePriceCalculator {
    @Override
    public int calculateDiscountedPrice(int originalPrice, float discount) {
        double discountedPrice = originalPrice * (1 - discount);
        return (int) discountedPrice;
    }

}
