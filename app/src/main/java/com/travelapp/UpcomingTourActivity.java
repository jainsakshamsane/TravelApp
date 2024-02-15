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
import android.widget.LinearLayout;
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
import com.travelapp.Adapters.AllHistoryAdapter;
import com.travelapp.Adapters.BestPlacesAdapter;
import com.travelapp.Adapters.HistoryAdapter;
import com.travelapp.Adapters.UpcomingPlacesAdapter;
import com.travelapp.Models.PlaceModel;
import com.travelapp.Models.PlacesModel;
import com.travelapp.Models.TransactionModel;
import com.travelapp.Models.UpcomingModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingTourActivity extends AppCompatActivity {

    ImageView back;
    private RecyclerView locationRecyclerView, recyclerView12;
    UpcomingPlacesAdapter adapter;
    List<PlacesModel> placeModelList = new ArrayList<>();
    List<UpcomingModel> UpcomingModelList = new ArrayList<>();
    HistoryAdapter paymentHistoryAdapter;
    AllHistoryAdapter allHistoryAdapter;
    LinearLayout linear6;
    List<TransactionModel> paymentList = new ArrayList<>();
    TextView noPlacesTextView;
    String userid;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcomingtour_activity);

        back = findViewById(R.id.back);
        noPlacesTextView = findViewById(R.id.noPlacesTextView);
        locationRecyclerView = findViewById(R.id.recyclerView12);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(UpcomingTourActivity.this, LinearLayoutManager.VERTICAL, false));

        recyclerView12 = findViewById(R.id.recyclerView122);
        recyclerView12.setLayoutManager(new LinearLayoutManager(UpcomingTourActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView12.setHasFixedSize(true);
        linear6 = findViewById(R.id.linear6);

        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payments");
        DatabaseReference placeRef = FirebaseDatabase.getInstance().getReference("places");

        adapter = new UpcomingPlacesAdapter(this, UpcomingModelList);
        allHistoryAdapter = new AllHistoryAdapter(this, placeModelList);


        SharedPreferences sharedPreferencess = getSharedPreferences("userdetails", MODE_PRIVATE);
         userid = sharedPreferencess.getString("userid", "");

        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cardsSnapshot) {
                // paymentList.clear(); // Clear the list before adding new data

                for (DataSnapshot cardSnapshot : cardsSnapshot.getChildren()) {
                    String userId = cardSnapshot.child("userId").getValue(String.class);

                    if (userId.equals(userid)) {
                        String placename = cardSnapshot.child("placeName").getValue(String.class);

                        String placeid = cardSnapshot.child("id").getValue(String.class);
                        String people = cardSnapshot.child("numberOfPeople").getValue(String.class);
                        Log.e("inside", "inside payment" + placename + "**" + placeid + "**" + people);
                        // Assuming you have a Payment class to represent the data
                        TransactionModel payment = new TransactionModel(placename, userId, placeid, people);
                        paymentList.add(payment);
                    }
                }


                // Fetch and process data from payments node
                placeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot paymentsSnapshot) {
                        for (DataSnapshot placeSnapshot : paymentsSnapshot.getChildren()) {
                            for (TransactionModel payment : paymentList) {
                                String nameofplace = placeSnapshot.child("name").getValue(String.class);
                                String idplace = placeSnapshot.child("id").getValue(String.class);
                                Log.e("inside", "data  " + payment.getId() + "**" + idplace);
                                // Proceed to check the placeName condition
                                if (payment.getId().equals(idplace)) {
                                    // Example: Fetch placeName from payments

                                    String placename = placeSnapshot.child("name").getValue(String.class);
                                    String city = placeSnapshot.child("city").getValue(String.class);
                                    String country = placeSnapshot.child("country").getValue(String.class);
                                    String price = placeSnapshot.child("price").getValue(String.class);
                                    String image = placeSnapshot.child("image").getValue(String.class);
                                    String startdate = placeSnapshot.child("date_start").getValue(String.class);
                                    String noofdays = placeSnapshot.child("no_of_days").getValue(String.class);
                                    String season = placeSnapshot.child("season").getValue(String.class);
                                    Log.e("inside", "placeModelList" + placename + "**" + city + "**" + country + "**" + idplace);
                                    PlacesModel placeModel = new PlacesModel(placename, city, country, price, image, idplace);
                                    placeModelList.add(placeModel);
                                    // Compare the start date with today's date
                                    if (isStartDateGreaterThanToday(startdate)) {
                                        Log.e("inside", "UpcomingModelList" + placename + "**" + city + "**" + noofdays + "**" + idplace);
                                        UpcomingModel upcomingmodel = new UpcomingModel(placename, city, country, price, image, noofdays, season, idplace);
                                        UpcomingModelList.add(upcomingmodel);
                                        // Set the adapter after cards data is retrieved

                                    } else {
//                                            PlacesModel placeModel = new PlacesModel(placename, city, country, price, image, idplace);
//                                            placeModelList.add(placeModel);
                                    }
                                }
                            }
                            if (UpcomingModelList != null) {
                                locationRecyclerView.setAdapter(adapter);
                                locationRecyclerView.setVisibility(View.VISIBLE);
                                noPlacesTextView.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            } else {
                                locationRecyclerView.setVisibility(View.GONE);
                                noPlacesTextView.setVisibility(View.VISIBLE);
                            }
                            if (placeModelList != null) {
                                recyclerView12.setAdapter(allHistoryAdapter);
                                recyclerView12.setVisibility(View.VISIBLE);
                                linear6.setVisibility(View.GONE);
                                allHistoryAdapter.notifyDataSetChanged();
                            } else {
                                recyclerView12.setVisibility(View.GONE);
                                linear6.setVisibility(View.VISIBLE);
                            }
                        }
                        // Notify the adapter that the data has changed
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
}
