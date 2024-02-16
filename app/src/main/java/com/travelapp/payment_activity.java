package com.travelapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;
import com.travelapp.Models.Card;
import com.travelapp.Models.Payment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class payment_activity extends AppCompatActivity {
    TextView price, totalPrice;
    TextView placename;
    EditText cardNumber, expiryDate, cvv;
    String userId, placeName, numberOfPeople,id; // You need to retrieve these values from SharedPreferences



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
        cardNumber.addTextChangedListener(new CardNumberTextWatcher());

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
            id = extras.getString("placeId");
            String imageUrl = extras.getString("image");

            // Find the ImageView in your layout
            ImageView imageView = findViewById(R.id.image);

            // Load the image using Picasso
            Picasso.get().load(imageUrl).into(imageView);

            // Set the place name to a TextView
            placename = findViewById(R.id.placename);
            placename.setText(placeName);

            numberOfPeople = extras.getString("numberOfPeople");
            Log.d("price", "Passing price: " + price + ", number of people: " + numberOfPeople + ", placeName: " + placeName);
        }
        userId = getSharedPreferences("userdetails", MODE_PRIVATE).getString("userid", "");

        // Save card details to Firebase when user clicks proceed
        findViewById(R.id.proceed).setOnClickListener(v -> {
            if (validateCardDetails()) {
                saveCardDetails();
                savePaymentDetails();
            }
        });
    }



    private class CardNumberTextWatcher implements TextWatcher {
        private static final int CARD_NUMBER_GROUP_SIZE = 4;
        private static final char CARD_NUMBER_SEPARATOR = ' ';

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No implementation needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // No implementation needed
        }

        @Override
        public void afterTextChanged(Editable s) {
            String input = s.toString().replaceAll(String.valueOf(CARD_NUMBER_SEPARATOR), "");

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                formatted.append(input.charAt(i));
                if ((i + 1) % CARD_NUMBER_GROUP_SIZE == 0 && i != input.length() - 1) {
                    formatted.append(CARD_NUMBER_SEPARATOR);
                }
            }

            cardNumber.removeTextChangedListener(this);
            cardNumber.setText(formatted.toString());
            cardNumber.setSelection(formatted.length());
            cardNumber.addTextChangedListener(this);
        }
    }
    private boolean validateCardDetails() {
        String cardNumberValue = cardNumber.getText().toString().trim().replaceAll("\\s", ""); // Remove spaces
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
                    payment.setId(id);
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
