package com.ticketblock.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyHelper {
    public static BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);

    }
}
