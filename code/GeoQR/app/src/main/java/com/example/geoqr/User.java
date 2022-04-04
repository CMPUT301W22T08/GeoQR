package com.example.geoqr;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * A class that represents an User
 */
public class User implements Parcelable {
    private String name;
    private int totalScore;
    private int highestScore;
    private ArrayList<QR> qrs;

    public User(String name, int totalScore, int highestScore, ArrayList<QR> qrs) {
        this.name = name;
        this.totalScore = totalScore;
        this.highestScore = highestScore;
        this.qrs = qrs;
    }

    /**
     * Constructor for Parceling a User object
     * @param in
     */
    protected User(Parcel in) {
        name = in.readString();
        totalScore = in.readInt();
        highestScore = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the user's attributes to a Parcel
     * @param parcel
     * @param i
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(totalScore);
        parcel.writeInt(highestScore);
        parcel.writeArray(qrs.toArray());
    }

    /**
     * Getter for user name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for user's total score
     * @return
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Getter for user's highest score
     * @return
     */
    public int getHighestScore() {
        return highestScore;
    }

    /**
     * Getter for user's total number of QRs
     * @return
     */
    public int getTotalQrs() {
        return qrs.size();
    }

    /**
     * Getter for user's QRs
     * @return
     */
    public ArrayList<QR> getQrs() {
        return qrs;
    }
}
