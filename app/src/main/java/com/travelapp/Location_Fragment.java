package com.travelapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.travelapp.Adapters.LocationAdapter;
import com.travelapp.Adapters.Save_Adapter;
import com.travelapp.Models.Location;
import com.travelapp.Models.PlaceModel;

import java.util.ArrayList;
import java.util.List;

public class Location_Fragment extends Fragment {
    private RecyclerView locationRecyclerView;
    private LocationAdapter locationAdapter;
    private Save_Adapter saveAdapter;
    private RecyclerView recyclerView123;
    private List<Location> locationList;
    private EditText searchEditText;
    private List<Location> originalLocationList;
    private ImageView userimage;
    TextView noPlacesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_fragment, container, false);

        userimage = rootView.findViewById(R.id.userimage);
        noPlacesTextView = rootView.findViewById(R.id.noPlacesTextView);

        locationRecyclerView = rootView.findViewById(R.id.recyclerView12);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        locationList = new ArrayList<>();
        originalLocationList = new ArrayList<>();
        locationAdapter = new LocationAdapter(getContext(), locationList);
        locationRecyclerView.setAdapter(locationAdapter);

        searchEditText = rootView.findViewById(R.id.searchtext);
        setupSearchListener();
        recyclerView123 = rootView.findViewById(R.id.recyclerView123);
        recyclerView123.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchSavedPlacesFromFirebase();
        // Fetch locations from Firebase
        String userCity = getUserCity();
        fetchLocationFromFirebase(userCity);

        // Load user image using Picasso
        loadUserImage();

        return rootView;
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
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
                filterLocations(s.toString());
            }
        });
    }

    private void filterLocations(String searchText) {
        locationList.clear();
        for (Location location : originalLocationList) {
            if (location.getName().toLowerCase().contains(searchText.toLowerCase())) {
                locationList.add(location);
            }
        }
        locationAdapter.notifyDataSetChanged();
    }

    private void fetchLocationFromFirebase(String userCity) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("places");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationList.clear();
                originalLocationList.clear(); // Clear original list before updating
                boolean locationsFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Location location = snapshot.getValue(Location.class);
                    if (location.getCity().equals(userCity)) {
                        locationList.add(location);
                        originalLocationList.add(location); // Update original list
                        locationsFound = true;
                    }
                }
                locationAdapter.notifyDataSetChanged();

                // Show or hide the TextView based on whether locations are found
                if (locationsFound) {
                    noPlacesTextView.setVisibility(View.GONE); // Hide the TextView
                } else {
                    noPlacesTextView.setVisibility(View.VISIBLE); // Show the TextView
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LocationFragment", "Error fetching location data from Firebase: " + databaseError.getMessage());
            }
        });
    }
    private void fetchSavedPlacesFromFirebase() {
        // Get the current user ID
        String userId = getCurrentUserId();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("save");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PlaceModel> savedPlaces = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlaceModel place = snapshot.getValue(PlaceModel.class);
                    // Check if the place and userId are not null
                    if (place != null && place.getUserId() != null && place.getUserId().equals(userId)) {
                        savedPlaces.add(place);
                    }
                }
                // Pass the saved places list to the adapter
                saveAdapter = new Save_Adapter(getContext(), savedPlaces);
                recyclerView123.setAdapter(saveAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LocationFragment", "Error fetching saved places data from Firebase: " + databaseError.getMessage());
            }
        });
    }


    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        return sharedPreferences.getString("userid", "");
    }

    private String getUserCity() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("city", "");
    }

    private void loadUserImage() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString("imageurl", "");

        // Load user image using Picasso
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.authorrr)
                .error(R.drawable.authorrr)
                .into(userimage);
    }
}
