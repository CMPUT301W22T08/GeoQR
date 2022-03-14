package com.example.geoqr;

import java.io.Serializable;

public class ListEntry implements Serializable {
    private String qrcode;
    private String content;
    private String score;
    private String location;
    private String time;


    public ListEntry(String qrcode, String content, String score, String location, String time) {
        this.qrcode = qrcode;
        this.content = content;
        this.score = score;
        this.location = location;
        this.time = time;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
