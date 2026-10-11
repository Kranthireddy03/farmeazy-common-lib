package com.farmeazy.common.logging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SensitiveDataMasker {

    public static final String FULL_MASK = "********";

    private static final Set<String> SECRET_KEYS = new HashSet<>(Arrays.asList(
            "password", "passwd", "pwd", "secret", "clientsecret",
            "accesstoken", "refreshtoken", "token",
            "authorization", "authheader", "apikey", "apisecret",
            "privatekey", "publickey", "jwt", "jwtsecret",
            "cookie", "sessioncookie", "sessionid",
            "csrf", "csrftoken", "otp", "cvv", "cardnumber",
            "razorpaykeysecret", "razorpaywebhooksecret", "smtppassword", "mailpassword",
            "authkey", "msg91authkey"
    ));

    private static final Pattern BEARER_PATTERN = Pattern.compile("(?i)\\b(Bearer\\s+)([A-Za-z0-9_\\-.]{6,})");
    private static final Pattern KEY_VALUE_PATTERN =
            Pattern.compile("(?i)(\\b(?:password|passwd|pwd|secret|client[_-]?secret|access[_-]?token|refresh[_-]?token|token|authorization|api[_-]?key|api[_-]?secret|private[_-]?key|encryption[_-]?key|jwt|jwt[_-]?secret|cookie|otp|cvv|card[_-]?number|bank[_-]?account(?:[_-]?number)?|account[_-]?number|auth[_-]?key)\\b[\"']?\\s*[=:]\\s*)(\"[^\"]*\"|'[^']*'|[^\\s,&|;]+)");
    private static final Pattern JWT_PATTERN = Pattern.compile("\\beyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\b");

    private SensitiveDataMasker() {}

    public static String maskSecret(String secret) {
        if (secret == null || secret.isEmpty()) return secret;
        return FULL_MASK;
    }

    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) return email;
        String val = email.trim();
        int atIdx = val.indexOf('@');
        if (atIdx <= 2 || val.length() <= 6) return FULL_MASK;
        String local = val.substring(0, atIdx);
        String domain = val.substring(atIdx);
        return local.substring(0, 2) + "**...**" + domain;
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) return phone;
        String val = phone.trim();
        if (val.length() <= 4) return FULL_MASK;
        String first = val.substring(0, 2);
        String last = val.substring(val.length() - 2);
        return first + "******" + last;
    }

    public static String maskMessage(String message) {
        if (message == null || message.isEmpty()) return message;

        // Mask Bearer tokens
        Matcher bearerMatcher = BEARER_PATTERN.matcher(message);
        String masked = bearerMatcher.replaceAll("$1" + FULL_MASK);

        // Mask key-value secrets
        Matcher kvMatcher = KEY_VALUE_PATTERN.matcher(masked);
        masked = kvMatcher.replaceAll("$1" + FULL_MASK);

        return JWT_PATTERN.matcher(masked).replaceAll(FULL_MASK);
    }

    public static boolean isSecretKey(String key) {
        if (key == null) return false;
        String clean = key.toLowerCase().replaceAll("[_\\-.]", "");
        return SECRET_KEYS.contains(clean);
    }
}
