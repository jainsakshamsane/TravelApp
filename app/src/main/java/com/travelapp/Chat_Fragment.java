package com.travelapp;

import static android.content.Context.MODE_PRIVATE;

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
import com.squareup.picasso.Picasso;
import com.travelapp.Adapters.ChatsRecyclerViewAdapter;
import com.travelapp.Models.User;

import java.util.ArrayList;
import java.util.List;
public class Chat_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatsRecyclerViewAdapter adapter;
    private List<User> userList;
    EditText searchtext;
    ImageView searchButton;

    ImageView userimage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);
        userimage = rootView.findViewById(R.id.userimage);
        searchButton = rootView.findViewById(R.id.searchButton);
        searchtext = rootView.findViewById(R.id.searchtext);
        recyclerView = rootView.findViewById(R.id.ChatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>(); // Populate from Firebase

        // Retrieve user data from Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("places");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    userList.add(user);
                }
                // Update UI
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });

        adapter = new ChatsRecyclerViewAdapter(getContext(), userList);
        recyclerView.setAdapter(adapter);

        // Load image using Picasso
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString("imageurl", "");
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.authorrr) // Placeholder image while loading
                .error(R.drawable.authorrr) // Image to show if loading fails
                .into(userimage);

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

        return rootView;
    }

    private void performSearch() {
        String searchText = searchtext.getText().toString().trim().toLowerCase();
        List<User> filteredUserList = new ArrayList<>();

        // Filter userList by name
        for (User user : userList) {
            if (user.getName().toLowerCase().contains(searchText)) {
                filteredUserList.add(user);
            }
        }

        // Update adapter with filtered list
        adapter.updateList(filteredUserList);
    }
}
