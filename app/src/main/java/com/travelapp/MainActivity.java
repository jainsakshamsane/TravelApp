package com.travelapp;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Set the default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Main_Fragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_item_1) {
                selectedFragment = new Main_Fragment();
            } else if (item.getItemId() == R.id.nav_item_2) {
                selectedFragment = new Explore_Fragment();
            } else if (item.getItemId() == R.id.nav_item_3) {
                selectedFragment = new Chat_Fragment();
            } else if (item.getItemId() == R.id.nav_item_4) {
                selectedFragment = new Location_Fragment();
            }else if (item.getItemId() == R.id.nav_item_5) {
                selectedFragment = new Profile_Fragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }

            return false;
        }
    };

}
