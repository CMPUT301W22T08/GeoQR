package com.example.geoqr;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geoqr.databinding.ActivityMapBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // to be tested
        FloatingActionButton scan_btn = findViewById(R.id.scan);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(MapActivity.this, ScanQR.class);
                back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(back);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        /* Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

        //Create a list of pointGIS, and update the map with them
        ArrayList<pointGIS> pointList;

        pointList = createPointsList();

        updateMap(pointList);

        //set the camera to a specified position
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(53.523988,-113.527551)));
    }

    private void updateMap(ArrayList<pointGIS> locations) {
        /* Iterates through a list of pointGIS objects and adds them to the map */
        for (int i = 0; i < locations.size(); i++){
            LatLng coordinates = locations.get(i).getCoordinates();
            String name = locations.get(i).getTitle();
            mMap.addMarker(new MarkerOptions().position(coordinates).title(name));
        }
    }

    private ArrayList<pointGIS> createPointsList() {
        ArrayList<pointGIS> points = new ArrayList<>();
        // connect to firebase here

        // loop through firebase connection and create points for every one on the list

        //temporary hard points for testing, remove before commissioning
        pointGIS hlthSci = new pointGIS(new LatLng(53.520020,-113.525857), "Health Sciences Jubilee");
        pointGIS vvc = new pointGIS(new LatLng(53.523988,-113.527551), "Van Vilet Centre");
        points.add(hlthSci);
        points.add(vvc);

        return points;
    }
}