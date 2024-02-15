package com.travelapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.travelapp.Adapters.ImageAdapter;
import com.travelapp.Adapters.ExploreAdapter;
import com.travelapp.Models.TravelDestination;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Explore_Fragment extends Fragment {
    private RecyclerView imageRecyclerView;
    private RecyclerView explorePlacesRecyclerView;
    private ImageAdapter imageAdapter;
    private ExploreAdapter exploreAdapter;
    private List<TravelDestination> imageDestinationsList;
    private List<TravelDestination> exploreDestinationsList;
    private DatabaseReference databaseReference;

    ImageView userimage,discover;
    TextView noPlacesTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.explore_fragment, container, false);
        userimage = rootView.findViewById(R.id.userimage);
        discover = rootView.findViewById(R.id.discover);

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle arrowImageView click (e.g., navigate to another activity)
                startActivity(new Intent(getActivity(), Discover_Activity.class));
            }
        });
        noPlacesTextView = rootView.findViewById(R.id.noPlacesTextView);
        imageRecyclerView = rootView.findViewById(R.id.ImagesrecyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        imageDestinationsList = new ArrayList<>();
        imageAdapter = new ImageAdapter(getContext(), imageDestinationsList);
        imageRecyclerView.setAdapter(imageAdapter);

        explorePlacesRecyclerView = rootView.findViewById(R.id.exploreplacesrecycelrview);
        explorePlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        exploreDestinationsList = new ArrayList<>();
        exploreAdapter = new ExploreAdapter(getContext(), exploreDestinationsList);
        explorePlacesRecyclerView.setAdapter(exploreAdapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Fetch data from Firebase
        fetchImagesFromFirebase();
        fetchExplorePlacesFromFirebase();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString("imageurl", "");

        // Load image using Picasso
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.authorrr) // Placeholder image while loading
                .error(R.drawable.authorrr) // Image to show if loading fails
                .into(userimage);

        return rootView;
    }

    private void fetchImagesFromFirebase() {
        databaseReference.child("places").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageDestinationsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageUrl = snapshot.child("image").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    if (imageUrl != null && name != null) {
                        TravelDestination destination = new TravelDestination();
                        destination.setImage(imageUrl); // Set the image URL
                        destination.setName(name); // Set the name
                        imageDestinationsList.add(destination);
                    }
                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ExploreFragment", "Error fetching image data from Firebase: " + databaseError.getMessage());
            }
        });
    }


    private void fetchExplorePlacesFromFirebase() {
        String userCity = getUserCity();

        databaseReference.child("places").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exploreDestinationsList.clear();
                int matchingCitiesCount = 0; // Initialize the count of matching cities
                boolean placesFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TravelDestination destination = snapshot.getValue(TravelDestination.class);
                    if (destination != null) {
                        String placeCity = destination.getCity();
                        Log.d("ExploreFragment", "City from Firebase: " + placeCity); // Log the city name from Firebase
                        if (placeCity.trim().equalsIgnoreCase(userCity.trim())) {
                            exploreDestinationsList.add(destination);
                            placesFound = true;
                            matchingCitiesCount++; // Increment the count of matching cities
                        }
                    }
                }
                exploreAdapter.notifyDataSetChanged();

                // Show or hide the TextView based on whether places are found
                if (placesFound) {
                    noPlacesTextView.setVisibility(View.GONE); // Hide the TextView
                } else {
                    noPlacesTextView.setVisibility(View.VISIBLE); // Show the TextView
                }

                // Log the count of matching cities
                Log.d("ExploreFragment", "Number of matching cities: " + matchingCitiesCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ExploreFragment", "Error fetching explore places data from Firebase: " + databaseError.getMessage());
            }
        });
    }






    private String getUserCity() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdetails", Context.MODE_PRIVATE);
        String city = sharedPreferences.getString("city", "");
        Log.d("ExploreFragment", "User City: " + city); // Log the city name
        return city;
    }


}
