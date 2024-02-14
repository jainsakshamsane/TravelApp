package com.travelapp.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.travelapp.Screen1Activity;
import com.travelapp.Screen2Activity;
import com.travelapp.Screen3Activity;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // Return the appropriate Fragment for each tab position
        switch (position) {
            case 0:
                return new Screen1Activity();
            case 1:
                return new Screen2Activity();
            case 2:
                return new Screen3Activity();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Number of tabs
        return 3;
    }
}
