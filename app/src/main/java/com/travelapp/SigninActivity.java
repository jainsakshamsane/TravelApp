package com.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.travelapp.Models.SigninModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class SigninActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword, signupphone, bio;
    TextView loginRedirectText;
    private Button btnSelect, btnUpload;
    private ImageView imageView;
    private Uri filePath;
    TextView signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 22;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        signupName = findViewById(R.id.name);
        signupEmail = findViewById(R.id.email);
        signupUsername = findViewById(R.id.username);
        signupPassword = findViewById(R.id.password);
        signupphone = findViewById(R.id.phone);
        loginRedirectText = findViewById(R.id.alreadyregistered);
        signupButton = findViewById(R.id.signupbutton);
        Spinner spinnerCountry = findViewById(R.id.spinnerCountry);
        Spinner spinnerCity = findViewById(R.id.spinnerCity);
        btnSelect = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.imgView);
        bio = findViewById(R.id.bio);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");
                uploadImage(signupUsername.getText().toString());
            }
        });

        // Create ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.countries_array,  // Create an array resource in your "res/values/arrays.xml" file
                android.R.layout.simple_spinner_dropdown_item
        );

        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.cities_array,  // Create an array resource in your "res/values/arrays.xml" file
                android.R.layout.simple_spinner_dropdown_item
        );

        // Specify the layout to use when the list of choices appears
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerCountry.setAdapter(countryAdapter);
        spinnerCity.setAdapter(cityAdapter);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();
                String phone = signupphone.getText().toString();
                String aboutbio = bio.getText().toString();
                String country = spinnerCountry.getSelectedItem().toString();
                String city = spinnerCity.getSelectedItem().toString();

                // Check if any field is left blank
                if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty() || aboutbio.isEmpty()) {
                    Toast.makeText(SigninActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing
                }

                // Email validation
                if (!isValidEmail(email)) {
                    Toast.makeText(SigninActivity.this, "Invalid email address. Use @gmail.com", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing
                }

                // Phone number validation
                if (!isValidPhoneNumber(phone)) {
                    Toast.makeText(SigninActivity.this, "Invalid phone number. Must be 10 digits or less", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing
                }

                SharedPreferences sharedPreferencess = getSharedPreferences("user_details", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencess.edit();
                editor.putString("username", username);
                editor.apply();

                SharedPreferences sharedPreferences = getSharedPreferences("imagedata", MODE_PRIVATE);
                String imageUrl = sharedPreferences.getString("image", "");

                // Generate a unique ID using push()
                DatabaseReference userRef = reference.push();
                String userId = userRef.getKey();

                // Get timestamp
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                SigninModel helperClass = new SigninModel(name, email, username, password, phone, country, city, imageUrl, timestamp, userId, aboutbio);
                reference.child(username).setValue(helperClass);

                Toast.makeText(SigninActivity.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();

                // Clear input fields after successful signup
                signupName.setText("");
                signupEmail.setText("");
                signupUsername.setText("");
                signupPassword.setText("");
                signupphone.setText("");
                bio.setText("");

                Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // Helper method to check if the email is in a valid format
    private boolean isValidEmail(String email) {
        return email.endsWith("@gmail.com");
    }

    // Helper method to check if the phone number is in a valid format
    private boolean isValidPhoneNumber(String phone) {
        return phone.length() <= 10;
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String username) {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    progressDialog.dismiss();
                                    String imageUrl = downloadUri.toString();

                                    SharedPreferences sharedPreferences = getSharedPreferences("imagedata", MODE_PRIVATE);
                                    SharedPreferences.Editor editors = sharedPreferences.edit();
                                    editors.putString("image", imageUrl);
                                    editors.apply();

                                    // Update the imageUrl field in SigninModel
                                    updateImageUrlInModel(username, imageUrl);

                                    // Save the imageUrl to the database directly
                                    reference.child(username).child("imageUrl").setValue(imageUrl);

                                    Toast.makeText(SigninActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SigninActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void updateImageUrlInModel(String username, String imageUrl) {
        DatabaseReference userRef = reference.child(username);
        userRef.child("imageUrl").setValue(imageUrl);
    }
}
