package com.travelapp;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.travelapp.Models.Card;
import com.travelapp.Models.Payment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class payment_activity extends AppCompatActivity {
    TextView price, totalPrice;
    EditText cardNumber, expiryDate, cvv;
    String userId, placeName, numberOfPeople; // You need to retrieve these values from SharedPreferences


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_method_activity);

        // Initialize views
        price = findViewById(R.id.price);
        totalPrice = findViewById(R.id.totalprice);
        cardNumber = findViewById(R.id.card_number);
        expiryDate = findViewById(R.id.exparedate);
        cvv = findViewById(R.id.cvv);

        // Retrieve data from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Set price from the previous activity
            String priceValue = extras.getString("price");
            price.setText(priceValue);

            // You can set total price based on the received price or any other calculation logic
            // For now, let's just set it to the same as price
            totalPrice.setText(priceValue);

            // Retrieve additional data from extras
            placeName = extras.getString("placeName");
            numberOfPeople = extras.getString("numberOfPeople");
            Log.d("price", "Passing price: " + price + ", number of people: " + numberOfPeople+", placeName: " + placeName);

        }

        // Retrieve userId from SharedPreferences
        userId = getSharedPreferences("userdetails", MODE_PRIVATE).getString("userid", "");

        // Save card details to Firebase when user clicks proceed
        findViewById(R.id.proceed).setOnClickListener(v -> {
            if (validateCardDetails()) {
                saveCardDetails();
                savePaymentDetails();
            }
        });
    }


    private boolean validateCardDetails() {
        String cardNumberValue = cardNumber.getText().toString().trim();
        String expiryDateValue = expiryDate.getText().toString().trim();
        String cvvValue = cvv.getText().toString().trim();

        if (cardNumberValue.length() != 16) {
            cardNumber.setError("Card number must be 16 digits");
            cardNumber.requestFocus();
            return false;
        }

        if (!expiryDateValue.matches("\\d{2}/\\d{2}")) {
            expiryDate.setError("Invalid expiry date format (MM/YY)");
            expiryDate.requestFocus();
            return false;
        }

        if (cvvValue.length() != 3) {
            cvv.setError("CVV must be 3 digits");
            cvv.requestFocus();
            return false;
        }

        return true;
    }

    private void saveCardDetails() {
        String cardNumberValue = cardNumber.getText().toString().trim();
        String expiryDateValue = expiryDate.getText().toString().trim();
        String cvvValue = cvv.getText().toString().trim();
        String totalprice = totalPrice.getText().toString().trim();

        Card card = new Card();
        card.setUserid(userId); // Set the userId
        card.setCard_number(cardNumberValue);
        card.setExpiry_date(expiryDateValue);
        card.setTotal_price(totalprice);
        card.setCvv(cvvValue);
        card.setPeople(numberOfPeople);

        DatabaseReference cardsRef = FirebaseDatabase.getInstance().getReference("cards");
        String cardId = cardsRef.push().getKey();
        card.setCard_id(cardId);

        cardsRef.child(cardId).setValue(card)
                .addOnSuccessListener(aVoid -> {
                    // Card added successfully
                    Toast.makeText(payment_activity.this, "Card added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to add card, handle error if needed
                    Toast.makeText(payment_activity.this, "Failed to add card: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void savePaymentDetails() {
        String cardNumberValue = cardNumber.getText().toString().trim();

        // Get a reference to the "cards" node in Firebase
        DatabaseReference cardsRef = FirebaseDatabase.getInstance().getReference("cards");

        // Query to find the card with the given card number
        Query query = cardsRef.orderByChild("card_number").equalTo(cardNumberValue);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the card ID from the snapshot
                    String cardId = dataSnapshot.getChildren().iterator().next().getKey();

                    // Create a new payment object
                    Payment payment = new Payment();
                    payment.setUserId(userId);
                    payment.setPlaceName(placeName);
                    payment.setNumberOfPeople(String.valueOf(numberOfPeople)); // Convert int to string
                    payment.setCard_id(cardId);

                    // Get current date and time
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy - HH-mm", Locale.getDefault());
                    String currentTime = sdf.format(new Date());

                    // Set payment time
                    payment.setTime(currentTime);

                    // Get a reference to the "payments" node in Firebase
                    DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payments");
                    String paymentId = paymentRef.push().getKey();
                    payment.setPaymentId(paymentId);

                    // Save the payment to Firebase
                    paymentRef.child(paymentId).setValue(payment)
                            .addOnSuccessListener(aVoid -> {
                                // Payment added successfully
                                new SweetAlertDialog(payment_activity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Payment Successful!")
                                        .setContentText("Your payment was successful.")
                                        .setConfirmClickListener(sDialog -> {
                                            sDialog.dismissWithAnimation();
                                            finish(); // Close the activity after successful payment
                                        })
                                        .show();
                            })
                            .addOnFailureListener(e -> {
                                // Failed to add payment, handle error if needed
                                Toast.makeText(payment_activity.this, "Failed to add payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Card with the given card number not found
                    Toast.makeText(payment_activity.this, "Card not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(payment_activity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





}
