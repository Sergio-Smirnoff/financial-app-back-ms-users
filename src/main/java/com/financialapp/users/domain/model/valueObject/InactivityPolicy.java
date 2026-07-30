package com.financialapp.users.domain.model.valueObject;

import java.time.Duration;
import java.time.LocalDateTime;

public record InactivityPolicy(Duration maxIdle) {

    public static InactivityPolicy fromMinutes(int minutes) {
        if (minutes == -1) {
            return new InactivityPolicy(null);
        }
        if (minutes == 30 || minutes == 120 || minutes == 480) {
            return new InactivityPolicy(Duration.ofMinutes(minutes));
        }
        throw new IllegalArgumentException("Invalid inactivity minutes: " + minutes + ". Allowed values: 30, 120, 480, -1");
    }

    public int toMinutes() {
        return maxIdle == null ? -1 : (int) maxIdle.toMinutes();
    }

    public boolean exceededBy(LocalDateTime lastSeen, LocalDateTime now) {
        if (maxIdle == null || lastSeen == null || now == null) {
            return false;
        }
        return Duration.between(lastSeen, now).compareTo(maxIdle) > 0;
    }
}
