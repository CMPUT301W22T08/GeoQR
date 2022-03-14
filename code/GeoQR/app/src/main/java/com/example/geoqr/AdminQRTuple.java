package com.example.geoqr;

/**
 * It represents the data for each element in the QR ListView in the Admin page
 */
public class AdminQRTuple {
    private String contents;
    private String player; // Username
    private int Score;

    public AdminQRTuple(String contents, String player, int score) {
        this.contents = contents;
        this.player = player;
        Score = score;
    }

    /**
     * Getter for the content of a QR code
     * @return
     */
    public String getContents() {
        return contents;
    }

    /**
     * Getter for the associated player
     * @return
     */
    public String getPlayer() {
        return player;
    }

    /**
     * Getter for the score of the QR code
     * @return
     */
    public int getScore() {
        return Score;
    }
}
