package com.example.geoqr;

import java.io.Serializable;

public class AdminPlayerTuple implements Serializable {
    private String name;
    private int score;

    public AdminPlayerTuple(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
