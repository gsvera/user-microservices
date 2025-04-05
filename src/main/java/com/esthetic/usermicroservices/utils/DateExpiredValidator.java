package com.esthetic.usermicroservices.utils;

import java.time.LocalDateTime;
public class DateExpiredValidator {
    public static boolean isExpired(LocalDateTime validUntil, LocalDateTime now) {
        return now.isAfter(validUntil);
    }

    public static boolean isNotYetValid(LocalDateTime validFrom, LocalDateTime now) {
        return now.isBefore(validFrom);
    }

    public static boolean isCurrentlyValid(LocalDateTime validFrom, LocalDateTime validUntil, LocalDateTime now) {
        return !now.isBefore(validFrom) && !now.isAfter(validUntil);
    }
}
