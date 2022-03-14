package com.example.geoqr;

import java.util.Random;

public class RandomString {
    private final String LETTERS = "abcdefghijklmnopqrstuvwxyz"; // wright a-z
    private final String NUMBERS = "0123456789";
    private final char[] ALPHANUMERIC = (LETTERS + LETTERS.toUpperCase() + NUMBERS).toCharArray();

    public String generateAlphaNumeric(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(ALPHANUMERIC[new Random().nextInt(ALPHANUMERIC.length)]);
        }
        return result.toString();
    }
}

