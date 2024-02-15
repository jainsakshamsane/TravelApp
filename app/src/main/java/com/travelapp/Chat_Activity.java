package com.travelapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.travelapp.Adapters.UserMessageAdapter;
import com.travelapp.Models.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Chat_Activity extends AppCompatActivity {

    private EditText placeholderTextView;
    private ImageView backButton, callButton, cameraImage;
    private RecyclerView recyclerView;
    private UserMessageAdapter messageAdapter;
    private List<Message> messageList;
    private DatabaseReference messagesRefSender;
    private DatabaseReference messagesRefRecipient;
    private String loggedInUserId;
    private String recipientUserId, recipientName;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private StorageReference storageReference;
    TextView userNameTextView;
    String placeId;
    String placeid;
    String senderid;
    String chatPathrecipient;
    String senderName;
    private DatabaseReference messagesRefAdminSender;
    private DatabaseReference messagesRefUserRecipient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_ativity);
        userNameTextView = findViewById(R.id.usernameTextView);

        String name = getIntent().getStringExtra("placename");
        senderName = getIntent().getStringExtra("Name");
        senderid = getIntent().getStringExtra("senderid");
        placeId = getIntent().getStringExtra("placeid");
        Log.d("PlaceNameDebug", "Place Name: " + name);
        userNameTextView.setText(name);


        placeholderTextView = findViewById(R.id.placeholderTextView);
        backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.userRecyclerView);
        callButton = findViewById(R.id.callButton);
        cameraImage = findViewById(R.id.cameraimage);
        // Get logged-in user ID and name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userdetails", MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getString("userid", "");
        String loggedInUsername = sharedPreferences.getString("username", "");

        chatPathrecipient = senderid + " _ " + placeid ;
        Log.e("sender","sernder"+chatPathrecipient+"/n"+senderid);
        // Initialize Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        messagesRefSender = FirebaseDatabase.getInstance().getReference(chatPathrecipient);
        messagesRefRecipient = FirebaseDatabase.getInstance().getReference(chatPathrecipient);
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageAdapter = new UserMessageAdapter(messageList, loggedInUserId, this);
        recyclerView.setAdapter(messageAdapter);



        // Initialize Firebase
        storageReference = FirebaseStorage.getInstance().getReference();



        // Load messages
        loadMessages();

        // Send message button click listener
        findViewById(R.id.sendbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // Camera button click listener
        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        // Call button click listener
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement call functionality
            }
        });

        // Back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Get recipient info from admin node
        placeId = getIntent().getStringExtra("placeid");
        String chatPathrecipient = loggedInUserId + " _ " + placeId ;
        Log.e("ImageLoading", "Image Message : " + chatPathrecipient);
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(chatPathrecipient);
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
//                  // Load messages after setting up database references
                        Message message = messageSnapshot.getValue(Message.class);
                        if (message != null) {
                            if (!TextUtils.isEmpty(message.getImageUrl())) {
                                Log.e("ImageLoading", "Image Message added: " + message.getImageUrl()+"  "+message.getSenderId());
                            } else {
                                Log.e("ImageLoading", "Text Message added: " + message.getText());
                            }
                            messageList.add(message);
                        }


                        messageAdapter.notifyDataSetChanged();



                    }
                } else {
                    // Admin node doesn't exist
                    Log.d("UserChatActivity", "Admin node doesn't exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("UserChatActivity", "Failed to read admin node", databaseError.toException());
            }
        });


        messageAdapter = new UserMessageAdapter(messageList, loggedInUserId, this);
        recyclerView.setAdapter(messageAdapter);
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            uploadImageToFirebase(selectedImageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        SharedPreferences sharedPreferences = getSharedPreferences("userdetails", MODE_PRIVATE);
        String loggedInUserId = sharedPreferences.getString("userid", "");
        String senderUsername = sharedPreferences.getString("username", "");

        // Fetch recipient details from the admin node
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("admin");
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming there's only one admin node, you can remove the loop
                    DataSnapshot adminSnapshot = dataSnapshot.getChildren().iterator().next();
                    String recipientUserId = adminSnapshot.child("userid").getValue(String.class);
                    String recipientName = adminSnapshot.child("name").getValue(String.class);

                    // Set recipient details in the Message object
                    recipientUserId = recipientUserId;
                    recipientName = recipientName;

                    DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");

                    // Determine the path based on the recipient and user
                    String path = loggedInUserId + " _ " + placeId;

                    // Push the message to the appropriate path
                    DatabaseReference newMessageRef = messagesRef.child(path).push();

                    // Get recipient (place) name from the intent
                    String recipientPlace = getIntent().getStringExtra("placename");

                    String messageId = newMessageRef.getKey(); // Get the message ID
                    String time =(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));


                    // Create a new Message object with all details including the image URL and placename
                    Message message = new Message(loggedInUserId, senderUsername, recipientUserId, recipientName, "", System.currentTimeMillis(), messageId, recipientPlace,"", placeId,time);

                    // Set the message text (which is empty in this case)
                    // You can set any other fields if needed

                    String imageName = "chat_images/" + System.currentTimeMillis() + ".jpg";
                    StorageReference imageRef = storageReference.child(imageName);

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(Chat_Activity.this.getContentResolver(), imageUri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        // Compress the bitmap with quality 50 (adjust as needed)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = imageRef.putBytes(data);
                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Update the message with the image URL
                                message.setImageUrl(uri.toString());

                                // Set the value of the new message node to the updated Message object
                                newMessageRef.setValue(message);
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(Chat_Activity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(Chat_Activity.this, "Error compressing image", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("SendMessage", "Failed to read admin node", databaseError.toException());
            }
        });
    }

    private void updateImageMessage(DatabaseReference senderRef, DatabaseReference recipientRef, String imageUrl) {
        senderRef.child("imageUrl").setValue(imageUrl);
        recipientRef.child("imageUrl").setValue(imageUrl);
    }


    private void loadMessages() {
        if (messagesRefAdminSender != null) {
            messagesRefAdminSender.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    addMessagesToAdapter(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Chat_Activity.this, "Failed to fetch sender messages", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (messagesRefUserRecipient != null) {
            messagesRefUserRecipient.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    addMessagesToAdapter(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Chat_Activity.this, "Failed to fetch recipient messages", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void addMessagesToAdapter(DataSnapshot dataSnapshot) {
        messageList.clear();

        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
            Message message = messageSnapshot.getValue(Message.class);
            if (message != null) {
                if (!TextUtils.isEmpty(message.getImageUrl())) {
                    Log.d("ImageLoading", "Image Message added: " + message.getImageUrl());
                } else {
                    Log.d("ImageLoading", "Text Message added: " + message.getText());
                }
                messageList.add(message);
            }
        }

        messageAdapter.notifyDataSetChanged();
    }


    private void sendMessage() {
        String text = placeholderTextView.getText().toString().trim();
        if (!text.isEmpty()) {
            // Get recipient (place) name from the intent
            String recipientPlace = getIntent().getStringExtra("placename");
            SharedPreferences sharedPreferences = getSharedPreferences("userdetails", MODE_PRIVATE);
            loggedInUserId = sharedPreferences.getString("userid", "");
            String senderUsername = sharedPreferences.getString("username", "");

            // Get admin ID and name from the database
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("admin");
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Assuming there's only one admin node, you can remove the loop
                        DataSnapshot adminSnapshot = dataSnapshot.getChildren().iterator().next();
                        recipientUserId = adminSnapshot.child("userid").getValue(String.class);
                        recipientName = adminSnapshot.child("name").getValue(String.class);

                        // Create a DatabaseReference for the messages
                        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");

                        // Determine the path based on the recipient and user
                        String path = loggedInUserId + " _ " + placeId;

                        // Push the message to the appropriate path
                        DatabaseReference newMessageRef = messagesRef.child(path).push();
                        String time = (new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));

                        // Create the message object with placename
                        Message message = new Message(loggedInUserId, senderUsername, recipientUserId, recipientName, text, System.currentTimeMillis() ,newMessageRef.getKey(), recipientPlace, "", placeId,time);

                        // Set the message values
                        newMessageRef.setValue(message);

                        // Add the message directly to the message list
                        messageList.add(message);

                        // Notify the adapter about the data change
                        messageAdapter.notifyDataSetChanged();

                        placeholderTextView.setText("");
                    } else {
                        // Admin node doesn't exist
                        Log.d("SendMessage", "Admin node doesn't exist");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    Log.e("SendMessage", "Failed to read admin node", databaseError.toException());
                }
            });
        } else if (selectedImageUri != null) {
            // If an image is selected, upload it to Firebase Storage and save the URL in the database
            uploadImageToFirebase(selectedImageUri);
        }
    }


}