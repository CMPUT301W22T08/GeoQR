package com.example.geoqr;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class addQR extends AppCompatActivity implements askAddPictureFragment.OnfragInteractionListener{
    //DATABASE STILL HAVE TROUBLE SETTING UP

    // Define values thats gonna display on the xml
    private String UserName;
    private String QRhex; // I still don't get what this is asking, the really long one or the value?
    private Integer QRscore;
    private String Comments;
    private Location location = new Location(""); // get location somehow, work with Juliean

    // Define variables thats relateded with external links like db/intent
    private byte[] qr_byte;
    private CalculateScore sccore;

    // Define variables thats going to be used inside this class
    TextView UNdisplay;
    TextView QRhexDisplay;
    TextView QRscoreDisplay;
    TextView GeoDisplay;
    EditText comment;
    ImageView QRimg;
    Button add_btn;
    private Boolean add_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_qr_code);

        UNdisplay = findViewById(R.id.UserAutoFill);
        QRhexDisplay = findViewById(R.id.QRautoFill);
        QRscoreDisplay = findViewById(R.id.QRscoreAutoFill);
        GeoDisplay = findViewById(R.id.GeoSharable);
        comment = findViewById(R.id.comments);
        QRimg = findViewById(R.id.imageView);
        add_btn = findViewById(R.id.Addbtn);

        // ask user if want to add img
        new askAddPictureFragment().ask(add_img).show(getSupportFragmentManager(), "ASK_ADD_IMG");

        // Call from Leo camera class
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("");
        UserName = bundle.getString("");
        qr_byte = bundle.getByteArray("");

        //Calculate score
        sccore = new CalculateScore(qr_byte);
        QRscore = sccore.find_total();

        // Set text on display
        UNdisplay.setText(UserName);
        QRscoreDisplay.setText(QRscore);
        QRhexDisplay.setText(sccore.getQRhex());

        GeoDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // somehow get the location object form other class
                GeoDisplay.setText("the return string");
            }
        });

        // if user choose to add image to the
        if (add_img){
            // https://www.informit.com/articles/article.aspx?p=2423187
            QRimg.setImageBitmap(sccore.getBitmap());
            QRimg.setScaleType(ImageView.ScaleType.FIT_CENTER);

        }else{
            // how to set image to default/empty?
        }

        // get all data to the QR database and go to next page
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // refer to lab 5?
                // add all to database with QRhex as first doc and rest as attributes
                //         dealing with errors again
                // put all items in bundle and sent out to next class?
            }
        });

    }

    // return from addPicFragment
    @Override
    public void onOkPressed(boolean b) {
        add_img = b;
    }
}
