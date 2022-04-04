package com.example.geoqr;

import java.util.Random;

/**
 * A class to check the random generation of a string
 */
public class RandomString {
    private final String LETTERS = "abcdefghijklmnopqrstuvwxyz"; // wright a-z
    private final String NUMBERS = "0123456789";
    private final char[] ALPHANUMERIC = (LETTERS + LETTERS.toUpperCase() + NUMBERS).toCharArray();

    /**
     * Returns a random alpha numeric string of a given length
     * @param length
     * @return
     */
    public String generateAlphaNumeric(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(ALPHANUMERIC[new Random().nextInt(ALPHANUMERIC.length)]);
        }
        return result.toString();
    }
}

