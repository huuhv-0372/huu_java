package com.huuhv.foodsndrinks.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SlugUtils {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private SlugUtils() {}

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = DIACRITICS.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ROOT)
                .replace("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("(^-+)|(-+$)", "");
    }
}

