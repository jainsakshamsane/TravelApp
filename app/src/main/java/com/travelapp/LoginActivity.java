package com.travelapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText username, loginpassword;
    TextView loginbutton, registertext;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        username = findViewById(R.id.username);
        loginpassword = findViewById(R.id.password);
        loginbutton = findViewById(R.id.loginbutton);
        registertext = findViewById(R.id.newuser);
        ImageView togglePassword = findViewById(R.id.togglePassword);

        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                int inputType = (loginpassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ?
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD :
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

                loginpassword.setInputType(inputType);
                // Move cursor to the end of the text
                loginpassword.setSelection(loginpassword.getText().length());

                // Change the visibility toggle icon
                togglePassword.setImageResource(
                        (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ?
                                R.drawable.visible :
                                R.drawable.hide
                );
            }
        });

        registertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {

                } else {
                    checkUser();
                }
            }
        });
    }

    public Boolean validateUsername () {
        String val = username.getText().toString();
        if (val.isEmpty()) {
            username.setError("name cannot be empty");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    public Boolean validatePassword () {
        String val = loginpassword.getText().toString();
        if (val.isEmpty()) {
            loginpassword.setError("Password cannot be empty");
            return false;
        } else {
            loginpassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String useruserName = username.getText().toString().trim();
        String userPassword = loginpassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(useruserName);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    username.setError(null);
                    String passwordFromDB = snapshot.child(useruserName).child("password").getValue(String.class);

                    if (passwordFromDB.equals(userPassword)) {
                        username.setError(null);

                        String nameFromDB = snapshot.child(useruserName).child("name").getValue(String.class);
                        String userFromDB = snapshot.child(useruserName).child("username").getValue(String.class);
                        String emailFromDB = snapshot.child(useruserName).child("email").getValue(String.class);
                        String cityFromDB = snapshot.child(useruserName).child("city").getValue(String.class);
                        String countryFromDB = snapshot.child(useruserName).child("country").getValue(String.class);
                        String passFromDB = snapshot.child(useruserName).child("password").getValue(String.class);
                        String phoneFromDB = snapshot.child(useruserName).child("phone").getValue(String.class);
                        String imageFromDB = snapshot.child(useruserName).child("imageurl").getValue(String.class);
                        String useridfromDB = snapshot.child(useruserName).child("userid").getValue(String.class);
                        String bioFromDB = snapshot.child(useruserName).child("bio").getValue(String.class);
                        String timestampFromDB = snapshot.child(useruserName).child("timestamp").getValue(String.class);

                        SharedPreferences sharedPreferencess = getSharedPreferences("userdetails", MODE_PRIVATE);
                        SharedPreferences.Editor editors = sharedPreferencess.edit();
                        editors.putString("fullname", nameFromDB);
                        editors.putString("email", emailFromDB);
                        editors.putString("username", userFromDB);
                        editors.putString("city", cityFromDB);
                        editors.putString("country", countryFromDB);
                        editors.putString("password", passFromDB);
                        editors.putString("phone", phoneFromDB);
                        editors.putString("imageurl", imageFromDB);
                        editors.putString("userid", useridfromDB);
                        editors.putString("bio", bioFromDB);
                        editors.putString("timestamp", timestampFromDB);

                        editors.apply();

                        username.setText("");
                        loginpassword.setText("");

                        Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("fullname", nameFromDB);
                        Log.e("naaaam", nameFromDB);
                        intent.putExtra("city", cityFromDB);
                        intent.putExtra("username", userFromDB);
                        startActivity(intent);

                        // Set the login status in shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                    } else {
                        loginpassword.setError("Invalid Credentials");
                        loginpassword.requestFocus();
                    }
                } else {
                    username.setError("User does not exist");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
