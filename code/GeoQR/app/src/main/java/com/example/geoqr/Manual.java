package com.example.geoqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class Manual extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_manual);

        FloatingActionButton back_btn = findViewById(R.id.manual_scan_btn);
        TextView manual_scanQR = findViewById(R.id.manual_scanQR);
        TextView manual_map = findViewById(R.id.manual_map);
        TextView manual_profile = findViewById(R.id.manual_profile);
        TextView manual_score = findViewById(R.id.manual_score_board);
        TextView manual_addQR = findViewById(R.id.manual_add_qr);

        String scanQR = "Scanning Page:\nScan your QR code or Barcode so that we could record them. Short click the button (finger print) on the bottom right side " +
                "navigates you to the profile page. Swipe left from this page, it navigates you to the score board where swipe right navigates you to the map page.\n" +
                "Shake the device to log out in any activity!";

        String map = "\nMap Activity:\nThe interactive map displays all valid QR codes that were uploaded by users.\n" +
                "\n" +
                "To use the Map, swipe right on the home screen (camera activity). Once in the map, the user can scroll around the map and see all QR code locations, indicated by a blue marker. " +
                "There are two button, one on the top right corner with a small cross-hair and one on the bottom right of the screen with a camera icon. The camera button (bottom right) returns the user to the home screen. The cross-hair button (top right) will center the map back on the users current position. " +
                "QR locations are updated each time the map is opened so the user always has the most up to date data.";

        String profile = "\nProfile Page:" +
                "\n" +
                "This page displays the highest and lowest QR scores that the user has, \n" +
                "as well as the sum of all there codes and total codes scanned.\n" +
                "\n" +
                "This page also shows a list of all the QR codes the user has scanned, with its content and score\n" +
                "\n" +
                "Click the Edit button beside Contact to add user information\n" +
                "\n" +
                "Clicking on Generate Login QR creates a QR that the user can scan to log into their account using a different device.\n" +
                "\n" +
                "Clicking on Generate Status QR creates a QR that allows friends of the user to view the users stats like highest and lowest score, total codes, etc.\n" +
                "\n" +
                "Clicking on a QR code in the list brings you to a page with more details related to that specific code. CLICK AND HOLDING on a code will prompt the user to delete that code from their account permanently. BE AWARE that this will change their score values displayed on their account.\n" +
                "\n" +
                "Clicking the Camera button at the bottom of the screen will return them to the Camera page for scanning QR codes or to go to other pages like leaderboard or maps.";

        String addQR = "\naddQR Page:\nAfter scanning the QR code, you will come to this page, called the AddQR page.\n" +
                "This page will display the details of the QR code like QR content, QR_hex_encryption result, QR score, Username, Longitute and Latitude (if you wish to add that on), image of QR code (if you wish to add that on), and personal comments on the QR code (if you wish to add that on).\n" +
                "\n" +
                "By opening the switch on the page where it says [Add Location], it will store the current location of your device, if it cannot get the location it will display null and give you a textbox at the bottom of the page saying cannot add Location.\n" +
                "If you choose not to store the location, it will not access the current location of your device.\n" +
                "\n" +
                "Click the part where it says [Click to add comments] to add personal comments on this QR code if you want.\n" +
                "\n" +
                "Click the [ADD IMAGE] to add image of the QR code by camera, which going to store in the database.\n" +
                "\n" +
                "Click the [DELETE IMAGE] to delete the current image of the QR code, which will not be stored in the database.\n" +
                "\n" +
                "Click the [ADD] to add this QR code to your total scores\n" +
                "\n" +
                "Click the [CANCEL] to stop the adding process of this QR code to your total scores";


        manual_scanQR.setText(scanQR);
        manual_map.setText(map);
        manual_profile.setText(profile);
        manual_addQR.setText(addQR);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scan = new Intent(Manual.this, ScanQR.class);
                scan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(scan);
            }
        });
    }
}
