package com.ticketblock.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class MoneyHelper {
    public static BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);

    }

    public static BigInteger priceInCents(BigDecimal price){
        return price.multiply(BigDecimal.valueOf(100)) // moltiplica per 100
                .setScale(0, RoundingMode.HALF_UP) // arrotonda al centesimo più vicino
                .toBigInteger(); // converte in BigInteger
    }

}
