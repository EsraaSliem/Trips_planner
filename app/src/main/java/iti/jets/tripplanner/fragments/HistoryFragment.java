package iti.jets.tripplanner.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.adapters.HistoryViewPagerAdapter;

public class HistoryFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    HistoryViewPagerAdapter viewPagerAdapter;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        context = getActivity();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabs);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("TAG", "onStart : history fragment");
        viewPagerAdapter = new HistoryViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
