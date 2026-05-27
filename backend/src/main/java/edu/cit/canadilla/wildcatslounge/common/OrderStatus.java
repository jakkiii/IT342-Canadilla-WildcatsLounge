package edu.cit.canadilla.wildcatslounge.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum OrderStatus {
    PENDING("pending"),
    PREPARING("preparing"),
    READY("ready"),
    COMPLETED("completed");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static final Set<String> ALLOWED = Arrays.stream(values())
            .map(OrderStatus::getValue)
            .collect(Collectors.toUnmodifiableSet());

    public static String normalize(String status) {
        if (status == null || status.isBlank()) {
            return PENDING.value;
        }
        String normalized = status.trim().toLowerCase();
        if (!ALLOWED.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Invalid order status. Allowed: pending, preparing, ready, completed");
        }
        return normalized;
    }

    public static boolean isActive(String status) {
        String s = normalize(status);
        return PENDING.value.equals(s) || PREPARING.value.equals(s) || READY.value.equals(s);
    }
}
