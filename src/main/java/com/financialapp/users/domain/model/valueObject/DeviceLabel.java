package com.financialapp.users.domain.model.valueObject;

import java.util.Objects;

public record DeviceLabel(String value) {

    public DeviceLabel {
        Objects.requireNonNull(value, "Device label cannot be null");
        value = value.trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("Device label cannot be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Device label cannot exceed 100 characters");
        }
    }

    public static DeviceLabel fromUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return new DeviceLabel("Unknown device");
        }
        String browser = detectBrowser(userAgent);
        String os = detectOs(userAgent);

        if (browser == null && os == null) {
            return new DeviceLabel("Unknown device");
        } else if (browser != null && os != null) {
            return new DeviceLabel(browser + " · " + os);
        } else if (browser != null) {
            return new DeviceLabel(browser);
        } else {
            return new DeviceLabel(os);
        }
    }

    private static String detectBrowser(String ua) {
        if (ua.contains("Edg/") || ua.contains("Edge/")) {
            return "Edge";
        }
        if (ua.contains("OPR/") || ua.contains("Opera/")) {
            return "Opera";
        }
        if (ua.contains("Chrome/")) {
            return "Chrome";
        }
        if (ua.contains("Firefox/")) {
            return "Firefox";
        }
        if (ua.contains("Safari/") && !ua.contains("Chrome/")) {
            return "Safari";
        }
        return null;
    }

    private static String detectOs(String ua) {
        if (ua.contains("Android")) {
            return "Android";
        }
        if (ua.contains("iPhone") || ua.contains("iPad") || ua.contains("iPod") || ua.contains("iOS")) {
            return "iOS";
        }
        if (ua.contains("Windows") || ua.contains("Win64") || ua.contains("WOW64")) {
            return "Windows";
        }
        if (ua.contains("Macintosh") || ua.contains("Mac OS X") || ua.contains("macOS")) {
            return "macOS";
        }
        if (ua.contains("Linux") || ua.contains("X11")) {
            return "Linux";
        }
        return null;
    }
}
