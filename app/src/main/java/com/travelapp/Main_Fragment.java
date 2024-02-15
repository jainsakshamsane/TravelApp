package com.travelapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.travelapp.Adapters.BestPlacesAdapter;
import com.travelapp.Adapters.PlacesAdapter;
import com.travelapp.Models.PlaceModel;

import java.util.ArrayList;
import java.util.List;

public class Main_Fragment extends Fragment implements PlacesAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewBestPlaces;
    private PlacesAdapter adapter;
    private BestPlacesAdapter bestPlacesAdapter;
    private List<PlaceModel> placeList;
    private List<PlaceModel> bestPlaceList;
    private List<PlaceModel> originalBestPlaceList; // Store original list of best places
    EditText searchtext;
    ImageView searchButton, userimage, discover, month;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView12);
        recyclerViewBestPlaces = rootView.findViewById(R.id.recyclerView122);
        searchtext = rootView.findViewById(R.id.searchtext);
        searchButton = rootView.findViewById(R.id.searchButton);
        userimage = rootView.findViewById(R.id.userimage);
        discover = rootView.findViewById(R.id.discover);
        month = rootView.findViewById(R.id.month);

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle arrowImageView click (e.g., navigate to another activity)
                startActivity(new Intent(getActivity(), Discover_Activity.class));
            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle arrowImageView click (e.g., navigate to another activity)
                startActivity(new Intent(getActivity(), Month_fliter_Activity.class));
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManagerBestPlaces = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBestPlaces.setLayoutManager(layoutManagerBestPlaces);

        placeList = new ArrayList<>();
        bestPlaceList = new ArrayList<>();
        originalBestPlaceList = new ArrayList<>(); // Initialize original list of best places
        adapter = new PlacesAdapter(getContext(), placeList);
        bestPlacesAdapter = new BestPlacesAdapter(getContext(), bestPlaceList);
        recyclerView.setAdapter(adapter);
        recyclerViewBestPlaces.setAdapter(bestPlacesAdapter);

        return rootView;
    }

    @Override
    public void onItemClick(PlaceModel place) {
        // Handle item click
        saveSelectedPlaceId(place.getId());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve all places
        DatabaseReference placesReference = FirebaseDatabase.getInstance().getReference("places");
        placesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                placeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlaceModel place = dataSnapshot.getValue(PlaceModel.class);
                    placeList.add(place);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        // Retrieve best places
        DatabaseReference bestPlacesReference = FirebaseDatabase.getInstance().getReference("places");
        bestPlacesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bestPlaceList.clear();
                originalBestPlaceList.clear(); // Clear original list before updating
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlaceModel place = dataSnapshot.getValue(PlaceModel.class);
                    if (place != null && place.getBest_place().equals("Yes")) {
                        bestPlaceList.add(place);
                        originalBestPlaceList.add(place); // Update original list
                    }
                }
                bestPlacesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        // Search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        // Automatically perform search if search text is not empty
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

    private void performSearch() {
        String searchText = searchtext.getText().toString().trim().toLowerCase();
        List<PlaceModel> filteredPlaceList = new ArrayList<>();

        // Filter placeList by name
        for (PlaceModel place : placeList) {
            if (place.getName().toLowerCase().contains(searchText)) {
                filteredPlaceList.add(place);
            }
        }

        adapter.updateList(filteredPlaceList); // Update adapter with filtered places
        bestPlacesAdapter.updateList(originalBestPlaceList); // Update bestPlacesAdapter with original list of best places
    }

    private void saveSelectedPlaceId(String id) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("selected_place", MODE_PRIVATE).edit();
        editor.putString("place_id", id);
        editor.apply();
    }
}
