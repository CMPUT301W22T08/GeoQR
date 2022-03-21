package com.example.geoqr;

import java.io.Serializable;

public class ListEntry implements Serializable {
    private String qrcode;
    private String content;
    private String score;

    public ListEntry(String qrcode, String content, String score) {
        this.qrcode = qrcode;
        this.content = content;
        this.score = score;

    }


    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
