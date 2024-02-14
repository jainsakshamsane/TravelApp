package com.travelapp;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.travelapp.Adapters.Discover_Adapter;
import com.travelapp.Models.PlaceModel;

import java.util.ArrayList;
import java.util.List;

public class Discover_byPlace_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Discover_Adapter adapter;
    private List<PlaceModel> placeList; // Move the declaration here
    EditText searchtext;
    ImageView searchButton;
    private TextView placeTextView; // TextView to display selected country and season

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_by_place_activity);
        searchtext = findViewById(R.id.searchtext);
        searchButton = findViewById(R.id.searchButton);

        placeTextView = findViewById(R.id.place);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        placeList = new ArrayList<>(); // Initialize the list of places

        // Retrieve country and season information passed from Discover_Activity
        String country = getIntent().getStringExtra("country");
        String season = getIntent().getStringExtra("season");

        // Set the text of the TextView to the selected country and season
        if (country != null) {
            placeTextView.setText("Country: " + country);
        } else {
            placeTextView.setText("Season: " + season);
        }

        // Initialize Firebase Database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("places");

        // Add ValueEventListener to fetch data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placeList.clear(); // Clear previous data

                // Iterate through all data in dataSnapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlaceModel place = snapshot.getValue(PlaceModel.class);

                    // Filter places based on the selected country and season
                    if ((country != null && place.getCountry().equalsIgnoreCase(country)) ||
                            (season != null && place.getSeason().equalsIgnoreCase(season))) {
                        placeList.add(place);
                    }
                }

                // Initialize and set adapter for RecyclerView
                adapter = new Discover_Adapter(Discover_byPlace_Activity.this, placeList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // Search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        // TextWatcher for EditText
        searchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });
    }

    // Perform search method
    // Inside Discover_byPlace_Activity

    private void performSearch() {
        String searchText = searchtext.getText().toString().trim().toLowerCase();
        List<PlaceModel> filteredPlaceList = new ArrayList<>();

        // Filter placeList by name
        for (PlaceModel place : placeList) {
            if (place.getName().toLowerCase().contains(searchText)) {
                filteredPlaceList.add(place);
            }
        }

        // Update adapter with filtered list
        adapter.updateList(filteredPlaceList);
    }

}
