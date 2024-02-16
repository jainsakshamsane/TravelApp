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
    private String placeId;
    private String senderId;
    private String chatPathRecipient;
    private String senderName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_ativity);

        senderName = getIntent().getStringExtra("Name");
        senderId = getIntent().getStringExtra("senderid");
        placeId = getIntent().getStringExtra("placeid");

        TextView userNameTextView = findViewById(R.id.usernameTextView);
        userNameTextView.setText(senderName);

        placeholderTextView = findViewById(R.id.placeholderTextView);
        backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.userRecyclerView);
        callButton = findViewById(R.id.callButton);
        cameraImage = findViewById(R.id.cameraimage);

        SharedPreferences sharedPreferences = getSharedPreferences("userdetails", MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getString("userid", "");

        chatPathRecipient = senderId + " _ " + placeId;

        storageReference = FirebaseStorage.getInstance().getReference();
        messagesRefSender = FirebaseDatabase.getInstance().getReference(chatPathRecipient);
        messagesRefRecipient = FirebaseDatabase.getInstance().getReference(chatPathRecipient);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageAdapter = new UserMessageAdapter(messageList, loggedInUserId, this);
        recyclerView.setAdapter(messageAdapter);

        loadMessages();

        findViewById(R.id.sendbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement call functionality
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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

        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        String path = loggedInUserId + " _ " + placeId;
        DatabaseReference newMessageRef = messagesRef.child(path).push();
        String recipientPlace = getIntent().getStringExtra("placename");
        String messageId = newMessageRef.getKey();
        String time = (new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));

        Message message = new Message(loggedInUserId, senderUsername, recipientUserId, recipientName, "", System.currentTimeMillis(), messageId, recipientPlace, "", placeId,time);

        String imageName = "chat_images/" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageReference.child(imageName);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Chat_Activity.this.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    message.setImageUrl(uri.toString());
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

    private void loadMessages() {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(chatPathRecipient);
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Chat_Activity.this, "Failed to fetch messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String text = placeholderTextView.getText().toString().trim();
        if (!text.isEmpty()) {
            String recipientPlace = getIntent().getStringExtra("placename");
            SharedPreferences sharedPreferences = getSharedPreferences("userdetails", MODE_PRIVATE);
            loggedInUserId = sharedPreferences.getString("userid", "");
            String senderUsername = sharedPreferences.getString("username", "");

            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
            messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String path = loggedInUserId + " _ " + placeId;
                        DatabaseReference newMessageRef = messagesRef.child(path).push();
                        String time = (new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));

                        Message message = new Message(loggedInUserId, senderUsername, recipientUserId, recipientName, text,System.currentTimeMillis(), newMessageRef.getKey(), recipientPlace, "", placeId,time);
                        newMessageRef.setValue(message);
                        messageList.add(message);
                        messageAdapter.notifyDataSetChanged();
                        placeholderTextView.setText("");
                    } else {
                        Log.d("SendMessage", "Messages node doesn't exist");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("SendMessage", "Failed to read messages node", databaseError.toException());
                }
            });
        } else if (selectedImageUri != null) {
            uploadImageToFirebase(selectedImageUri);
        }
    }
}
