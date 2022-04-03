package com.example.geoqr;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(totalScore);
        parcel.writeInt(highestScore);
        parcel.writeArray(qrs.toArray());
    }

    public String getName() {
        return name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public int getTotalQrs() {
        return qrs.size();
    }
}
