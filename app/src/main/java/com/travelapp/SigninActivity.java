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
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.travelapp.Models.SigninModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SigninActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword, signupphone, bio;
    TextView loginRedirectText;
    private Button btnUpload;
    private ImageView imageView;
    private Uri filePath;
    TextView signupButton, uploadimage;
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
        btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.imgView);
        uploadimage = findViewById(R.id.uploadimage);
        bio = findViewById(R.id.bio);
        ImageView togglePassword = findViewById(R.id.togglePassword);

        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                int inputType = (signupPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ?
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD :
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

                signupPassword.setInputType(inputType);
                // Move cursor to the end of the text
                signupPassword.setSelection(signupPassword.getText().length());

                // Change the visibility toggle icon
                togglePassword.setImageResource(
                        (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ?
                                R.drawable.visible :
                                R.drawable.hide
                );
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnUpload.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        uploadimage.setOnClickListener(new View.OnClickListener() {
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

        // Set up Firebase Database references
        DatabaseReference countryReference = FirebaseDatabase.getInstance().getReference("country");
        DatabaseReference cityReference = FirebaseDatabase.getInstance().getReference("city");

        // Set up the listener for the countries
        countryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> countryList = new ArrayList<>();

                for (DataSnapshot countrySnapshot : dataSnapshot.getChildren()) {
                    String countryName = countrySnapshot.child("countryname").getValue(String.class);
                    countryList.add(countryName);
                }

                ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                        SigninActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        countryList
                );

                countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCountry.setAdapter(countryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SigninActivity.this, "Failed to load countries", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the listener for the selected country
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCountryName = parentView.getItemAtPosition(position).toString();
                String selectedCountryCode = getCountryCodeFromSelectedItem(selectedCountryName, spinnerCity);

                // Query cities based on the selected country code
                Query cityQuery = cityReference.orderByChild("countrycode").equalTo(selectedCountryCode);
                cityQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> cityList = new ArrayList<>();

                        for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                            String cityName = citySnapshot.child("cityname").getValue(String.class);
                            cityList.add(cityName);
                        }

                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                                SigninActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                cityList
                        );

                        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCity.setAdapter(cityAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SigninActivity.this, "Failed to load cities", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

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
                String imageurl = sharedPreferences.getString("image", "");

                // Generate a unique ID using push()
                DatabaseReference userRef = reference.push();
                String userId = userRef.getKey();

                // Get timestamp
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                SigninModel helperClass = new SigninModel(name, email, username, password, phone, country, city, imageurl, timestamp, userId, aboutbio);
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
                imageView.setVisibility(View.VISIBLE);
                btnUpload.setVisibility(View.VISIBLE);
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
                                    String imageurl = downloadUri.toString();

                                    SharedPreferences sharedPreferences = getSharedPreferences("imagedata", MODE_PRIVATE);
                                    SharedPreferences.Editor editors = sharedPreferences.edit();
                                    editors.putString("image", imageurl);
                                    editors.apply();

                                    // Update the imageUrl field in SigninModel
                                    updateImageUrlInModel(username, imageurl);

                                    // Save the imageUrl to the database directly
                                    reference.child(username).child("imageurl").setValue(imageurl);

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

    private void updateImageUrlInModel(String username, String imageurl) {
        DatabaseReference userRef = reference.child(username);
        userRef.child("imageurl").setValue(imageurl);
    }

    // Helper method to get the country code from the selected item in the spinnerCountry
    private String getCountryCodeFromSelectedItem(String selectedCountryName, Spinner spinnerCity) {
        DatabaseReference countryReference = FirebaseDatabase.getInstance().getReference("country");

        // Query the country node to get the country code based on the selected country name
        countryReference.orderByChild("countryname").equalTo(selectedCountryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot countrySnapshot : dataSnapshot.getChildren()) {
                            String countryCode = countrySnapshot.child("countrycode").getValue(String.class);

                            // Now that we have the country code, fetch and display cities for this country
                            fetchAndDisplayCities(spinnerCity, countryCode);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });

        return selectedCountryName;  // Default return if the country code is not found (Handle this accordingly)
    }

    private void fetchAndDisplayCities(Spinner spinnerCity, String countryCode) {
        DatabaseReference cityReference = FirebaseDatabase.getInstance().getReference("city");

        // Query the city node to get cities with the matching country code
        cityReference.orderByChild("countrycode").equalTo(countryCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> cityList = new ArrayList<>();

                        // Iterate through each child of the "city" node
                        for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                            // Get the value of the "cityname" field and add it to the list
                            String cityName = citySnapshot.child("cityname").getValue(String.class);
                            cityList.add(cityName);
                        }

                        // Create ArrayAdapter using the fetched data
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                                SigninActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                cityList
                        );

                        // Specify the layout to use when the list of choices appears
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // Apply the adapter to the spinner
                        spinnerCity.setAdapter(cityAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                        Toast.makeText(SigninActivity.this, "Failed to load cities", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
