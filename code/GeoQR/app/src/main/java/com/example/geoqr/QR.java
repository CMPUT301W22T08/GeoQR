package com.example.geoqr;

import java.io.Serializable;

public class QR implements Serializable {
    private String id;
    private String content;
    private int score;

    public QR(String id, String content, int score) {
        this.id = id;
        this.content = content;
        this.score = score;
    }

    public String getContent() {
        return content;
    }

}
