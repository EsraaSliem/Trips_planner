package iti.jets.tripplanner.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iti.jets.tripplanner.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTripsFragment extends Fragment {

    public ShowTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_trips, container, false);
        return view;
    }
}
