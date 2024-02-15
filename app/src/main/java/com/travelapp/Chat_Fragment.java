package com.travelapp;

import static android.content.Context.MODE_PRIVATE;

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
import com.travelapp.Models.Chat;
import com.travelapp.Models.User;

import java.util.ArrayList;
import java.util.List;
public class Chat_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatsRecyclerViewAdapter adapter;
    private List<Chat> chatList;
    EditText searchtext;
    ImageView searchButton, userimage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);
        userimage = rootView.findViewById(R.id.userimage);
        searchButton = rootView.findViewById(R.id.searchButton);
        searchtext = rootView.findViewById(R.id.searchtext);
        recyclerView = rootView.findViewById(R.id.ChatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        String loggedInUserId = sharedPreferences.getString("userid", "");
        String loggedInUsername = sharedPreferences.getString("fullname", "");
        Log.e("Chat_Fragment", "messages: " + loggedInUsername);





        chatList = new ArrayList<>();

        // Get the Firebase database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Navigate to the "messages" node
        DatabaseReference messagesRef = databaseReference.child("Messages");

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Get the last child node (latest)
                    DataSnapshot latestChildSnapshot = null;
                    // Iterate through each child of the current messageSnapshot
                    for (DataSnapshot childSnapshot : messageSnapshot.getChildren()) {
                        latestChildSnapshot = childSnapshot;
                    }

                    // Access the data under the latest child node
                    if (latestChildSnapshot != null) {
                        Chat chat = latestChildSnapshot.getValue(Chat.class);

                        // Access the data under each child node
                        chat.setMessageId(latestChildSnapshot.child("messageId").getValue(String.class));
                        chat.setPlaceName(latestChildSnapshot.child("placeName").getValue(String.class));
                        chat.setRecipientId(latestChildSnapshot.child("recipientId").getValue(String.class));
                        chat.setRecipientName(latestChildSnapshot.child("recipientName").getValue(String.class));
                        chat.setSenderId(latestChildSnapshot.child("senderId").getValue(String.class));
                        chat.setSenderName(latestChildSnapshot.child("senderName").getValue(String.class));
                        chat.setText(latestChildSnapshot.child("text").getValue(String.class));

                        // Check if the logged-in user is the sender or recipient
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
                        String userId = sharedPreferences.getString("userid", "");
                        if (userId.equals(chat.getSenderId()) || userId.equals(chat.getRecipientId())) {
                            chatList.add(chat);
                        }
                    }
                }
                // Update the adapter after fetching all chats
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("Chat_Fragment", "Error retrieving messages: " + databaseError.getMessage());
            }
        });

        adapter = new ChatsRecyclerViewAdapter(getContext(), chatList,loggedInUserId,loggedInUsername);
        recyclerView.setAdapter(adapter);

        // Load user's image using Picasso
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
        List<Chat> filteredChatList = new ArrayList<>();

        // Filter chatList by relevant criteria
        for (Chat chat : chatList) {
            // Example filtering based on sender's name or recipient's name
            if (chat.getSenderName().toLowerCase().contains(searchText) || chat.getRecipientName().toLowerCase().contains(searchText)) {
                filteredChatList.add(chat);
            }
        }

        // Update adapter with filtered list
        adapter.updateList(filteredChatList);
    }
}
