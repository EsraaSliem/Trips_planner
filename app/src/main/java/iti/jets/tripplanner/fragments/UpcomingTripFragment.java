package iti.jets.tripplanner.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.adapters.TripAdapter;
import iti.jets.tripplanner.pojos.Trip;

public class UpcomingTripFragment extends Fragment {

    ImageButton addNoteTrip;
    RecyclerView tripRecyclerView;
    Button editBtn, removeBtn;

    public UpcomingTripFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_trip, container, false);
        tripRecyclerView = view.findViewById(R.id.recyclerView_upcomingTrip);
        List<Trip> tripList = new ArrayList<>();
        tripList.add(new Trip("1", "cairo", "14-5-2019", "10pm", "smart", "dreampark", 1, 1));
        tripList.add(new Trip("1", "cairo", "14-5-2019", "10pm", "smart", "dreampark", 1, 1));
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TripAdapter adapter = new TripAdapter(getContext(), tripList);

        tripRecyclerView.setItemAnimator(new DefaultItemAnimator());
        tripRecyclerView.setAdapter(adapter);

        addNoteTrip = view.findViewById(R.id.imgBtn_addNote_upcomingTripCard);
        editBtn = view.findViewById(R.id.editTripBtn_upcomingTripCard);
        removeBtn = view.findViewById(R.id.deleteTripBtn_upcomingTripCard);

        return view;
    }


}
