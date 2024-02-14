package com.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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
import com.travelapp.Adapters.Month_Adapter;
import com.travelapp.Models.PlaceModel;

import java.util.ArrayList;
import java.util.List;

public class Each_Month_Fliter_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Month_Adapter adapter;
    private List<PlaceModel> places;
    private EditText searchtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eac_month_fliter_activity);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchtext = findViewById(R.id.searchtext);

        places = new ArrayList<>(); // Initialize the list of places

        // Retrieve the selected month passed from the previous activity
        Intent intent = getIntent();
        String selectedMonth = intent.getStringExtra("selected_month");

        // Query Firebase to fetch places where the month matches the selected month
        DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference("places");
        placesRef.orderByChild("month").equalTo(selectedMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list before adding new data
                places.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlaceModel place = snapshot.getValue(PlaceModel.class);
                    places.add(place);
                }

                // Initialize and set the adapter
                adapter = new Month_Adapter(Each_Month_Fliter_Activity.this, places);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Toast.makeText(Each_Month_Fliter_Activity.this, "Failed to retrieve data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set text change listener for searchEditText
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
                performSearch(s.toString());
            }
        });
    }

    private void performSearch(String searchText) {
        List<PlaceModel> filteredPlaces = new ArrayList<>();
        for (PlaceModel place : places) {
            if (place.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredPlaces.add(place);
            }
        }
        adapter.updateList(filteredPlaces);
    }
}
