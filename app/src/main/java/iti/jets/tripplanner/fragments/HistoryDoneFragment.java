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
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.FireBaseData;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryDoneFragment extends Fragment {

    RecyclerView tripRecyclerView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_done, container, false);
        context = getActivity();
        tripRecyclerView = view.findViewById(R.id.historyDoneTrip_recyclerView);

        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tripRecyclerView.setItemAnimator(new DefaultItemAnimator());

        FireBaseData fireBaseData = new FireBaseData(context);
        fireBaseData.getTrips(tripRecyclerView, Trip.STATUS_DONE);
        return view;
    }
}
