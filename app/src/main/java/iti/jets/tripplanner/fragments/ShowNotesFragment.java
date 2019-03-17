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
import iti.jets.tripplanner.interfaces.ObjectCarrier;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.FireBaseData;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowNotesFragment extends Fragment implements ObjectCarrier {

    Trip trip;
    private RecyclerView recyclerView;
    private Context context;

    public ShowNotesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_notes, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        context = getActivity();
        FireBaseData fireBaseData = new FireBaseData(context);
        fireBaseData.getNotes(recyclerView, trip);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void sendTripId(Trip trip) {
        this.trip = new Trip();
        this.trip.setTripId(trip.getTripId());
    }
}
