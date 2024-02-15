package com.travelapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.travelapp.Adapters.DebitCardsAdapter;
import com.travelapp.Adapters.HistoryAdapter;
import com.travelapp.Models.CardModel;
import com.travelapp.Models.PlaceModel;
import com.travelapp.Models.PlacesModel;
import com.travelapp.Models.TransactionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class By_Payment_Method_Activity extends AppCompatActivity {

    RecyclerView recyclerView, recyclerView12;
    LinearLayout linear01, linear5;
    ImageView back;
    DebitCardsAdapter adapter; // Declare the adapter as a field
    // Declare an ArrayList to store user names
    List<CardModel> cardList = new ArrayList<>();

    HistoryAdapter paymentHistoryAdapter;
    List<PlacesModel> placeModelList = new ArrayList<>();
    List<TransactionModel> paymentList = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardhistory_activity);

        back = findViewById(R.id.back);
        linear01 = findViewById(R.id.linear01);
        linear5 = findViewById(R.id.linear5);

        linear01.setVisibility(View.GONE);
        linear5.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(By_Payment_Method_Activity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        recyclerView12 = findViewById(R.id.recyclerView12);
        recyclerView12.setLayoutManager(new LinearLayoutManager(By_Payment_Method_Activity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView12.setHasFixedSize(true);

        DatabaseReference cardsRef = FirebaseDatabase.getInstance().getReference("cards");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        adapter = new DebitCardsAdapter(cardList, this); // Initialize the adapter once

        cardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cardsSnapshot) {
                cardList.clear();

                SharedPreferences sharedPreferencess = getSharedPreferences("userdetails", MODE_PRIVATE);
                String fullName = sharedPreferencess.getString("fullname", "");
                String userid = sharedPreferencess.getString("userid", "");

                for (DataSnapshot cardSnapshot : cardsSnapshot.getChildren()) {
                    String cardNumber = cardSnapshot.child("card_number").getValue(String.class);
                    String expiryDate = cardSnapshot.child("expiry_date").getValue(String.class);
                    String totalPrice = cardSnapshot.child("total_price").getValue(String.class);
                    String userId = cardSnapshot.child("userid").getValue(String.class);

                    if (userId.equals(userid)) {
                        CardModel card = new CardModel(cardNumber, expiryDate, totalPrice, userId);
                        cardList.add(card);

                    }
                }
                if(cardList != null){
                    recyclerView.setVisibility(View.VISIBLE);
                    linear01.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    linear01.setVisibility(View.VISIBLE);
                }
                // Set the adapter after cards data is retrieved
                recyclerView.setAdapter(adapter);

                // Fetch and process data from payments node
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot paymentsSnapshot) {
                        for (CardModel card : cardList) {
                            for (DataSnapshot paymentSnapshot : paymentsSnapshot.getChildren()) {
                                String userId = paymentSnapshot.child("userid").getValue(String.class);

                                // Link data from payments node based on card_id
                                if (card.getUserid().equals(userId)) {
                                    // Example: Fetch placeName from payments
                                    String userkaname = paymentSnapshot.child("name").getValue(String.class);
                                    Log.d("PaymentMethodActivity", "Linked Data - UserId: " + card.getUserid() + ", Name: " + userkaname);

                                    SharedPreferences sharedPreferencess = getSharedPreferences("userdetails", MODE_PRIVATE);
                                    String fullName = sharedPreferencess.getString("fullname", "");
                                    String userid = sharedPreferencess.getString("userid", "");

                                    if (userId.equals(userid)) {
                                        SharedPreferences sharedPreferencesss = getSharedPreferences("carddetails", MODE_PRIVATE);
                                        SharedPreferences.Editor editors = sharedPreferencesss.edit();
                                        editors.putString("name", userkaname);
                                        editors.apply();
                                    } else {
                                        Toast.makeText(By_Payment_Method_Activity.this, "No Debit Cards found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(By_Payment_Method_Activity.this, "Failed to retrieve payments", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(By_Payment_Method_Activity.this, "Failed to retrieve cards", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payments");
        DatabaseReference placeRef = FirebaseDatabase.getInstance().getReference("places");

        paymentHistoryAdapter = new HistoryAdapter(this, placeModelList);

        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cardsSnapshot) {
                paymentList.clear(); // Clear the list before adding new data

                for (DataSnapshot cardSnapshot : cardsSnapshot.getChildren()) {
                    String placename = cardSnapshot.child("placeName").getValue(String.class);
                    String userId = cardSnapshot.child("userId").getValue(String.class);
                    String date = cardSnapshot.child("time").getValue(String.class);

                    // Assuming you have a Payment class to represent the data
                    TransactionModel payment = new TransactionModel(placename, userId, date);
                    paymentList.add(payment);
                }

                // Fetch and process data from payments node
                placeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot paymentsSnapshot) {
                        for (TransactionModel payment : paymentList) {
                            for (DataSnapshot placeSnapshot : paymentsSnapshot.getChildren()) {
                                String nameofplace = placeSnapshot.child("name").getValue(String.class);

                                SharedPreferences sharedPreferencess = getSharedPreferences("userdetails", MODE_PRIVATE);
                                String userid = sharedPreferencess.getString("userid", "");

                                // Check if userId matches the currently logged-in user's userid
                                if (payment.getUserId().equals(userid)) {
                                    // Proceed to check the placeName condition
                                    if (payment.getPlaceName().equals(nameofplace)) {
                                        // Example: Fetch placeName from payments
                                            String placename = placeSnapshot.child("name").getValue(String.class);
                                            String city = placeSnapshot.child("city").getValue(String.class);
                                            String country = placeSnapshot.child("country").getValue(String.class);
                                            String price = placeSnapshot.child("price").getValue(String.class);
                                            String image = placeSnapshot.child("image").getValue(String.class);
                                            String idplace = placeSnapshot.child("id").getValue(String.class);
                                            Log.d("PlaceMethodActivity", "Linked Data - UserId: " + payment.getPlaceName() + ", Name: " + placename + city + country + price);

                                            PlacesModel placeModel = new PlacesModel(placename, city, country, price, image, idplace);
                                            placeModelList.add(placeModel);

                                            // Set the adapter after cards data is retrieved

                                    }
                                } else {
                                    recyclerView12.setVisibility(View.GONE);
                                    linear5.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        recyclerView12.setAdapter(paymentHistoryAdapter);
                        recyclerView12.setVisibility(View.VISIBLE);
                        linear5.setVisibility(View.GONE);
                        // Notify the adapter that the data has changed
                        paymentHistoryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(By_Payment_Method_Activity.this, "Failed to retrieve payments", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(By_Payment_Method_Activity.this, "Failed to retrieve places", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(By_Payment_Method_Activity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private String extractDate(String dateTime) {
        if (dateTime != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy - HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            try {
                Date date = inputFormat.parse(dateTime);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return ""; // Handle the parsing error according to your needs
            }
        } else {
            return ""; // or some default value if dateTime is null
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
