package com.travelapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    ImageView back, profileimage;
    TextView seecountry, seecity;
    EditText edit_name, edit_email, edit_password, edit_phone, edit_bio;
    TextView savechanges;

    private Button btnUpload;
    private ImageView imageView;
    private Uri filePath;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 22;

    String fullName, email, city, country, password, phone, aboutbio, username, imageUrl;

    Spinner spinnerCountry, spinnerCity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile_activity);

        spinnerCountry = findViewById(R.id.spinnerCountry);
        spinnerCity = findViewById(R.id.spinnerCity);

        database = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");

        back = findViewById(R.id.back);
        savechanges = findViewById(R.id.savechanges);
        edit_name = findViewById(R.id.editname);
        edit_phone = findViewById(R.id.editphone);
        edit_email = findViewById(R.id.editemail);
        edit_password = findViewById(R.id.editpassword);
        edit_bio = findViewById(R.id.editbio);
        profileimage = findViewById(R.id.profileimage);
        btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.imgView);
        seecountry = findViewById(R.id.seecountry);
        seecity = findViewById(R.id.seecity);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        btnUpload.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming username is a variable that holds the current user's username
                String currentUsername = getUsernameFromSharedPreferences();

                if (currentUsername != null) {
                    uploadImage(currentUsername);
                } else {
                    // Handle the case where the username is not available
                    Toast.makeText(EditProfileActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                }
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
                        EditProfileActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        countryList
                );

                countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCountry.setAdapter(countryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, "Failed to load countries", Toast.LENGTH_SHORT).show();
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
                                EditProfileActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                cityList
                        );

                        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCity.setAdapter(cityAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(EditProfileActivity.this, "Failed to load cities", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
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

        SharedPreferences sharedPreferencess = getSharedPreferences("userdetails", MODE_PRIVATE);
        fullName = sharedPreferencess.getString("fullname", "");
        email = sharedPreferencess.getString("email", "");
        username = sharedPreferencess.getString("username", "");
        city = sharedPreferencess.getString("city", "");
        country = sharedPreferencess.getString("country", "");
        password = sharedPreferencess.getString("password", "");
        phone = sharedPreferencess.getString("phone", "");
        imageUrl = sharedPreferencess.getString("imageurl", "");
        aboutbio = sharedPreferencess.getString("bio", "");
        String timestamps = sharedPreferencess.getString("timestamp", "");

        edit_name.setText(fullName);
        edit_phone.setText(phone);
        edit_email.setText(email);
        edit_password.setText(password);
        edit_bio.setText(aboutbio);
        seecountry.setText(country);
        seecity.setText(city);
        Picasso.get().load(imageUrl).into(profileimage);
        // Find the index of the country and city in their respective arrays
        int countryIndex = ((ArrayAdapter<String>) spinnerCountry.getAdapter()).getPosition(country);
        int cityIndex = ((ArrayAdapter<String>) spinnerCity.getAdapter()).getPosition(city);

// Set the default selected item based on the found index
        spinnerCountry.setSelection(countryIndex);
        spinnerCity.setSelection(cityIndex);

        savechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
                Toast.makeText(EditProfileActivity.this, "Changes saved successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveChanges() {
        // Update name
        updateName();

        // Update phone
        updatePhone();

        // Update password
        updatePassword();

        // Update email
        updateEmail();

        // Update Bio
        updateBio();

        // Update City
        updateCity();

        // Update Country
        updateCountry();

        // Update Image
        updateImageUrl();

        // Update Image
        updateImageUrl();
    }

    private void updateName() {
        if (!fullName.equals(edit_name.getText().toString())) {
            reference.child(username).child("name").setValue(edit_name.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            fullName = edit_name.getText().toString();
                            updateSharedPreferences("fullname", edit_name.getText().toString());
                        } else {
                            // Handle the error
                            Toast.makeText(EditProfileActivity.this, "Error updating name", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updatePhone() {
        if (!phone.equals(edit_phone.getText().toString())) {
            reference.child(username).child("phone").setValue(edit_phone.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            phone = edit_phone.getText().toString();
                            updateSharedPreferences("phone", edit_phone.getText().toString());
                        } else {
                            // Handle the error
                            Toast.makeText(EditProfileActivity.this, "Error updating phone", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updatePassword() {
        if (!password.equals(edit_password.getText().toString())) {
            reference.child(username).child("password").setValue(edit_password.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            password = edit_password.getText().toString();
                            updateSharedPreferences("password", edit_password.getText().toString());
                        } else {
                            // Handle the error
                            Toast.makeText(EditProfileActivity.this, "Error updating password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateEmail() {
        if (!email.equals(edit_email.getText().toString())) {
            reference.child(username).child("email").setValue(edit_email.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            email = edit_email.getText().toString();
                            updateSharedPreferences("email", edit_email.getText().toString());
                        } else {
                            // Handle the error
                            Toast.makeText(EditProfileActivity.this, "Error updating email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateImageUrl() {

        SharedPreferences sharedPreferences = getSharedPreferences("imagedata", MODE_PRIVATE);
        String newImageUrl = sharedPreferences.getString("image", "");

        if (!newImageUrl.equals(imageUrl)) {
            reference.child(username).child("imageurl").setValue(newImageUrl)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            imageUrl = newImageUrl;
                            updateSharedPreferences("imageurl", newImageUrl);
                        } else {
                            // Handle the error
                            Toast.makeText(EditProfileActivity.this, "Error updating image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void updateBio() {
        if (!aboutbio.equals(edit_bio.getText().toString())) {
            reference.child(username).child("bio").setValue(edit_bio.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            aboutbio = edit_bio.getText().toString();
                            updateSharedPreferences("bio", edit_bio.getText().toString());
                        } else {
                            // Handle the error
                            Toast.makeText(EditProfileActivity.this, "Error updating bio", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateCountry() {
        String selectedCountry = spinnerCountry.getSelectedItem().toString();

        if (!country.equals(selectedCountry)) {
            reference.child(username).child("country").setValue(selectedCountry)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            country = selectedCountry;
                            updateSharedPreferences("country", selectedCountry);
                        } else {
                            // Handle the error
                            Toast.makeText(EditProfileActivity.this, "Error updating country", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateCity() {
        String selectedCity = spinnerCity.getSelectedItem().toString();

        if (!city.equals(selectedCity)) {
            reference.child(username).child("city").setValue(selectedCity)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            city = selectedCity;
                            updateSharedPreferences("city", selectedCity);
                        } else {
                            // Handle the error
                            Toast.makeText(EditProfileActivity.this, "Error updating city", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
                                    String imageUrl = downloadUri.toString();

                                    SharedPreferences sharedPreferences = getSharedPreferences("imagedata", MODE_PRIVATE);
                                    SharedPreferences.Editor editors = sharedPreferences.edit();
                                    editors.putString("image", imageUrl);
                                    editors.apply();

                                    // Update the imageUrl field in SigninModel
                                    updateImageUrlInModel(username, imageUrl);

                                    // Save the imageUrl to the database directly
                                    reference.child(username).child("imageurl").setValue(imageUrl);

                                    Toast.makeText(EditProfileActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        userRef.child("imageurl").setValue(imageUrl);
    }

    // Add this method to retrieve the username from SharedPreferences
    private String getUsernameFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("userdetails", MODE_PRIVATE);
        return sharedPreferences.getString("username", null);
    }

    private void updateSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferencess = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor editors = sharedPreferencess.edit();
        editors.putString(key, value);
        editors.apply();
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
                                EditProfileActivity.this,
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
                        Toast.makeText(EditProfileActivity.this, "Failed to load cities", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
