package com.example.geoqr;

import java.io.Serializable;

/**
 * It represents the data for each element in the Player ListView in the Admin page
 */
public class AdminPlayerTuple implements Serializable {
    private String name;
    private int score;

    public AdminPlayerTuple(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Getter for name of the player
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for score of the player
     * @return
     */
    public int getScore() {
        return score;
    }
}
