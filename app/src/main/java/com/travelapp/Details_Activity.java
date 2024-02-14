package com.travelapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.travelapp.R;

public class Details_Activity extends AppCompatActivity {
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5;
    private PopupWindow popupWindow;
    private View popupView;


    @SuppressLint("MissingInflatedId")
    // Modify Details_Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);


        // Retrieve the name of the place passed from the previous activity
        Intent intent = getIntent();
        String placeName = intent.getStringExtra("placeName");

        // Query the Firebase Realtime Database to find the node with the matching name
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("places");
        Query query = databaseReference.orderByChild("name").equalTo(placeName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the details from the database
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String location = snapshot.child("city").getValue(String.class) + ", " + snapshot.child("country").getValue(String.class);
                        String price = snapshot.child("price").getValue(String.class);
                        String image = snapshot.child("image").getValue(String.class);
                        String information = snapshot.child("information").getValue(String.class);
                        String Service = snapshot.child("service").getValue(String.class);
                        String temp = snapshot.child("temprature").getValue(String.class);


                        // Initialize views
                        ImageView backgroundImageView = findViewById(R.id.backgroundImageView);
                        TextView NametextView1 = findViewById(R.id.NametextView1);
                        TextView LocationtextView2 = findViewById(R.id.LocationtextView2);
                        TextView PricetextView3 = findViewById(R.id.PricetextView3);
                        TextView Informationtextview = findViewById(R.id.Informationtextview);
                        TextView servicetextview = findViewById(R.id.servicetextview);
                        TextView rightTextView = findViewById(R.id.rightTextView);

                        // Set data to views
                        NametextView1.setText(name);
                        LocationtextView2.setText(location);
                        PricetextView3.setText(price);
                        Informationtextview.setText(information);
                        servicetextview.setText(Service);
                        rightTextView.setText(temp);

                        // Load image using Picasso
                        Picasso.get().load(image).into(backgroundImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Details_Activity", "Error querying database: " + databaseError.getMessage());
            }
        });


        // Initialize ImageViews
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);

        // Set click listeners
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView1.setImageResource(R.drawable.sunorange);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView2.setImageResource(R.drawable.areoplaneeee);
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView3.setImageResource(R.drawable.boatorange);
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView4.setImageResource(R.drawable.busorange);
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView5.setImageResource(R.drawable.bikeorange);
            }
        });
    }

    private void resetImages() {
        imageView1.setImageResource(R.drawable.sun);
        imageView2.setImageResource(R.drawable.areoplanewhite);
        imageView3.setImageResource(R.drawable.boattt);
        imageView4.setImageResource(R.drawable.busss);
        imageView5.setImageResource(R.drawable.bike);
    }
}