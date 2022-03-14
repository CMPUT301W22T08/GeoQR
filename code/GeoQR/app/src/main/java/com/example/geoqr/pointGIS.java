package com.example.geoqr;

import com.google.android.gms.maps.model.LatLng;

public class pointGIS {
    /* Class to store data for map markers */
    LatLng coordinates;
    String title;


    public pointGIS(LatLng coordinates, String title){
        this.coordinates = coordinates;
        this.title = title;
    }

    public LatLng getCoordinates() { return coordinates; }

    public void setCoordinates(LatLng coordinates) {this.coordinates = coordinates;}

    public String getTitle() {return this.title;}

    public void setTitle(String title) {this.title = title;}
}
