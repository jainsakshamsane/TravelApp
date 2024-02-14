package com.travelapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.nio.charset.StandardCharsets;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout linear9, linear7;
    TextView name, emailid, logout;
    ImageView back, profilephoto;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        name = findViewById(R.id.name);
        emailid = findViewById(R.id.email);
        back = findViewById(R.id.back);
        profilephoto = findViewById(R.id.profilephoto);
        logout = findViewById(R.id.logout);
        linear9 = findViewById(R.id.linear9);
        linear7 = findViewById(R.id.linear7);

        logout.setOnClickListener(v -> logoutUser());

        SharedPreferences sharedPreferencess = getSharedPreferences("userdetails", MODE_PRIVATE);
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

        name.setText(fullName);
        emailid.setText(email);
        Picasso.get().load(imageUrl).into(profilephoto);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        linear9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, PaymentMethodActivity.class);
                startActivity(intent);
            }
        });

        linear7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, UpcomingTourActivity.class);
                startActivity(intent);
            }
        });
    }
    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> logout());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all preferences, including the switch state
        editor.clear();
        editor.apply();

        // Navigate to the login page
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        Toast.makeText(SettingsActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }
}
