package com.smartuis.server.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("The number of decimal places must be non-negative");
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public static final Random RANDOM = new Random();
}
