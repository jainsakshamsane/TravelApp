package com.travelapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

public class Details_Activity extends AppCompatActivity {
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5;
    TextView text1, text2, text3, text4;
    Button proceed;
    String price;
    String numberOfPeople = "";
    ImageView iconImageView;
    private boolean isSaved = false;
    DatabaseReference saveReference;
    String id,name;
    String image;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        Intent intent = getIntent();
        String placeName = intent.getStringExtra("placeName");
        saveReference = FirebaseDatabase.getInstance().getReference("save").child(placeName);

        iconImageView = findViewById(R.id.iconImageView);
        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSaveState(placeName);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("places");
        Query query = databaseReference.orderByChild("name").equalTo(placeName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                         name = snapshot.child("name").getValue(String.class);
                        String location = snapshot.child("city").getValue(String.class) + ", " + snapshot.child("country").getValue(String.class);
                        price = snapshot.child("price").getValue(String.class);

                        String information = snapshot.child("information").getValue(String.class);
                        String Service = snapshot.child("service").getValue(String.class);
                        String temp = snapshot.child("temprature").getValue(String.class);
                        id = snapshot.child("id").getValue(String.class);
                        image = snapshot.child("image").getValue(String.class);
                        ImageView backgroundImageView = findViewById(R.id.backgroundImageView);
                        TextView NametextView1 = findViewById(R.id.NametextView1);
                        TextView LocationtextView2 = findViewById(R.id.LocationtextView2);
                        TextView PricetextView3 = findViewById(R.id.PricetextView3);
                        TextView Informationtextview = findViewById(R.id.Informationtextview);
                        TextView servicetextview = findViewById(R.id.servicetextview);
                        TextView rightTextView = findViewById(R.id.rightTextView);

                        NametextView1.setText(name);
                        LocationtextView2.setText(location);
                        PricetextView3.setText(price);
                        Informationtextview.setText(information);
                        servicetextview.setText(Service);
                        rightTextView.setText(temp);

                        Picasso.get().load(image).into(backgroundImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Details_Activity", "Error querying database: " + databaseError.getMessage());
            }
        });

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView1.setImageResource(R.drawable.sunorange);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView2.setImageResource(R.drawable.areoplaneeee);
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView3.setImageResource(R.drawable.boatorange);
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView4.setImageResource(R.drawable.busorange);
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImages();
                imageView5.setImageResource(R.drawable.bikeorange);
            }
        });

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);

        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfPeople = String.valueOf(1);
                resetBackgrounds();
                text1.setBackgroundResource(R.drawable.unselcted_rectagle_background);
            }
        });

        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfPeople = String.valueOf(2);
                resetBackgrounds();
                text2.setBackgroundResource(R.drawable.unselcted_rectagle_background);
            }
        });

        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfPeople = String.valueOf(3);
                resetBackgrounds();
                text3.setBackgroundResource(R.drawable.unselcted_rectagle_background);
            }
        });

        text4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfPeople = String.valueOf(10);
                resetBackgrounds();
                text4.setBackgroundResource(R.drawable.unselcted_rectagle_background);
            }
        });

        proceed = findViewById(R.id.proceed);
        proceed = findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfPeople.isEmpty()) {
                    Toast.makeText(Details_Activity.this, "Please select the number of people", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("price", "Passing price: " + price + ", number of people: " + numberOfPeople + ", placeName: " + placeName);
                    Intent intent = new Intent(Details_Activity.this, payment_activity.class);
                    intent.putExtra("price", price);
                    intent.putExtra("numberOfPeople", numberOfPeople);
                    intent.putExtra("placeName", placeName);
                    intent.putExtra("placeId", id);
                    intent.putExtra("name", name);
                    intent.putExtra("image", image);
                    startActivity(intent);
                }
            }
        });
    }


    private void resetImages() {
        imageView1.setImageResource(R.drawable.sun);
        imageView2.setImageResource(R.drawable.areoplanewhite);
        imageView3.setImageResource(R.drawable.boattt);
        imageView4.setImageResource(R.drawable.busss);
        imageView5.setImageResource(R.drawable.bike);
    }

    private void resetBackgrounds() {
        text1.setBackgroundResource(R.drawable.selected_backround);
        text2.setBackgroundResource(R.drawable.selected_backround);
        text3.setBackgroundResource(R.drawable.selected_backround);
        text4.setBackgroundResource(R.drawable.selected_backround);
    }

    private void toggleSaveState(String placeName) {
        DatabaseReference saveRef = FirebaseDatabase.getInstance().getReference("save").child(placeName);
        saveRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    saveRef.removeValue();
                    iconImageView.setImageResource(R.drawable.saveunslect);
                    Toast.makeText(Details_Activity.this, "Removed from saved", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("places");
                    Query query = databaseReference.orderByChild("name").equalTo(placeName);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String name = snapshot.child("name").getValue(String.class);
                                    String location = snapshot.child("city").getValue(String.class) + ", " + snapshot.child("country").getValue(String.class);
                                    price = snapshot.child("price").getValue(String.class);
                                    String image = snapshot.child("image").getValue(String.class);
                                    String information = snapshot.child("information").getValue(String.class);
                                    String Service = snapshot.child("service").getValue(String.class);
                                    String temp = snapshot.child("temprature").getValue(String.class);
                                    String season = snapshot.child("season").getValue(String.class);
                                    String no_of_days = snapshot.child("no_of_days").getValue(String.class);

                                    String userId = getSharedPreferences("userdetails", MODE_PRIVATE).getString("userid", "");

                                    saveRef.child("name").setValue(name);
                                    saveRef.child("location").setValue(location);
                                    saveRef.child("price").setValue(price);
                                    saveRef.child("image").setValue(image);
                                    saveRef.child("information").setValue(information);
                                    saveRef.child("service").setValue(Service);
                                    saveRef.child("temp").setValue(temp);
                                    saveRef.child("userId").setValue(userId);
                                    saveRef.child("season").setValue(season);
                                    saveRef.child("no_of_days").setValue(no_of_days);
                                }
                                iconImageView.setImageResource(R.drawable.saveslect);
                                Toast.makeText(Details_Activity.this, "Added to saved", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Details_Activity", "Error querying database: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Details_Activity", "Error querying database: " + databaseError.getMessage());
            }
        });
    }
}
