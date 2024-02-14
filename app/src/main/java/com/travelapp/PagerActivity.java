package com.travelapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.travelapp.Adapters.SectionsPagerAdapter;
import com.travelapp.LoginActivity;

public class PagerActivity extends AppCompatActivity {

    ImageView previous, next;
    ViewPager viewPager;
    SectionsPagerAdapter sectionsPagerAdapter;
    TabLayout tabs;

    private static final long AUTO_SWIPE_INTERVAL = 1500; // in milliseconds
    private Handler autoSwipeHandler;
    private Runnable autoSwipeRunnable;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_activity);

        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        viewPager = findViewById(R.id.view_pager);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Set custom views (images) for each tab
        tabs.getTabAt(0).setCustomView(R.layout.custom_tab_layout_0);
        tabs.getTabAt(1).setCustomView(R.layout.custom_tab_layout_1);
        tabs.getTabAt(2).setCustomView(R.layout.custom_tab_layout_2);

        // Set initial tab
        viewPager.setCurrentItem(0);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentTab = viewPager.getCurrentItem();
                if (currentTab > 0) {
                    viewPager.setCurrentItem(currentTab - 1);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentTab = viewPager.getCurrentItem();
                int lastTab = sectionsPagerAdapter.getCount() - 1;

                if (currentTab < lastTab) {
                    viewPager.setCurrentItem(currentTab + 1);
                } else {
                    // If it's the last tab, navigate to the next action (LoginActivity)
                    Intent intent = new Intent(PagerActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Initialize auto-swipe functionality
        autoSwipeHandler = new Handler();
        autoSwipeRunnable = new Runnable() {
            @Override
            public void run() {
                int currentTab = viewPager.getCurrentItem();
                int lastTab = sectionsPagerAdapter.getCount() - 1;

                if (currentTab < lastTab) {
                    viewPager.setCurrentItem(currentTab + 1);
                    autoSwipeHandler.postDelayed(this, AUTO_SWIPE_INTERVAL);
                } else {
                    // If it's the last tab, navigate to the next action (LoginActivity)
                    Intent intent = new Intent(PagerActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

        // Start auto-swipe on create
        startAutoSwipe();
    }

    private void startAutoSwipe() {
        autoSwipeHandler.postDelayed(autoSwipeRunnable, AUTO_SWIPE_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to avoid memory leaks
        autoSwipeHandler.removeCallbacks(autoSwipeRunnable);
    }
}
