package com.example.geoqr;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
// LAST SUCCESFUL BUILD
public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private com.example.geoqr.databinding.ActivityMapBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String Latitude;
    private String Longitude;
    private LatLng tempVal;

    public LatLng tempPos;


    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("testing","Activated onCreate");

        binding = com.example.geoqr.databinding.ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



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
    protected void onResume(Bundle saveInstanceState) {
        super.onResume();
        Log.d("testing","Activated onResume");
        updateUserPosition();
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // Button to center on current position
        mMap.setMyLocationEnabled(true);

        updateUserPosition();
        db.collection("QR codes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot qrData : task.getResult()){
                    Latitude = (String) qrData.get("Latitude");
                    Longitude = (String) qrData.get("Longitude");

                    try {
                        tempVal = new LatLng(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
                        mMap.addMarker(new MarkerOptions().position(tempVal).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    } catch (Exception e){
                        //failed
                    }
                }
            }
        });


        // Add a marker in Sydney and move the camera
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currPos));
    }

    @SuppressLint("MissingPermission")
    private void updateUserPosition() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("testing","onSuccess Succeeded!");
                if (location != null) {
                    tempPos = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(tempPos));
                    Log.d("testing","Value after succeed: " + String.valueOf(tempPos));
                } else {
                    // default location. In case the listener fails to find a location
                    tempPos = new LatLng(53.523988,-113.527551);
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(tempPos));
                    Log.d("testing","Value after failure: " + String.valueOf(tempPos));
                }
            }
        });
    }

}



