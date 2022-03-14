package com.example.geoqr;

public class QRsubcollection {
    private String comment;
    private String content;
    private String location;
    private String score;
    private String time;

    public QRsubcollection() {
        //public no-arg constructor needed
    }

    public QRsubcollection(String comment, String content, String location, String score, String time) {
        this.comment = comment;
        this.content = content;
        this.location = location;
        this.score = score;
        this.time = time;
    }
}