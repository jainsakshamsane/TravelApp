package com.travelapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Discover_Activity extends AppCompatActivity {

    private TextView India, Usa, France, Italy;
    ImageView leftIcon;
    private TextView Summer, Rainy, Winter, Spring;
    private TextView lastClickedCountryTextView; // Keep track of the last clicked country TextView
    private TextView lastClickedSeasonTextView;  // Keep track of the last clicked season TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_activity);

        // Initialize the TextViews
        lastClickedCountryTextView = findViewById(R.id.text1); // Assign any initial TextView for country
        lastClickedSeasonTextView = findViewById(R.id.text5); // Assign any initial TextView for season

        India = findViewById(R.id.text1);
        leftIcon = findViewById(R.id.leftIcon);
        Usa = findViewById(R.id.text2);
        France = findViewById(R.id.text3);
        Italy = findViewById(R.id.text4);
        Summer = findViewById(R.id.text5);
        Rainy = findViewById(R.id.text6);
        Winter = findViewById(R.id.text7);
        Spring = findViewById(R.id.text8);

        // Set click listeners for country TextViews
        India.setOnClickListener(createOnClickListener("India", null));
        Usa.setOnClickListener(createOnClickListener("USA", null));
        France.setOnClickListener(createOnClickListener("France", null));
        Italy.setOnClickListener(createOnClickListener("Italy", null));

        // Set click listeners for season TextViews
        Summer.setOnClickListener(createOnClickListener(null, "Summer"));
        Rainy.setOnClickListener(createOnClickListener(null, "Rainy"));
        Winter.setOnClickListener(createOnClickListener(null, "Winter"));
        Spring.setOnClickListener(createOnClickListener(null, "Spring"));

//        leftIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start MainActivity to navigate back to Main_Fragment
//                Intent intent = new Intent(Discover_Activity.this, Main_Fragment.class);
//                startActivity(intent);
//            }
//        });
    }

    private View.OnClickListener createOnClickListener(final String country, final String season) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDiscoverByPlace(country, season);
                if (country != null) {
                    changeBackgroundColor(lastClickedCountryTextView, false);
                    lastClickedCountryTextView = (TextView) v;
                } else {
                    changeBackgroundColor(lastClickedSeasonTextView, false); // Reset the color of the previous season TextView
                    lastClickedSeasonTextView = (TextView) v;
                }
                changeBackgroundColor((TextView) v, true);
            }
        };
    }

    private void openDiscoverByPlace(String country, String season) {
        Intent intent = new Intent(Discover_Activity.this, Discover_byPlace_Activity.class);
        intent.putExtra("country", country);
        intent.putExtra("season", season);
        startActivity(intent);
    }

    private void changeBackgroundColor(TextView textView, boolean selected) {
        Drawable background;
        if (selected) {
            background = getResources().getDrawable(R.drawable.unselcted_rectagle_background); // Change to your custom drawable for selected state
        } else {
            background = getResources().getDrawable(R.drawable.selected_backround); // Change to your custom drawable for unselected state
        }
        textView.setBackground(background);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset the background color when returning from the next activity
        if (lastClickedCountryTextView != null) {
            changeBackgroundColor(lastClickedCountryTextView, false);
        }
        if (lastClickedSeasonTextView != null) {
            changeBackgroundColor(lastClickedSeasonTextView, false);
        }
    }
}
