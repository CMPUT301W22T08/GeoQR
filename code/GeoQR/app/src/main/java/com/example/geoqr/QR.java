package com.example.geoqr;

/**
 * It represents the data for each element in the QR ListView in the Admin page
 */
public class QR {
    private String id;
    private String contents;
    private String player; // Username
    private int score;

    public QR(String id, String contents, String player, int score) {
        this.id = id;
        this.contents = contents;
        this.player = player;
        this.score = score;
    }
    /**
     * Getter for the id of the QR code
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for the content of the QR code
     * @return contents
     */
    public String getContent() {
        return contents;
    }

    /**
     * Getter for the associated player
     * @return player
     */
    public String getPlayer() {
        return player;
    }

    /**
     * Getter for the score of the QR code
     * @return Score
     */
    public int getScore() {
        return score;
    }
}
