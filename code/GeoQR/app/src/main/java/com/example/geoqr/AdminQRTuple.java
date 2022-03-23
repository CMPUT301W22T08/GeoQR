package com.example.geoqr;

/**
 * It represents the data for each element in the QR ListView in the Admin page
 */
public class AdminQRTuple {
    private String id;
    private String contents;
    private String player; // Username
    private int Score;

    public AdminQRTuple(String id, String contents, String player, int score) {
        this.id = id;
        this.contents = contents;
        this.player = player;
        Score = score;
    }
    /**
     * Getter for the id of the QR code
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for the content of the QR code
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
