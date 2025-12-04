package com.ticketblock.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyHelper {
    public static BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);

    }
}
