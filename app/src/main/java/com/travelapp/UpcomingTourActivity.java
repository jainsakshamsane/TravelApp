package com.travelapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.travelapp.Adapters.BestPlacesAdapter;
import com.travelapp.Adapters.HistoryAdapter;
import com.travelapp.Adapters.UpcomingPlacesAdapter;
import com.travelapp.Models.PlaceModel;
import com.travelapp.Models.PlacesModel;
import com.travelapp.Models.TransactionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingTourActivity extends AppCompatActivity {

    ImageView back;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5;
    private RecyclerView locationRecyclerView;
    UpcomingPlacesAdapter adapter;
    List<PlacesModel> placeModelList = new ArrayList<>();
    HistoryAdapter paymentHistoryAdapter;
    List<TransactionModel> paymentList = new ArrayList<>();
    TextView noPlacesTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcomingtour_activity);

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

        back = findViewById(R.id.back);
        noPlacesTextView = findViewById(R.id.noPlacesTextView);
        locationRecyclerView = findViewById(R.id.recyclerView12);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(UpcomingTourActivity.this, LinearLayoutManager.VERTICAL, false));

        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payments");
        DatabaseReference placeRef = FirebaseDatabase.getInstance().getReference("places");

        adapter = new UpcomingPlacesAdapter(this, placeModelList);
        paymentHistoryAdapter = new HistoryAdapter(this, placeModelList);

        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cardsSnapshot) {
                paymentList.clear(); // Clear the list before adding new data

                for (DataSnapshot cardSnapshot : cardsSnapshot.getChildren()) {
                    String placename = cardSnapshot.child("placeName").getValue(String.class);
                    String userId = cardSnapshot.child("userId").getValue(String.class);

                    // Assuming you have a Payment class to represent the data
                    TransactionModel payment = new TransactionModel(placename, userId);
                    paymentList.add(payment);
                }

                // Fetch and process data from payments node
                placeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot paymentsSnapshot) {
                        for (TransactionModel payment : paymentList) {
                            for (DataSnapshot placeSnapshot : paymentsSnapshot.getChildren()) {
                                String nameofplace = placeSnapshot.child("name").getValue(String.class);

                                SharedPreferences sharedPreferencess = getSharedPreferences("userdetails", MODE_PRIVATE);
                                String userid = sharedPreferencess.getString("userid", "");

                                // Check if userId matches the currently logged-in user's userid
                                if (payment.getUserId().equals(userid)) {
                                    // Proceed to check the placeName condition
                                    if (payment.getPlaceName().equals(nameofplace)) {
                                        // Example: Fetch placeName from payments
                                        String placename = placeSnapshot.child("name").getValue(String.class);
                                        String city = placeSnapshot.child("city").getValue(String.class);
                                        String country = placeSnapshot.child("country").getValue(String.class);
                                        String price = placeSnapshot.child("price").getValue(String.class);
                                        String image = placeSnapshot.child("image").getValue(String.class);
                                        String startdate = placeSnapshot.child("date_start").getValue(String.class);
                                        String noofdays = placeSnapshot.child("no_of_days").getValue(String.class);
                                        String season = placeSnapshot.child("season").getValue(String.class);

                                        // Compare the start date with today's date
                                        if (isStartDateGreaterThanToday(startdate)) {
                                            Log.d("PlaceMethodActivity", "Linked Data - UserId: " + payment.getPlaceName() + ", Name: " + placename + city + country + price + startdate + noofdays + season);

                                            PlacesModel placeModel = new PlacesModel(placename, city, country, price, image, noofdays, season);
                                            placeModelList.add(placeModel);

                                            // Set the adapter after cards data is retrieved
                                            locationRecyclerView.setAdapter(adapter);
                                            locationRecyclerView.setVisibility(View.VISIBLE);
                                            noPlacesTextView.setVisibility(View.GONE);
                                        }
                                    }
                                } else {
                                    locationRecyclerView.setVisibility(View.GONE);
                                    noPlacesTextView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UpcomingTourActivity.this, "Failed to retrieve payments", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpcomingTourActivity.this, "Failed to retrieve places", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpcomingTourActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    // Function to check if the start date is greater than today
    private boolean isStartDateGreaterThanToday(String startDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date currentDate = new Date();
        try {
            Date startDateObject = dateFormat.parse(startDate);
            return startDateObject != null && startDateObject.after(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void resetImages() {
        imageView1.setImageResource(R.drawable.sun);
        imageView2.setImageResource(R.drawable.areoplanewhite);
        imageView3.setImageResource(R.drawable.boattt);
        imageView4.setImageResource(R.drawable.busss);
        imageView5.setImageResource(R.drawable.bike);
    }
}
