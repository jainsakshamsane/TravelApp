package com.travelapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Month_fliter_Activity extends AppCompatActivity {
    TextView Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec;
    TextView lastClickedMonthTextView;
    ImageView leftIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_fliter_activity);


        // Initialize TextViews for each month
        Jan = findViewById(R.id.text1);
        Feb = findViewById(R.id.text2);
        Mar = findViewById(R.id.text3);
        Apr = findViewById(R.id.text4);
        May = findViewById(R.id.text5);
        Jun = findViewById(R.id.text6);
        Jul = findViewById(R.id.text7);
        Aug = findViewById(R.id.text8);
        Sep = findViewById(R.id.text9);
        Oct = findViewById(R.id.text10);
        Nov = findViewById(R.id.text11);
        Dec = findViewById(R.id.text12);
        leftIcon = findViewById(R.id.leftIcon);

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity to navigate back to Main_Fragment
                Intent intent = new Intent(Month_fliter_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set click listeners for each month TextView to filter places and display them in the next activity
        Jan.setOnClickListener(createOnClickListener(Jan, "January"));
        Feb.setOnClickListener(createOnClickListener(Feb, "February"));
        Mar.setOnClickListener(createOnClickListener(Mar, "March"));
        Apr.setOnClickListener(createOnClickListener(Apr, "April"));
        May.setOnClickListener(createOnClickListener(May, "May"));
        Jun.setOnClickListener(createOnClickListener(Jun, "June"));
        Jul.setOnClickListener(createOnClickListener(Jul, "July"));
        Aug.setOnClickListener(createOnClickListener(Aug, "August"));
        Sep.setOnClickListener(createOnClickListener(Sep, "September"));
        Oct.setOnClickListener(createOnClickListener(Oct, "October"));
        Nov.setOnClickListener(createOnClickListener(Nov, "November"));
        Dec.setOnClickListener(createOnClickListener(Dec, "December"));
    }

    private View.OnClickListener createOnClickListener(final TextView textView, final String month) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if lastClickedMonthTextView is not null and revert its background color
                if (lastClickedMonthTextView != null) {
                    changeBackgroundColor(lastClickedMonthTextView, false);
                }

                // Set background color of the clicked TextView
                changeBackgroundColor(textView, true);
                lastClickedMonthTextView = textView; // Update lastClickedMonthTextView

                openMonthFilteredPlaces(month);
            }
        };
    }

    private void openMonthFilteredPlaces(String month) {
        // Pass the selected month to the next activity
        Intent intent = new Intent(Month_fliter_Activity.this, Each_Month_Fliter_Activity.class);
        intent.putExtra("selected_month", month);
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
        if (lastClickedMonthTextView != null) {
            changeBackgroundColor(lastClickedMonthTextView, false);
        }
    }
}
