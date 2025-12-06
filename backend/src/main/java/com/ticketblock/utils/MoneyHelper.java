package com.ticketblock.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for handling monetary calculations and formatting.
 * 
 * Provides helper methods to ensure consistent handling of BigDecimal
 * amounts with proper rounding and scale for currency operations.
 */
public class MoneyHelper {
    public static BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);

    }
}
