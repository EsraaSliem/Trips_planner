package iti.jets.tripplanner.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iti.jets.tripplanner.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryCancelledFragment extends Fragment {

    RecyclerView tripRecyclerView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_cancelled, container, false);
        context = getActivity();
        tripRecyclerView = view.findViewById(R.id.historyCancelledTrip_recyclerView);

        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tripRecyclerView.setItemAnimator(new DefaultItemAnimator());

//       fireBaseData.getTrips(tripRecyclerView, Trip.STATUS_CANCELLED);
        return view;
    }
}
