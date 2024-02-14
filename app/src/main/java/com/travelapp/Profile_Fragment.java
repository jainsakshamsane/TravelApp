package com.travelapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.travelapp.Adapters.ImageAdapter;
import com.travelapp.Adapters.ProfilePost1Adapter;
import com.travelapp.Adapters.ProfilePostAdapter;
import com.travelapp.Models.TravelDestination;

import java.util.ArrayList;
import java.util.List;

public class Profile_Fragment extends Fragment {

    TextView bio, location, timestamp;
    private RecyclerView imageRecyclerView, imageRecyclerView1;
    private ProfilePostAdapter imageAdapter;
    private ProfilePost1Adapter imageAdapter1;
    TextView name, editprofile;
    ImageView profilephoto, settings;
    private List<TravelDestination> imageDestinationsList;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        name =  view.findViewById(R.id.name);
        profilephoto = view.findViewById(R.id.profilephoto);
        bio = view.findViewById(R.id.bio);
        location = view.findViewById(R.id.location);
        timestamp = view.findViewById(R.id.timestamp);
        settings = view.findViewById(R.id.settings);
        editprofile = view.findViewById(R.id.editprofile);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        imageRecyclerView = view.findViewById(R.id.recyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        imageDestinationsList = new ArrayList<>();
        imageAdapter = new ProfilePostAdapter(getContext(), imageDestinationsList);
        imageRecyclerView.setAdapter(imageAdapter);

        imageRecyclerView1 = view.findViewById(R.id.recyclerView1);
        imageRecyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        imageDestinationsList = new ArrayList<>();
        imageAdapter1 = new ProfilePost1Adapter(getContext(), imageDestinationsList);
        imageRecyclerView1.setAdapter(imageAdapter1);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Fetch data from Firebase
        fetchImagesFromFirebase();
        fetchImages1FromFirebase();

        SharedPreferences sharedPreferencess = requireActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        String fullName = sharedPreferencess.getString("fullname", "");
        String email = sharedPreferencess.getString("email", "");
        String username = sharedPreferencess.getString("username", "");
        String city = sharedPreferencess.getString("city", "");
        String country = sharedPreferencess.getString("country", "");
        String password = sharedPreferencess.getString("password", "");
        String phone = sharedPreferencess.getString("phone", "");
        String imageUrl = sharedPreferencess.getString("imageurl", "");
        String aboutbio = sharedPreferencess.getString("bio", "");
        String timestamps = sharedPreferencess.getString("timestamp", "");
        String userid = sharedPreferencess.getString("userid", "");

        name.setText(fullName);
        bio.setText(aboutbio);
        location.setText(city + ", " + country);
        // Assuming timestamps is the timestamp string "2024-02-09 16:02:03"
        String[] timestampParts = timestamps.split(" ");
        String datePart = timestampParts[0]; // Extract the date part

// Now set the datePart to the timestamp TextView
        timestamp.setText(datePart);

        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference("users");

        imageRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String pathofimage = ds.child("imageurl").getValue(String.class);

                    Picasso.get().load(pathofimage).into(profilephoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve images", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
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

    private void fetchImages1FromFirebase() {
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
                imageAdapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ExploreFragment", "Error fetching image data from Firebase: " + databaseError.getMessage());
            }
        });
    }
}
