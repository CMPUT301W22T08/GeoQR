package com.example.geoqr;

public class AdminQRTuple {
    private String contents;
    private String player; // Username
    private String Score;

    public AdminQRTuple(String contents, String player, String score) {
        this.contents = contents;
        this.player = player;
        Score = score;
    }

    public String getContents() {
        return contents;
    }

    public String getPlayer() {
        return player;
    }

    public String getScore() {
        return Score;
    }
}
