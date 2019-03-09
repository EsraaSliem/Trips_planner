package iti.jets.tripplanner.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import iti.jets.tripplanner.fragments.HistoryCancelledFragment;
import iti.jets.tripplanner.fragments.HistoryDoneFragment;

public class HistoryViewPagerAdapter extends FragmentPagerAdapter {

    public HistoryViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new HistoryDoneFragment();
        } else if (position == 1) {
            fragment = new HistoryCancelledFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Tab-1";
        } else if (position == 1) {
            title = "Tab-2";
        }
        return title;
    }
}

