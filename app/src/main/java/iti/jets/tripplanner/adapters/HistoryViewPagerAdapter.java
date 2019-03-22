package iti.jets.tripplanner.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import iti.jets.tripplanner.fragments.HistoryCancelledFragment;
import iti.jets.tripplanner.fragments.HistoryDoneFragment;
import iti.jets.tripplanner.fragments.HistoryMapFragment;

public class HistoryViewPagerAdapter extends FragmentPagerAdapter {
    Fragment fragment;

    public HistoryViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                fragment = new HistoryDoneFragment();
                break;
            case 1:
                fragment = new HistoryMapFragment();
                break;
            case 2:
                fragment = new HistoryCancelledFragment();
                break;
            default:
                Log.e("TAG", "getItem: Not Found Fragment");
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = "Done";
                break;
            case 1:
                title = "Map";
                break;
            case 2:
                title = "Cancelled";
                break;
            default:
                Log.e("TAG", "getItem: Not Found Fragment");
                break;
        }
        return title;
    }
}

