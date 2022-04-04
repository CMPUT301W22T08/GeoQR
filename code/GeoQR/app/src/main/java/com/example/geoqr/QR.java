package com.example.geoqr;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A class to represent QRs
 */
public class QR implements Parcelable {
    private String id;
    private String content;
    private String player; // Username
    private int score;
    private Location loc;

    public QR(String id, String content, String player, int score) {
        this.id = id;
        this.content = content;
        this.player = player;
        this.score = score;
        this.loc = null;
    }

    /**
     * Class for constructing a Parcel from a QR object
     * @param in
     */
    protected QR(Parcel in) {
        id = in.readString();
        content = in.readString();
        player = in.readString();
        score = in.readInt();
        loc = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<QR> CREATOR = new Creator<QR>() {
        @Override
        public QR createFromParcel(Parcel in) {
            return new QR(in);
        }

        @Override
        public QR[] newArray(int size) {
            return new QR[size];
        }
    };

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
        return content;
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

    /**
     * Getter for the location of the QR
     * @return
     */
    public Location getLoc() {
        return loc;
    }

    /**
     * Sets the latitude & longitude of the QR's Location
     * @param latitude
     * @param longitude
     */
    public void setLoc(Double latitude, Double longitude) {
        this.loc = new Location(player);
        this.loc.setLatitude(latitude);
        this.loc.setLongitude(longitude);
    }

    /**
     * Checks if two QR codes are same based on the location
     * and the QR code content.
     * Threshold distance 10 meters
     * @param qr
     * @return
     */
    public boolean isSame(QR qr) {
        if (loc != null && qr.getLoc() != null && qr.getContent().equals(content)) {
            // Check nearness of the 2 locations
            return loc.distanceTo(qr.getLoc()) <= 10;
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(content);
        parcel.writeString(player);
        parcel.writeInt(score);
        parcel.writeParcelable(loc, i);
    }
}
